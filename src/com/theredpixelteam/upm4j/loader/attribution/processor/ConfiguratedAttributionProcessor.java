package com.theredpixelteam.upm4j.loader.attribution.processor;

import com.theredpixelteam.upm4j.loader.attribution.AttributionWorkflow;
import com.theredpixelteam.upm4j.loader.source.PluginSource;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface ConfiguratedAttributionProcessor {
    public void provide(@Nonnull AttributionWorkflow workflow,
                        @Nonnull PluginSource source,
                        @Nonnull Barrier barrier)
            throws IOException;
}
