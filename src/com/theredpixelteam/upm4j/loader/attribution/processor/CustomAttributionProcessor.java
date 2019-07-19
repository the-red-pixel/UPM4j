package com.theredpixelteam.upm4j.loader.attribution.processor;

import com.theredpixelteam.upm4j.loader.attribution.AttributionWorkflow;
import com.theredpixelteam.upm4j.source.Source;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface CustomAttributionProcessor {
    public void process(@Nonnull AttributionWorkflow workflow,
                        @Nonnull Source source,
                        @Nonnull Barrier barrier)
            throws IOException;
}
