package com.theredpixelteam.upm4j.emulated;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Optional;

@ThreadSafe
public interface EmulatedTranscation {
    public @Nonnull Emulated getRoot();

    public @Nonnull Optional<InputStream> openInputStream() throws IOException;

    public @Nonnull Optional<OutputStream> openOutputStream() throws IOException;

    public boolean doOutput();

    public boolean doInput();

    public default @Nonnull Optional<URL> getURL()
    {
        return Optional.of(EmulatedURLFactory.create(this));
    }

    public @Nonnull String getName();

    public @Nonnull String getPath();

    public boolean delete() throws IOException;

    public boolean create() throws IOException;

    public boolean exists();

    public boolean available();

    public void close() throws IOException;
}
