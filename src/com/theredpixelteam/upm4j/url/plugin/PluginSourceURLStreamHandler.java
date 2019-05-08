package com.theredpixelteam.upm4j.url.plugin;

import com.theredpixelteam.upm4j.loader.source.PluginSource;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Objects;

public class PluginSourceURLStreamHandler extends URLStreamHandler {
    public PluginSourceURLStreamHandler(@Nonnull String identity,
                                        @Nonnull PluginSource source)
    {
        this.identity = Objects.requireNonNull(identity, "identity");
        this.source = Objects.requireNonNull(source, "source");
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException
    {
        if (!u.getProtocol().equals(identity))
            throw new MalformedURLException("Identity failure, received \""
                    + u.getProtocol() + "\" but \"" + identity + "\" required");

        return new PluginSourceURLConnection(source, u);
    }

    public @Nonnull PluginSource getSource()
    {
        return source;
    }

    public @Nonnull String getIdentity()
    {
        return identity;
    }

    private final String identity;

    private final PluginSource source;
}
