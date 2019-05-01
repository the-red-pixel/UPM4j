package com.theredpixelteam.upm4j.loader.source;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface PluginSourceEntry {
    public @Nonnull String getName() throws IOException;

    public @Nonnull byte[] getBytes() throws IOException;
}
