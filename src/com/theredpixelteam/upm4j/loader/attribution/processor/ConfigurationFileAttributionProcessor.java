package com.theredpixelteam.upm4j.loader.attribution.processor;

import com.theredpixelteam.upm4j.loader.attribution.AttributionWorkflow;
import com.theredpixelteam.upm4j.source.SourceEntry;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface ConfigurationFileAttributionProcessor {
    public void process(@Nonnull AttributionWorkflow workflow,
                        @Nonnull SourceEntry fileEntry,
                        @Nonnull Barrier barrier)
            throws IOException;
}
