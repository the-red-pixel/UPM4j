package com.theredpixelteam.upm4j.loader.attribution.processor;

import com.theredpixelteam.upm4j.loader.attribution.AttributionWorkflow;
import com.theredpixelteam.upm4j.loader.source.PluginSourceEntry;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface ConfigurationFileAttributionProcessor {
    public void provide(@Nonnull AttributionWorkflow workflow,
                        @Nonnull PluginSourceEntry fileEntry,
                        @Nonnull Barrier barrier)
            throws IOException;
}
