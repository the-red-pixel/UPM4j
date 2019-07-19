package com.theredpixelteam.upm4j.loader.attribution.processor;

import com.theredpixelteam.upm4j.loader.attribution.AttributionWorkflow;
import com.theredpixelteam.upm4j.source.SourceEntry;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface FixedClassAttributionProcessor {
    public void process(@Nonnull AttributionWorkflow workflow,
                        @Nonnull String className,
                        @Nonnull SourceEntry sourceEntry,
                        @Nonnull Barrier barrier)
            throws IOException;
}
