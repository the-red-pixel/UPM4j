package com.theredpixelteam.upm4j.loader.attribution;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.attribution.event.AttributionWorkflowEvent;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.util.*;

public class AttributionWorkflow {
    public AttributionWorkflow(@Nonnull UPMContext context)
    {
        this.context = Objects.requireNonNull(context);
    }

    public PluginAttribution.Builder getOrCreateBuilder(@Nonnull String identity)
    {
        return this.builders.computeIfAbsent(
                Objects.requireNonNull(identity),
                (id) -> PluginAttribution.builder().identity(identity));
    }

    public Optional<PluginAttribution.Builder> getBuilder(@Nonnull String identity)
    {
        return Optional.ofNullable(this.builders.get(Objects.requireNonNull(identity)));
    }

    public Collection<PluginAttribution> buildAll()
    {
        List<PluginAttribution> attributions = new ArrayList<>();

        for (PluginAttribution.Builder builder : builders.values())
        {
            if (!postBeforeBuild(builder))
            {
                postCancelledBeforeBuild(builder);
                continue;
            }

            PluginAttribution attribution;
            try {
                attribution = builder.build();
            } catch (Exception e) {
                postBuildFailure(builder, e);
                continue;
            }

            if (!postAfterBuild(attribution))
            {
                postCancelledAfterBuild(attribution);
                continue;
            }

            attributions.add(attribution);

            postBuildSuccess(attribution);
        }

        return attributions;
    }

    private boolean postBeforeBuild(PluginAttribution.Builder builder)
    {
        AttributionWorkflowEvent.BeforeBuild event =
                new AttributionWorkflowEvent.BeforeBuild(this, builder);

        context.getEventBus().post(event);

        return !event.isCancelled();
    }

    private void postCancelledBeforeBuild(PluginAttribution.Builder builder)
    {
        context.getEventBus().post(
                new AttributionWorkflowEvent.CancelledBeforeBuild(this, builder));
    }

    private void postBuildFailure(PluginAttribution.Builder builder, Exception cause)
    {
        context.getEventBus().post(
                new AttributionWorkflowEvent.BuildFailure(this, builder, cause));
    }

    private boolean postAfterBuild(PluginAttribution attribution)
    {
        AttributionWorkflowEvent.AfterBuild event =
                new AttributionWorkflowEvent.AfterBuild(this, attribution);

        context.getEventBus().post(event);

        return !event.isCancelled();
    }

    private void postCancelledAfterBuild(PluginAttribution attribution)
    {
        context.getEventBus().post(
                new AttributionWorkflowEvent.CancelledAfterBuild(this, attribution));
    }

    private void postBuildSuccess(PluginAttribution attribution)
    {
        context.getEventBus().post(
                new AttributionWorkflowEvent.BuildSuccess(this, attribution));
    }

    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    private final UPMContext context;

    private final Map<String, PluginAttribution.Builder> builders = new HashMap<>();
}
