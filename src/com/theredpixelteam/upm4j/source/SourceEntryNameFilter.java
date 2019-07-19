package com.theredpixelteam.upm4j.source;

import javax.annotation.Nonnull;

public interface SourceEntryNameFilter {
    public boolean accept(@Nonnull String entryName);
}
