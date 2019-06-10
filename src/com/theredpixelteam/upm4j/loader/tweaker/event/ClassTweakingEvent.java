package com.theredpixelteam.upm4j.loader.tweaker.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ClassTweakingEvent implements UPMEvent {
    protected ClassTweakingEvent(@Nonnull UPMContext context)
    {
        this.context = Objects.requireNonNull(context, "context");
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    // TODO

    private final UPMContext context;
}
