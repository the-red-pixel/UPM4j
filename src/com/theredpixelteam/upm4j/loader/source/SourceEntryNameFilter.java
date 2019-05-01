package com.theredpixelteam.upm4j.loader.source;

import javax.annotation.Nonnull;

public interface SourceEntryNameFilter {
    public boolean accept(@Nonnull String entryName);
}
