package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class PluginConstructionStageEvent implements UPMEvent {
    protected PluginConstructionStageEvent(@Nonnull UPMContext context,
                                           @Nonnull PluginAttribution plugin,
                                           @Nonnull Class<?> mainClass)
    {
        this.context = Objects.requireNonNull(context, "context");
        this.plugin = Objects.requireNonNull(plugin, "pluginAttribution");
        this.mainClass = Objects.requireNonNull(mainClass, "mainClass");
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

    public @Nonnull Class<?> getMainClass()
    {
        return mainClass;
    }

    private final Class<?> mainClass;

    private final UPMContext context;

    private final PluginAttribution plugin;

    public static class Start extends PluginConstructionStageEvent
    {
        public Start(@Nonnull UPMContext context,
                     @Nonnull PluginAttribution plugin,
                     @Nonnull Class<?> mainClass)
        {
            super(context, plugin, mainClass);
        }
    }

    public static class InjectionMismatch extends PluginConstructionStageEvent
    {
        public InjectionMismatch(@Nonnull UPMContext context,
                                 @Nonnull PluginAttribution plugin,
                                 @Nonnull Class<?> mainClass)
        {
            super(context, plugin, mainClass);
        }
    }

    public static class Failed extends PluginConstructionStageEvent
    {
        public Failed(@Nonnull UPMContext context,
                      @Nonnull PluginAttribution plugin,
                      @Nonnull Class<?> mainClass,
                      @Nonnull Exception cause)
        {
            super(context, plugin, mainClass);
            this.cause = Objects.requireNonNull(cause, "cause");
        }

        public @Nonnull Exception getCause()
        {
            return cause;
        }

        private final Exception cause;
    }

    public static class Passed extends PluginConstructionStageEvent
    {
        public Passed(@Nonnull UPMContext context,
                      @Nonnull PluginAttribution plugin,
                      @Nonnull Class<?> mainClass)
        {
            super(context, plugin, mainClass);
        }
    }
}
