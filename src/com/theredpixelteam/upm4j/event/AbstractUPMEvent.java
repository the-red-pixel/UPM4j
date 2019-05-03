package com.theredpixelteam.upm4j.event;

import com.theredpixelteam.upm4j.UPMContext;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AbstractUPMEvent implements UPMEvent {
    protected AbstractUPMEvent(@Nonnull UPMContext context)
    {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    private final UPMContext context;
}
