package com.theredpixelteam.upm4j.loader.attribution.processor;

import com.theredpixelteam.upm4j.loader.attribution.AttributionWorkflow;
import com.theredpixelteam.upm4j.loader.source.PluginSourceEntry;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface SubclassScanAttributionProcessor {
    public void process(@Nonnull AttributionWorkflow workflow,
                        @Nonnull Class<?> superclass,
                        @Nonnull PluginSourceEntry entry)
            throws IOException;
}
