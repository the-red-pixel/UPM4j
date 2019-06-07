package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.loader.PluginVerifier;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PluginVerificationStageEvent implements UPMEvent {
    protected PluginVerificationStageEvent(@Nonnull UPMContext context,
                                           @Nonnull PluginAttribution plugin)
    {
        this.context = Objects.requireNonNull(context, "context");
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public @Nonnull PluginAttribution getPlugin()
    {
        return plugin;
    }

    private final UPMContext context;

    private final PluginAttribution plugin;

    protected static class Cancellable extends PluginVerificationStageEvent implements UPMEvent.Cancellable
    {
        protected Cancellable(@Nonnull UPMContext context,
                              @Nonnull PluginAttribution plugin)
        {
            super(context, plugin);
        }

        @Override
        public boolean isCancelled()
        {
            return cancelled;
        }

        @Override
        public void cancel()
        {
            cancelled = true;
        }

        private boolean cancelled;
    }

    public static class Start extends Cancellable
    {
        public Start(@Nonnull UPMContext context,
                     @Nonnull PluginAttribution plugin)
        {
            super(context, plugin);
        }
    }

    public static class Cancelled extends PluginVerificationStageEvent
    {
        public Cancelled(@Nonnull UPMContext context,
                         @Nonnull PluginAttribution plugin)
        {
            super(context, plugin);
        }
    }

    public static class VerificationEvent extends PluginVerificationStageEvent
    {
        protected VerificationEvent(@Nonnull UPMContext context,
                                    @Nonnull PluginAttribution plugin,
                                    @Nonnull PluginVerifier verifier)
        {
            super(context, plugin);
            this.verifier = Objects.requireNonNull(verifier, "verifier");
        }

        public @Nonnull PluginVerifier getVerifier()
        {
            return verifier;
        }

        private final PluginVerifier verifier;
    }

    public static class CancellableVerificationEvent extends VerificationEvent implements UPMEvent.Cancellable
    {
        protected CancellableVerificationEvent(@Nonnull UPMContext context,
                                               @Nonnull PluginAttribution plugin,
                                               @Nonnull PluginVerifier verifier)
        {
            super(context, plugin, verifier);
        }

        @Override
        public boolean isCancelled()
        {
            return cancelled;
        }

        @Override
        public void cancel()
        {
            this.cancelled = true;
        }

        private boolean cancelled;
    }

    public static class VerificationStart extends CancellableVerificationEvent
    {
        public VerificationStart(@Nonnull UPMContext context,
                                 @Nonnull PluginAttribution plugin,
                                 @Nonnull PluginVerifier verifier)
        {
            super(context, plugin, verifier);
        }
    }

    public static class VerificationCancelled extends VerificationEvent
    {
        public VerificationCancelled(@Nonnull UPMContext context,
                                     @Nonnull PluginAttribution plugin,
                                     @Nonnull PluginVerifier verifier)
        {
            super(context, plugin, verifier);
        }
    }

    public static class VerificationPassed extends VerificationEvent
    {
        public VerificationPassed(@Nonnull UPMContext context,
                                  @Nonnull PluginAttribution plugin,
                                  @Nonnull PluginVerifier verifier)
        {
            super(context, plugin, verifier);
        }
    }

    public static class VerificationFailure extends CancellableVerificationEvent
    {
        public VerificationFailure(@Nonnull UPMContext context,
                                   @Nonnull PluginAttribution plugin,
                                   @Nonnull PluginVerifier verifier)
        {
            super(context, plugin, verifier);
        }
    }

    public static class VerificationFailureCancelled extends VerificationEvent
    {
        public VerificationFailureCancelled(@Nonnull UPMContext context,
                                            @Nonnull PluginAttribution plugin,
                                            @Nonnull PluginVerifier verifier)
        {
            super(context, plugin, verifier);
        }
    }

    public static class Passed extends PluginVerificationStageEvent
    {
        public Passed(@Nonnull UPMContext context,
                      @Nonnull PluginAttribution plugin)
        {
            super(context, plugin);
        }
    }

    // TODO
}
