package com.theredpixelteam.upm4j.emulated;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Objects;

public class EmulatedURLStreamHandler extends URLStreamHandler {
    public EmulatedURLStreamHandler(@Nonnull EmulatedTranscation transcation)
    {
        this.transcation = Objects.requireNonNull(transcation);
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException
    {
        if (!transcation.available())
            throw new IOException("transcation closed");

        return new EmulatedURLConnection(transcation, u);
    }

    private final EmulatedTranscation transcation;
}
