package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.redtea.util.Cluster;
import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.redtea.util.Pair;
import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.inject.PluginInjector;
import com.theredpixelteam.upm4j.loader.attribution.processor.Barrier;
import com.theredpixelteam.upm4j.loader.event.*;
import com.theredpixelteam.upm4j.loader.exception.*;
import com.theredpixelteam.upm4j.source.Source;
import com.theredpixelteam.upm4j.plugin.Plugin;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;
import com.theredpixelteam.upm4j.plugin.PluginNamespace;
import com.theredpixelteam.upm4j.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PluginConstructor {
    public PluginConstructor(@Nonnull UPMContext context,
                             @Nonnull Source source)
    {
        this.context = Objects.requireNonNull(context);

        this.source = Objects.requireNonNull(source, "source");
        this.classLoader = context.getClassLoaderProvider().provide(context);

        this.entryDiscoverer = context.getEntryDiscoverer();
        this.instancePolicy = context.getInstancePolicy();

        this.classLoader.addSource(source);
    }

    public @Nonnull Source getSource()
    {
        return source;
    }

    public @Nonnull PluginInstancePolicy getInstancePolicy()
    {
        return instancePolicy;
    }

    public @Nonnull PluginEntryDiscoverer getEntryDiscoverer()
    {
        return entryDiscoverer;
    }

    public @Nonnull PluginClassLoader getClassLoader()
    {
        return classLoader;
    }

    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public @Nonnull Stage getStage()
    {
        return stage;
    }

    private void clearState()
    {
        stage = Stage.INITIALIZED;
    }

    private void nextStage()
    {
        stage = stage.nextStage();
    }

    @SuppressWarnings("unchecked")
    public synchronized void construct()
            throws PluginMountingException
    {
        clearState();

        boolean single = !PluginInstancePolicy.MULTIPLE.equals(instancePolicy);
        boolean restricted = PluginInstancePolicy.RESTRICTED_SINGLE.equals(instancePolicy);

        Barrier barrier = single ? (restricted ? Barrier.barrier(2) : Barrier.barrier(1)) : Barrier.barrier();

        nextStage(); // INITIALIZED -> ENTRY_SEARCH

        Collection<PluginAttribution> attributions;
        try {
            attributions = entryDiscoverer.search(context, source, barrier);
        } catch (PluginMountingException e) {
            postSearchStageFailed(context, source, entryDiscoverer, barrier, e);

            throw e;
        }

        if (restricted && attributions.size() > 1)
        {
            PluginInstancePolicyViolationException instancePolicyViolation
                    = new PluginInstancePolicyViolationException(
                            "Multiple plugin entries found when the instance policy is RESTRICTED_SINGLE");

            postSearchStageFailed(context, source, entryDiscoverer, barrier, instancePolicyViolation);

            throw instancePolicyViolation;
        }

        postSearchStagePassed(context, source, entryDiscoverer, barrier, attributions);

        nextStage(); // ENTRY_SEARCH -> VERIFICATION

        PluginVerificationManager verificationManager = context.getVerificationManager();
        if (verificationManager.hasVerifier())
        {
            Iterator<PluginAttribution> iter = attributions.iterator();

            while (iter.hasNext())
            {
                PluginAttribution attribution = iter.next();

                try {
                    if (verificationManager.verify(context, attribution)) // unsafe point
                        postVerificationStagePassed(context, attribution);
                    else
                    {
                        iter.remove();

                        postVerificationStageRejected(context, attribution);
                    }
                } catch (PluginVerifierException e) {
                    postVerificationStageFailed(context, attribution, e);

                    throw e;
                }
            }
        }
        else
            postVerificationStageSkipped(context);

        nextStage(); // VERIFICATION -> CLASS_LOAD

        List<Pair<PluginAttribution, Class<?>>> loadCache = new ArrayList<>();

        for (PluginAttribution attribution : attributions)
        {
            Class<?> mainClassInstance;

            postClassLoadStart(context, classLoader, attribution);

            try {
                mainClassInstance = classLoader.loadClass(attribution.getMainClass());
            } catch (ClassNotFoundException e) {
                postClassLoadFailure(context, classLoader, attribution, e);

                continue;
            }

            if (postClassLoaded(context, classLoader, attribution, mainClassInstance))
            {
                postClassLoadCancelled(context, classLoader, attribution, mainClassInstance);

                continue;
            }

            loadCache.add(Pair.of(attribution, mainClassInstance));

            postClassLoadPassed(context, classLoader, attribution, mainClassInstance);
        }

        nextStage(); // CLASS_LOAD -> CONSTRUCTION

        for (Pair<PluginAttribution, Class<?>> pair : loadCache)
        {
            PluginAttribution attribution = pair.first();
            Class<?> mainClass = pair.second();

            postConstructionStart(context, attribution, mainClass);

            Optional<PluginInjector> injector = PluginInjector
                    .ofConstructor(context.getInvokerProvider(), classLoader, context.getInjection(), mainClass);

            Object instance;
            if (injector.isPresent())
            {
                try {
                    instance = injector.get().inject(null, Cluster.of(
                            Pair.of("context", context),
                            Pair.of("classLoder", classLoader),
                            Pair.of("plugin", attribution)
                    ));
                } catch (InvocationTargetException e) {
                    postConstructionFailed(context, attribution, mainClass, e);

                    continue;
                }
            }
            else
            {
                postInjectionMismatch(context, attribution, mainClass);

                continue;
            }

            attribution.initInstance(instance);

            postConstructionPassed(context, attribution, mainClass);
        }

        nextStage(); // CONSTRUCTION -> AFTER_CONSTRUCTION

        PluginNamespace namespace = context.getPluginNamespace();

        for (Pair<PluginAttribution, Class<?>> pair : loadCache)
        {
            PluginAttribution attribution = pair.first();

            if (!attribution.isConstructed())
                continue;

            JavaPlugin plugin = new JavaPlugin(context, pair.second(), attribution);

            try {
                namespace.registerPlugin(plugin);
            } catch (PluginMountingException e) {
                postAfterConstructionRegistrationFailed(context, namespace, attribution, e);

                throw e;
            }

            postAfterConstructionRegistrationPassed(context, namespace, plugin);
        }

        nextStage(); // AFTER_CONSTRUCTION -> FINISHED
    }

    public static void postSearchStageFailed(@Nonnull UPMContext context,
                                             @Nonnull Source source,
                                             @Nonnull PluginEntryDiscoverer discoverer,
                                             @Nonnull Barrier barrier,
                                             @Nonnull Exception cause)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .Failed(context, source, discoverer, barrier, cause));
    }

    public static void postSearchStagePassed(@Nonnull UPMContext context,
                                             @Nonnull Source source,
                                             @Nonnull PluginEntryDiscoverer discoverer,
                                             @Nonnull Barrier barrier,
                                             @Nonnull Collection<PluginAttribution> attributions)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .Passed(context, source, discoverer, barrier, attributions));
    }

    public static void postVerificationStagePassed(@Nonnull UPMContext context,
                                                   @Nonnull PluginAttribution attribution)
    {
        context.getEventBus().post(new PluginVerificationStageEvent.Passed(context, attribution));
    }

    public static void postVerificationStageRejected(@Nonnull UPMContext context,
                                                     @Nonnull PluginAttribution attribution)
    {
        context.getEventBus().post(new PluginVerificationStageEvent.Rejected(context, attribution));
    }

    public static void postVerificationStageSkipped(@Nonnull UPMContext context)
    {
        context.getEventBus().post(new PluginVerificationStageEvent.Skipped(context));
    }

    public static void postVerificationStageFailed(@Nonnull UPMContext context,
                                                   @Nonnull PluginAttribution attribution,
                                                   @Nonnull Exception cause)
    {
        context.getEventBus().post(new PluginVerificationStageEvent.Failed(context, attribution, cause));
    }

    public static void postClassLoadStart(@Nonnull UPMContext context,
                                          @Nonnull PluginClassLoader classLoader,
                                          @Nonnull PluginAttribution attribution)
    {
        context.getEventBus().post(
                new PluginClassLoadStageEvent.Start(context, classLoader, attribution));
    }

    public static void postClassLoadFailure(@Nonnull UPMContext context,
                                            @Nonnull PluginClassLoader classLoader,
                                            @Nonnull PluginAttribution attribution,
                                            @Nonnull Exception cause)
    {
        context.getEventBus().post(
                new PluginClassLoadStageEvent.Failure(context, classLoader, attribution, cause));
    }

    public static boolean postClassLoaded(@Nonnull UPMContext context,
                                          @Nonnull PluginClassLoader classLoader,
                                          @Nonnull PluginAttribution attribution,
                                          @Nonnull Class<?> mainClass)
    {
        PluginClassLoadStageEvent.Loaded event =
                new PluginClassLoadStageEvent.Loaded(context, classLoader, attribution, mainClass);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postClassLoadCancelled(@Nonnull UPMContext context,
                                              @Nonnull PluginClassLoader classLoader,
                                              @Nonnull PluginAttribution attribution,
                                              @Nonnull Class<?> mainClass)
    {
        context.getEventBus().post(
                new PluginClassLoadStageEvent.Cancelled(context, classLoader, attribution, mainClass));
    }

    public static void postClassLoadPassed(@Nonnull UPMContext context,
                                           @Nonnull PluginClassLoader classLoader,
                                           @Nonnull PluginAttribution attribution,
                                           @Nonnull Class<?> mainClass)
    {
        context.getEventBus().post(
                new PluginClassLoadStageEvent.Passed(context, classLoader, attribution, mainClass));
    }

    public static void postConstructionStart(@Nonnull UPMContext context,
                                             @Nonnull PluginAttribution attribution,
                                             @Nonnull Class<?> mainClass)
    {
        context.getEventBus().post(
                new PluginConstructionStageEvent.Start(context, attribution, mainClass));
    }

    public static void postConstructionFailed(@Nonnull UPMContext context,
                                              @Nonnull PluginAttribution attribution,
                                              @Nonnull Class<?> mainClass,
                                              @Nonnull Exception cause)
    {
        context.getEventBus().post(
                new PluginConstructionStageEvent.Failed(context, attribution, mainClass, cause));
    }

    public static void postConstructionPassed(@Nonnull UPMContext context,
                                              @Nonnull PluginAttribution attribution,
                                              @Nonnull Class<?> mainClass)
    {
        context.getEventBus().post(
                new PluginConstructionStageEvent.Passed(context, attribution, mainClass));
    }

    public static void postInjectionMismatch(@Nonnull UPMContext context,
                                             @Nonnull PluginAttribution attribution,
                                             @Nonnull Class<?> mainClass)
    {
        context.getEventBus().post(
                new PluginConstructionStageEvent.InjectionMismatch(context, attribution, mainClass));
    }

    public static void postAfterConstructionRegistrationPassed(@Nonnull UPMContext context,
                                                               @Nonnull PluginNamespace namespace,
                                                               @Nonnull Plugin plugin)
    {
        context.getEventBus().post(
                new PluginAfterConstructionStageEvent.RegistrationPassed(context, namespace, plugin));
    }

    public static void postAfterConstructionRegistrationFailed(@Nonnull UPMContext context,
                                                               @Nonnull PluginNamespace namespace,
                                                               @Nonnull PluginAttribution attribution,
                                                               @Nonnull Exception cause)
    {
        context.getEventBus().post(
                new PluginAfterConstructionStageEvent.RegistrationFailed(context, namespace, attribution, cause));
    }

    private Stage stage = Stage.INITIALIZED;

    private final Source source;

    private final PluginInstancePolicy instancePolicy;

    private final PluginEntryDiscoverer entryDiscoverer;

    private final PluginClassLoader classLoader;

    private final UPMContext context;

    public static enum Stage
    {
        INITIALIZED,
        ENTRY_SEARCH,
        VERIFICATION,
        CLASS_LOAD,
        CONSTRUCTION,
        AFTER_CONSTRUCTION,
        FINISHED;

        public Stage nextStage()
        {
            return values()[ordinal() + 1];
        }
    }
}
