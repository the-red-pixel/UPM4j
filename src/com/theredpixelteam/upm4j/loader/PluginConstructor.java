package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.attribution.processor.Barrier;
import com.theredpixelteam.upm4j.loader.event.PluginEntrySearchStageEvent;
import com.theredpixelteam.upm4j.loader.event.PluginVerificationStageEvent;
import com.theredpixelteam.upm4j.loader.exception.PluginInstancePolicyViolationException;
import com.theredpixelteam.upm4j.loader.source.Source;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class PluginConstructor {
    public PluginConstructor(@Nonnull UPMContext context,
                             @Nonnull Source source)
    {
        this.context = Objects.requireNonNull(context);

        this.source = Objects.requireNonNull(source, "source");
        this.classLoader = context.getClassLoaderProvider().provide();

        this.entryDiscoverer = context.getEntryDiscoverer();
        this.instancePolicy = context.getInstancePolicy();
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

    public @Nonnull UPMClassLoader getClassLoader()
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

        // TODO
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

    private int stage = STAGE_INITIALIZED;

    private final Source source;

    private Exception lastException;

    private final PluginInstancePolicy instancePolicy;

    private final PluginEntryDiscoverer entryDiscoverer;

    private final UPMClassLoader classLoader;

    private final UPMContext context;

    public static final int STAGE_INITIALIZED = 0x01;

    public static final int STAGE_ENTRY_SEARCH = 0x02;

    public static final int STAGE_VERIFICATION = 0x04;

    public static final int STAGE_CLASS_LOAD = 0x08;
}
