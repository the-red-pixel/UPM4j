package com.theredpixelteam.upm4j.event;

import com.theredpixelteam.upm4j.UPMContext;

import javax.annotation.Nonnull;

public abstract class AbstractCancellableUPMEvent extends AbstractUPMEvent implements UPMEvent.Cancellable {
    protected AbstractCancellableUPMEvent(@Nonnull UPMContext context)
    {
        super(context);
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

    protected boolean cancelled;
}
