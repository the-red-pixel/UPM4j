package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.loader.PluginConstructor;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class PluginConstructorEvent implements UPMEvent {
    protected PluginConstructorEvent(@Nonnull UPMContext context,
                                     @Nonnull PluginConstructor constructor)
    {
        this.context = Objects.requireNonNull(context, "context");
        this.constructor = Objects.requireNonNull(constructor, "constructor");
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public @Nonnull PluginConstructor getConstructor()
    {
        return constructor;
    }

    private final UPMContext context;

    private final PluginConstructor constructor;

    public static class Reset extends PluginConstructorEvent
    {
        public Reset(@Nonnull UPMContext context,
                     @Nonnull PluginConstructor constructor)
        {
            super(context, constructor);
        }
    }

    public static class StageChange extends PluginConstructorEvent
    {
        public StageChange(@Nonnull UPMContext context,
                           @Nonnull PluginConstructor constructor,
                           @Nonnull PluginConstructor.Stage prevStage,
                           @Nonnull PluginConstructor.Stage currentStage)
        {
            super(context, constructor);
            this.prev = Objects.requireNonNull(prevStage, "prevStage");
            this.next = Objects.requireNonNull(currentStage, "currentStage");
        }

        public @Nonnull PluginConstructor.Stage getPreviousStage()
        {
            return prev;
        }

        public PluginConstructor.Stage getCurrentStage()
        {
            return next;
        }

        private final PluginConstructor.Stage prev;

        private final PluginConstructor.Stage next;
    }
}
