package com.theredpixelteam.upm4j.emulated;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Optional;

@ThreadSafe
public interface Emulated {
    public @Nonnull Optional<EmulatedTranscation> acquire(@Nonnull String path);

    public boolean idle(@Nonnull String path);

    public default boolean occupied(@Nonnull String path)
    {
        return !idle(path);
    }

    public String[] list(@Nonnull String path);

    public default @Nonnull Optional<String> getName()
    {
        return Optional.empty();
    }
}
