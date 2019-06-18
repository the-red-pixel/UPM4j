package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.plugin.Plugin;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;
import com.theredpixelteam.upm4j.plugin.PluginNamespace;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PluginAfterConstructionStageEvent implements UPMEvent {
    protected PluginAfterConstructionStageEvent(@Nonnull UPMContext context)
    {
        this.context = Objects.requireNonNull(context, "context");
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    private final UPMContext context;

    public static class RegistrationPassed extends PluginAfterConstructionStageEvent
    {
        public RegistrationPassed(@Nonnull UPMContext context,
                                  @Nonnull PluginNamespace namespace,
                                  @Nonnull Plugin plugin)
        {
            super(context);
            this.namespace = Objects.requireNonNull(namespace, "namespace");
            this.plugin = Objects.requireNonNull(plugin, "plugin");
        }

        public @Nonnull PluginNamespace getNamespace()
        {
            return namespace;
        }

        public @Nonnull Plugin getPlugin()
        {
            return plugin;
        }

        private final PluginNamespace namespace;

        private final Plugin plugin;
    }

    public static class RegistrationFailed extends PluginAfterConstructionStageEvent
    {
        public RegistrationFailed(@Nonnull UPMContext context,
                                  @Nonnull PluginNamespace namespace,
                                  @Nonnull PluginAttribution attribution,
                                  @Nonnull Exception cause)
        {
            super(context);
            this.namespace = Objects.requireNonNull(namespace, "namespace");
            this.attribution = Objects.requireNonNull(attribution, "attribution");
            this.cause = Objects.requireNonNull(cause, "cause");
        }

        public @Nonnull PluginNamespace getNamespace()
        {
            return namespace;
        }

        public @Nonnull PluginAttribution getAttribution()
        {
            return attribution;
        }

        public @Nonnull Exception getCause()
        {
            return cause;
        }

        private final PluginNamespace namespace;

        private final PluginAttribution attribution;

        private final Exception cause;
    }
}
