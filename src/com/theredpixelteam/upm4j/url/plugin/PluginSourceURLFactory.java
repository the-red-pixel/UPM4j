package com.theredpixelteam.upm4j.url.plugin;

import com.theredpixelteam.upm4j.loader.source.PluginSource;
import com.theredpixelteam.upm4j.url.URLFactory;

import javax.annotation.Nonnull;
import java.net.URL;

public class PluginSourceURLFactory {
    private PluginSourceURLFactory()
    {
    }

    public static @Nonnull URL create(@Nonnull String identity,
                                      @Nonnull String pluginHost,
                                      @Nonnull PluginSource source)
    {
        return URLFactory.create(identity, pluginHost, "", new PluginSourceURLStreamHandler(identity, source));
    }
}
