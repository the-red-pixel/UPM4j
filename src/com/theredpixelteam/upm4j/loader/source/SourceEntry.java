package com.theredpixelteam.upm4j.loader.source;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface SourceEntry {
    public @Nonnull String getName() throws IOException;

    public @Nonnull byte[] getBytes() throws IOException;

    public @Nonnull
    Source getSource();
}
