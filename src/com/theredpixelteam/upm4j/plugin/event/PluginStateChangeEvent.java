package com.theredpixelteam.upm4j.plugin.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.plugin.Plugin;
import com.theredpixelteam.upm4j.plugin.PluginState;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class PluginStateChangeEvent implements UPMEvent {
    protected PluginStateChangeEvent(@Nonnull Plugin plugin,
                                     @Nonnull Action action,
                                     @Nonnull PluginState originalState)
    {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.context = plugin.getContext();
        this.action = Objects.requireNonNull(action, "action");
        this.originalState = Objects.requireNonNull(originalState, "originalState");
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public @Nonnull Plugin getPlugin()
    {
        return plugin;
    }

    public @Nonnull Action getAction()
    {
        return action;
    }

    public @Nonnull PluginState getOriginalState()
    {
        return originalState;
    }

    private final Plugin plugin;

    private final UPMContext context;

    private final Action action;

    private final PluginState originalState;

    public static interface Action
    {
        public String getName();
    }

    public static final class Actions
    {
        public static final Action LOAD = () -> "LOAD";

        public static final Action ENABLE = () -> "ENABLE";

        public static final Action DISABLE = () -> "DISABLE";

        public static final Action UNLOAD = () -> "UNLOAD";
    }

    public static class Rejected extends PluginStateChangeEvent
    {
        public Rejected(@Nonnull Plugin plugin,
                        @Nonnull Action action,
                        @Nonnull PluginState originalState)
        {
            super(plugin, action, originalState);
        }
    }

    public static class Failed extends PluginStateChangeEvent
    {
        public Failed(@Nonnull Plugin plugin,
                      @Nonnull Action action,
                      @Nonnull PluginState originalState,
                      @Nonnull Throwable cause)
        {
            super(plugin, action, originalState);
            this.cause = Objects.requireNonNull(cause, "cause");
        }

        public @Nonnull Throwable getCause()
        {
            return cause;
        }

        private final Throwable cause;
    }

    public static class Passed extends PluginStateChangeEvent
    {
        public Passed(@Nonnull Plugin plugin,
                      @Nonnull Action action,
                      @Nonnull PluginState originalState,
                      @Nonnull PluginState currentState)
        {
            super(plugin, action, originalState);
            this.currentState = Objects.requireNonNull(currentState, "currentState");
        }

        public @Nonnull PluginState getCurrentState()
        {
            return currentState;
        }

        private final PluginState currentState;
    }
}
