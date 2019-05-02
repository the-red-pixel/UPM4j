package com.theredpixelteam.upm4j.event;

public interface UPMEvent {
    public interface Cancellable
    {
        public boolean isCancelled();

        public void cancel();
    }
}
