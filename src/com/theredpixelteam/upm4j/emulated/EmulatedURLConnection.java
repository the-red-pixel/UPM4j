package com.theredpixelteam.upm4j.emulated;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class EmulatedURLConnection extends URLConnection {
    public EmulatedURLConnection(@Nonnull EmulatedTranscation transcation,
                                 @Nonnull URL url)
    {
        super(url);
        this.transcation = Objects.requireNonNull(transcation);
    }

    @Override
    public void connect() throws IOException
    {
        connected = true;
    }

    @Override
    public void setDoInput(boolean flag)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDoOutput(boolean flag)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getDoInput()
    {
        return transcation.doInput();
    }

    @Override
    public boolean getDoOutput()
    {
        return transcation.doOutput();
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return transcation.openInputStream().orElseThrow(() -> new IOException("Input operation not supported"));
    }

    @Override
    public OutputStream getOutputStream() throws IOException
    {
        return transcation.openOutputStream().orElseThrow(() -> new IOException("Output operation not supported"));
    }

    public @Nonnull EmulatedTranscation getTranscation()
    {
        return transcation;
    }

    private final EmulatedTranscation transcation;
}
