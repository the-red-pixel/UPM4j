package com.theredpixelteam.upm4j.plugin.java;

import com.theredpixelteam.redtea.function.FunctionWithThrowable;
import com.theredpixelteam.redtea.function.Predicate;
import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.plugin.*;
import com.theredpixelteam.upm4j.plugin.event.PluginStateChangeEvent;
import com.theredpixelteam.upm4j.plugin.event.PluginStateChangeEvent.Actions;
import com.theredpixelteam.upm4j.plugin.exception.PluginTargetException;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

public class JavaPlugin implements Plugin {
    public JavaPlugin(@Nonnull UPMContext context,
                      @Nonnull Class<?> mainClass,
                      @Nonnull PluginAttribution attribution)
    {
        this.context = Objects.requireNonNull(context, "context");
        this.mainClass = Objects.requireNonNull(mainClass, "mainClass");
        this.attribution = Objects.requireNonNull(attribution, "attribution");
        this.handler = context.getStateHandler();
    }

    @Override
    public @Nonnull PluginState getState()
    {
        return state;
    }

    @Override
    public @Nonnull PluginAttribution getAttribution()
    {
        return attribution;
    }

    @Override
    public @Nonnull Class<?> getMainClass()
    {
        return mainClass;
    }

    @Override
    public boolean isLoaded()
    {
        return handler.isLoaded(state);
    }

    @Override
    public boolean isEnabled()
    {
        return handler.isEnabled(state);
    }

    private boolean actionTemplate(Predicate<Plugin> pretest,
                                   FunctionWithThrowable<Plugin, Optional<PluginState>, PluginTargetException> procedure,
                                   PluginStateChangeEvent.Action action)
    {
        if (pretest.test(this))
        {
            postStateChangeRejected(this, action, state);

            return false;
        }

        Optional<PluginState> nextState;
        try {
            nextState = procedure.apply(this);
        } catch (PluginTargetException e) {
            postStateChangeFailed(this, action, state, e);

            return false;
        }

        if (!nextState.isPresent())
        {
            postStateChangeRejected(this, action, state);

            return false;
        }

        PluginState oldState = state;
        state = nextState.get();

        postStateChangePassed(this, action, oldState, state);

        return true;
    }

    @Override
    public boolean load()
    {
        return actionTemplate(handler::canLoad, handler::tryLoad, Actions.LOAD);
    }

    @Override
    public boolean unload()
    {
        return actionTemplate(handler::canUnload, handler::tryUnload, Actions.UNLOAD);
    }

    @Override
    public boolean enable()
    {
        return actionTemplate(handler::canEnable, handler::tryEnable, Actions.ENABLE);
    }

    @Override
    public boolean disable()
    {
        return actionTemplate(handler::canDisable, handler::tryDisable, Actions.DISABLE);
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public static void postStateChangeRejected(@Nonnull Plugin plugin,
                                               @Nonnull PluginStateChangeEvent.Action action,
                                               @Nonnull PluginState originalState)
    {
        plugin.getContext().getEventBus().post(
                new PluginStateChangeEvent.Rejected(plugin, action, originalState));
    }

    public static void postStateChangeFailed(@Nonnull Plugin plugin,
                                             @Nonnull PluginStateChangeEvent.Action action,
                                             @Nonnull PluginState originalState,
                                             @Nonnull Throwable cause)
    {
        plugin.getContext().getEventBus().post(
                new PluginStateChangeEvent.Failed(plugin, action, originalState, cause));
    }

    public static void postStateChangePassed(@Nonnull Plugin plugin,
                                             @Nonnull PluginStateChangeEvent.Action action,
                                             @Nonnull PluginState originalState,
                                             @Nonnull PluginState currentState)
    {
        plugin.getContext().getEventBus().post(
                new PluginStateChangeEvent.Passed(plugin, action, originalState, currentState));
    }

    private volatile PluginState state;

    private final UPMContext context;

    private final Class<?> mainClass;

    private final PluginStateHandler handler;

    private final PluginAttribution attribution;
}
