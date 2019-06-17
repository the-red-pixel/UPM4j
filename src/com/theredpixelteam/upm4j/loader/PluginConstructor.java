package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.redtea.util.Cluster;
import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.redtea.util.Pair;
import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.inject.PluginInjector;
import com.theredpixelteam.upm4j.loader.attribution.processor.Barrier;
import com.theredpixelteam.upm4j.loader.event.PluginClassLoadStageEvent;
import com.theredpixelteam.upm4j.loader.event.PluginConstructionStageEvent;
import com.theredpixelteam.upm4j.loader.event.PluginEntrySearchStageEvent;
import com.theredpixelteam.upm4j.loader.event.PluginVerificationStageEvent;
import com.theredpixelteam.upm4j.loader.source.Source;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.io.IOException;
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

    public @Nonnull Optional<Exception> getLastException()
    {
        return Optional.ofNullable(lastException);
    }

    public boolean hasLastException()
    {
        return lastException != null;
    }

    public int getStage()
    {
        return stage;
    }

    private void clearState()
    {
        lastException = null;
        stage = STAGE_INITIALIZED;
    }

    private void nextStage()
    {
        stage <<= 1;
    }

    private void failed(Exception e)
    {
        lastException = e;
    }

    @SuppressWarnings("unchecked")
    public synchronized void construct()
    {
        clearState();

        boolean single = !PluginInstancePolicy.MULTIPLE.equals(instancePolicy);
        boolean restricted = PluginInstancePolicy.RESTRICTED_SINGLE.equals(instancePolicy);

        Barrier barrier = single ? (restricted ? Barrier.barrier(2) : Barrier.barrier(1)) : Barrier.barrier();

        nextStage(); // INITIALIZED -> ENTRY_SEARCH

        Collection<PluginAttribution> attributions;
        try {
            attributions = entryDiscoverer.search(context, source, barrier);
        } catch (IOException e) {
            failed(e);
            postSearchStageFailed(context, source, entryDiscoverer, barrier, e);

            return;
        }

        if (restricted && attributions.size() > 1)
        {
            PluginInstancePolicyViolationException instancePolicyViolation
                    = new PluginInstancePolicyViolationException(
                            "Multiple plugin entries found when the instance policy is RESTRICTED_SINGLE");

            failed(instancePolicyViolation);
            postSearchStageFailed(context, source, entryDiscoverer, barrier, instancePolicyViolation);

            return;
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

                if (verificationManager.verify(context, attribution))
                    postVerificationStagePassed(context, attribution);
                else
                {
                    iter.remove();

                    postVerificationStageRejected(context, attribution);
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

            loadCache.add(Pair.of(attribution, mainClassInstance));

            postClassLoadPassed(context, classLoader, attribution);
        }

        nextStage(); // CLASS_LOAD -> CONSTRUCTION

        for (Pair<PluginAttribution, Class<?>> pair : loadCache)
        {
            PluginAttribution attribution = pair.first();
            Class<?> mainClass = pair.second();

            postConstructionStart(context, attribution, mainClass);

            Optional<PluginInjector> injector = PluginInjector
                    .ofConstructor(context.getInvokerProvider(), context.getInjection(), mainClass);

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

        // TODO

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

    public static void postClassLoadPassed(@Nonnull UPMContext context,
                                           @Nonnull PluginClassLoader classLoader,
                                           @Nonnull PluginAttribution attribution)
    {
        context.getEventBus().post(
                new PluginClassLoadStageEvent.Passed(context, classLoader, attribution));
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

    private int stage = STAGE_INITIALIZED;

    private final Source source;

    private Exception lastException;

    private final PluginInstancePolicy instancePolicy;

    private final PluginEntryDiscoverer entryDiscoverer;

    private final PluginClassLoader classLoader;

    private final UPMContext context;

    public static final int STAGE_INITIALIZED = 0x01;

    public static final int STAGE_ENTRY_SEARCH = 0x02;

    public static final int STAGE_VERIFICATION = 0x04;

    public static final int STAGE_CLASS_LOAD = 0x08;

    public static final int STAGE_CONSTRUCTION = 0x10;

    public static final int STAGE_AFTER_CONSTRUCTION = 0x20;

    public static final int STAGE_FINISHED = 0x40;
}
