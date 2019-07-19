package com.theredpixelteam.upm4j.emulated.file;

import com.theredpixelteam.upm4j.emulated.Emulated;
import com.theredpixelteam.upm4j.emulated.EmulatedTranscation;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.*;
import java.util.Objects;
import java.util.Optional;

@ThreadSafe
public class FileEmulatedTranscation implements EmulatedTranscation {
    public FileEmulatedTranscation(@Nonnull FileEmulated root,
                                   @Nonnull File file,
                                   @Nonnull String path,
                                   @Nonnull String name)
    {
        this.root = Objects.requireNonNull(root, "root");
        this.file = Objects.requireNonNull(file, "file");
        this.path = Objects.requireNonNull(path, "path");
        this.name = Objects.requireNonNull(name, "name");
    }

    @Override
    public @Nonnull Emulated getRoot()
    {
        return root;
    }

    @Override
    public @Nonnull Optional<InputStream> openInputStream() throws IOException
    {
        checkAvailablity();

        if (inputStream == null && outputStream == null)
            synchronized (streamLock)
            {
                checkAvailablity();

                if (inputStream == null && outputStream == null)
                    return Optional.of(inputStream = new MonitoredInputStream(new FileInputStream(file)));
            }

        return Optional.empty();
    }

    @Override
    public @Nonnull Optional<OutputStream> openOutputStream() throws IOException
    {
        checkAvailablity();

        if (inputStream == null && outputStream == null)
            synchronized (streamLock)
            {
                checkAvailablity();

                if (inputStream == null && outputStream == null)
                    return Optional.of(outputStream = new MonitoredOutputStream(new FileOutputStream(file)));
            }

        return Optional.empty();
    }

    @Override
    public boolean doOutput()
    {
        return true;
    }

    @Override
    public boolean doInput()
    {
        return true;
    }

    @Override
    public @Nonnull String getName()
    {
        return name;
    }

    @Override
    public @Nonnull String getPath()
    {
        return path;
    }

    @Override
    public boolean delete() throws IOException
    {
        return file.delete();
    }

    @Override
    public boolean create() throws IOException
    {
        return file.createNewFile();
    }

    @Override
    public boolean exists()
    {
        return file.exists();
    }

    @Override
    public boolean available()
    {
        return available;
    }

    @Override
    public void close() throws IOException
    {
        if (!available)
            return;

        synchronized (streamLock)
        {
            if (outputStream != null)
                outputStream.close();
            else if (inputStream != null)
                inputStream.close();
        }

        available = false;

        root.unregister(this);
    }

    void enable()
    {
        available = true;
    }

    void checkAvailablity() throws IOException
    {
        if (!available)
            throw new IOException("Transcation closed");
    }

    private volatile MonitoredInputStream inputStream;

    private volatile MonitoredOutputStream outputStream;

    private final FileEmulated root;

    private final File file;

    private final String name;

    private final String path;

    private volatile boolean available;

    private final Object streamLock = new Object();

    private class MonitoredInputStream extends InputStream
    {
        public MonitoredInputStream(@Nonnull InputStream is)
        {
            this.is = Objects.requireNonNull(is);
        }

        @Override
        public int read() throws IOException
        {
            checkAvailablity();

            return is.read();
        }

        @Override
        public int read(@Nonnull byte[] byts) throws IOException
        {
            checkAvailablity();

            return is.read(byts);
        }

        @Override
        public int read(@Nonnull byte[] byts, int off, int len) throws IOException
        {
            checkAvailablity();

            return is.read(byts, off, len);
        }

        @Override
        public long skip(long n) throws IOException
        {
            checkAvailablity();

            return is.skip(n);
        }

        @Override
        public int available() throws IOException
        {
            checkAvailablity();

            return is.available();
        }

        @Override
        public void mark(int readlimit)
        {
            is.mark(readlimit);
        }

        @Override
        public void reset() throws IOException
        {
            checkAvailablity();

            is.reset();
        }

        @Override
        public boolean markSupported()
        {
            return is.markSupported();
        }

        @Override
        public void close() throws IOException
        {
            is.close();

            FileEmulatedTranscation.this.inputStream = null;
        }

        private final InputStream is;
    }

    private class MonitoredOutputStream extends OutputStream
    {
        public MonitoredOutputStream(@Nonnull OutputStream os)
        {
            this.os = Objects.requireNonNull(os);
        }

        @Override
        public void write(int b) throws IOException
        {
            checkAvailablity();

            os.write(b);
        }

        @Override
        public void write(@Nonnull byte[] byts) throws IOException
        {
            checkAvailablity();

            os.write(byts);
        }

        @Override
        public void write(@Nonnull byte[] byts, int off, int len) throws IOException
        {
            checkAvailablity();

            os.write(byts, off, len);
        }

        @Override
        public void flush() throws IOException
        {
            checkAvailablity();

            os.flush();
        }

        @Override
        public void close() throws IOException
        {
            os.close();

            FileEmulatedTranscation.this.outputStream = null;
        }

        private final OutputStream os;
    }
}
