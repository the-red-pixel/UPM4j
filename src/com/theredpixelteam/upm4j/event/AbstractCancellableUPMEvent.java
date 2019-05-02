package com.theredpixelteam.upm4j.event;

public abstract class AbstractCancellableUPMEvent implements UPMEvent.Cancellable {
    protected AbstractCancellableUPMEvent()
    {
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
