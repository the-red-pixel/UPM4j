package com.theredpixelteam.upm4j.source;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class SourceURLConnection extends URLConnection {
    protected SourceURLConnection(@Nonnull Source source,
                                  @Nonnull URL url)
    {
        super(url);
        this.source = Objects.requireNonNull(source);
    }

    @Override
    public void connect() throws IOException
    {
        connected = true;
    }

    @Override
    public void setDoInput(boolean b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getDoInput()
    {
        return true;
    }

    @Override
    public void setDoOutput(boolean b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getDoOutput()
    {
        return false;
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(source.getEntry(url.getPath().substring(1))
                .orElseThrow(
                        () -> new IOException("No such source class path resource: " + url.getPath().substring(1)))
                .getBytes());
    }

    private final Source source;
}
