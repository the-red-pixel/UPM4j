package com.theredpixelteam.upm4j.url.source;

import com.theredpixelteam.upm4j.loader.source.Source;
import com.theredpixelteam.upm4j.url.URLFactory;

import javax.annotation.Nonnull;
import java.net.URL;

public class SourceURLFactory {
    private SourceURLFactory()
    {
    }

    public static @Nonnull URL create(@Nonnull String identity,
                                      @Nonnull String pluginHost,
                                      @Nonnull Source source)
    {
        return URLFactory.create(identity, pluginHost, "", new SourceURLStreamHandler(identity, source));
    }
}
