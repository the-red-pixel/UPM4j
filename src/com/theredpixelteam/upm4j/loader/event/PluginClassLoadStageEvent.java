package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.loader.PluginClassLoader;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class PluginClassLoadStageEvent implements UPMEvent {
    protected PluginClassLoadStageEvent(@Nonnull UPMContext context,
                                        @Nonnull PluginClassLoader classLoader,
                                        @Nonnull PluginAttribution plugin)
    {
        this.context = Objects.requireNonNull(context, "context");
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
        this.plugin = Objects.requireNonNull(plugin, "pluginAttribution");
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

    public @Nonnull PluginClassLoader getClassLoader()
    {
        return classLoader;
    }

    private final PluginClassLoader classLoader;

    private final PluginAttribution plugin;

    private final UPMContext context;

    public static class Start extends PluginClassLoadStageEvent
    {
        public Start(@Nonnull UPMContext context,
                     @Nonnull PluginClassLoader classLoader,
                     @Nonnull PluginAttribution plugin)
        {
            super(context, classLoader, plugin);
        }
    }

    public static class Failure extends PluginClassLoadStageEvent
    {

        public Failure(@Nonnull UPMContext context,
                       @Nonnull PluginClassLoader classLoader,
                       @Nonnull PluginAttribution plugin,
                       @Nonnull Exception cause)
        {
            super(context, classLoader, plugin);
            this.cause = Objects.requireNonNull(cause, "cause");
        }

        public @Nonnull Exception getCause()
        {
            return cause;
        }

        private final Exception cause;
    }

    public static class Passed extends PluginClassLoadStageEvent
    {
        public Passed(@Nonnull UPMContext context,
                      @Nonnull PluginClassLoader classLoader,
                      @Nonnull PluginAttribution plugin)
        {
            super(context, classLoader, plugin);
        }
    }
}
