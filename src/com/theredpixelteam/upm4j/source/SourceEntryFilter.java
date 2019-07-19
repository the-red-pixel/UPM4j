package com.theredpixelteam.upm4j.source;

import javax.annotation.Nonnull;

public interface SourceEntryFilter {
    public boolean accept(@Nonnull SourceEntry entry);
}
