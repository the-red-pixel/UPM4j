package com.theredpixelteam.upm4j.event;

import com.theredpixelteam.upm4j.UPMContext;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class AbstractCancellableUPMEvent implements UPMEvent.Cancellable {
    protected AbstractCancellableUPMEvent(@Nonnull UPMContext context)
    {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void cancel()
    {
        this.cancelled = true;
    }

    @Override
    public UPMContext getContext()
    {
        return context;
    }

    protected boolean cancelled;

    protected final UPMContext context;
}
