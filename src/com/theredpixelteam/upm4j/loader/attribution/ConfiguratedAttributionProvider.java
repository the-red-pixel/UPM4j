package com.theredpixelteam.upm4j.loader.attribution;

import com.theredpixelteam.upm4j.loader.source.PluginSource;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface ConfiguratedAttributionProvider {
    public @Nonnull void provide(@Nonnull AttributionWorkflow workflow,
                                 @Nonnull PluginSource source)
            throws IOException;
}
