package com.theredpixelteam.upm4j.loader.attribution;

import com.theredpixelteam.upm4j.loader.source.PluginSourceEntry;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;

public interface FixedClassAttributionProvider {
    public @Nonnull Collection<PluginAttribution> provide(@Nonnull String className,
                                                          @Nonnull PluginSourceEntry sourceEntry)
            throws IOException;
}
