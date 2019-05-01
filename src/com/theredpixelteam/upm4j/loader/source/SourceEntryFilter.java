package com.theredpixelteam.upm4j.loader.source;

import javax.annotation.Nonnull;

public interface SourceEntryFilter {
    public boolean accept(@Nonnull PluginSourceEntry entry);
}
