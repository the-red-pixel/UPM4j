package com.theredpixelteam.upm4j.loader.attribution;

import com.theredpixelteam.upm4j.loader.source.PluginSourceEntry;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface FixedClassAttributionProvider {
    public @Nonnull void provide(@Nonnull AttributionWorkflow workflow,
                                 @Nonnull String className,
                                 @Nonnull PluginSourceEntry sourceEntry)
            throws IOException;
}
