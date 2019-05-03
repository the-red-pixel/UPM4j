package com.theredpixelteam.upm4j.loader.attribution.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.loader.attribution.AttributionWorkflow;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class AttributionWorkflowEvent implements UPMEvent {
    AttributionWorkflowEvent(@Nonnull AttributionWorkflow workflow)
    {
        this.context = workflow.getContext();
        this.workflow = workflow;
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public @Nonnull AttributionWorkflow getWorkflow()
    {
        return workflow;
    }

    private final AttributionWorkflow workflow;

    private final UPMContext context;

    public static abstract class CancellableAttributionWorkflowEvent extends AttributionWorkflowEvent
            implements UPMEvent.Cancellable
    {
        CancellableAttributionWorkflowEvent(@Nonnull AttributionWorkflow workflow)
        {
            super(workflow);
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

        private volatile boolean cancelled;
    }

    public static class BeforeBuild extends CancellableAttributionWorkflowEvent
    {
        public BeforeBuild(@Nonnull AttributionWorkflow workflow,
                           @Nonnull PluginAttribution.Builder builder)
        {
            super(workflow);

            this.builder = Objects.requireNonNull(builder);
        }

        public @Nonnull PluginAttribution.Builder getBuilder()
        {
            return builder;
        }

        private final PluginAttribution.Builder builder;
    }

    public static class CancelledBeforeBuild extends AttributionWorkflowEvent
    {
        public CancelledBeforeBuild(@Nonnull AttributionWorkflow workflow,
                                    @Nonnull PluginAttribution.Builder builder)
        {
            super(workflow);

            this.builder = Objects.requireNonNull(builder);
        }

        public @Nonnull PluginAttribution.Builder getBuilder()
        {
            return builder;
        }

        private final PluginAttribution.Builder builder;
    }

    public static class CancelledAfterBuild extends AttributionWorkflowEvent
    {
        public CancelledAfterBuild(@Nonnull AttributionWorkflow workflow,
                                   @Nonnull PluginAttribution attribution)
        {
            super(workflow);
            this.attribution = Objects.requireNonNull(attribution);
        }

        public @Nonnull PluginAttribution getAttribution()
        {
            return attribution;
        }

        private final PluginAttribution attribution;
    }

    public static class BuildFailure extends AttributionWorkflowEvent
    {
        public BuildFailure(@Nonnull AttributionWorkflow workflow,
                            @Nonnull PluginAttribution.Builder builder,
                            @Nonnull Exception cause)
        {
            super(workflow);

            this.builder = Objects.requireNonNull(builder);
            this.cause = Objects.requireNonNull(cause);
        }

        public @Nonnull PluginAttribution.Builder getBuilder()
        {
            return builder;
        }

        public Exception getCause()
        {
            return cause;
        }

        private final PluginAttribution.Builder builder;

        private final Exception cause;
    }

    public static class BuildSuccess extends AttributionWorkflowEvent
    {
        public BuildSuccess(@Nonnull AttributionWorkflow workflow,
                            @Nonnull PluginAttribution attribution)
        {
            super(workflow);

            this.attribution = attribution;
        }

        public @Nonnull PluginAttribution getAttribution()
        {
            return attribution;
        }

        private final PluginAttribution attribution;
    }

    public static class AfterBuild extends CancellableAttributionWorkflowEvent
    {
        public AfterBuild(@Nonnull AttributionWorkflow workflow,
                          @Nonnull PluginAttribution attribution)
        {
            super(workflow);

            this.attribution = Objects.requireNonNull(attribution);
        }

        public @Nonnull PluginAttribution getAttribution()
        {
            return attribution;
        }

        private final PluginAttribution attribution;
    }
}
