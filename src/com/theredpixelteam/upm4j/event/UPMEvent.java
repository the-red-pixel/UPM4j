package com.theredpixelteam.upm4j.event;

import com.theredpixelteam.upm4j.UPMContext;

public interface UPMEvent {
    public UPMContext getContext();

    public interface Cancellable extends UPMEvent
    {
        public boolean isCancelled();

        public void cancel();
    }
}
