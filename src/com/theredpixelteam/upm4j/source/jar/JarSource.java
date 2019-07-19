package com.theredpixelteam.upm4j.source.jar;

import com.theredpixelteam.redtea.function.Consumer;
import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.source.Source;
import com.theredpixelteam.upm4j.source.SourceEntry;
import com.theredpixelteam.upm4j.source.SourceURLFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarSource implements Source {
    public JarSource(@Nonnull JarFile jar)
    {
        this(jar, null);
    }

    public JarSource(@Nonnull String name) throws IOException
    {
        this(new File(name));
    }

    public JarSource(@Nonnull String name, boolean verify) throws IOException
    {
        this(new File(name), verify);
    }

    public JarSource(@Nonnull File file) throws IOException
    {
        this(new JarFile(file), file.toURI().toURL());
    }

    public JarSource(@Nonnull File file, boolean verify) throws IOException
    {
        this(new JarFile(file, verify), file.toURI().toURL());
    }

    JarSource(@Nonnull JarFile jar, @Nullable URL url)
    {
        this.jar = Objects.requireNonNull(jar);
        this.url = url == null ? SourceURLFactory.create("jarsource", getName(), this) : url;
    }

    @Override
    public @Nonnull String getName()
    {
        return jar.getName();
    }

    @Override
    public @Nonnull Iterator<SourceEntry> getEntries()
    {
        if (!polled)
            poll();

        return Collections.unmodifiableCollection(entries.values()).iterator();
    }

    @Override
    public Optional<SourceEntry> getEntry(@Nonnull String name)
    {
        if (!polled)
            poll();

        return Optional.ofNullable(entries.get(Objects.requireNonNull(name)));
    }

    @Override
    public @Nonnull Optional<URL> getURL()
    {
        return Optional.of(url);
    }

    @Override
    public @Nonnull Optional<Manifest> getManifest()
            throws IOException
    {
        return Optional.of(this.jar.getManifest());
    }

    @Override
    public boolean close(@Nonnull Consumer<IOException> ioExceptionHandler)
    {
        Objects.requireNonNull(ioExceptionHandler);

        try {
            jar.close();

            return true;
        } catch (IOException e) {
            ioExceptionHandler.accept(e);

            return false;
        }
    }

    private synchronized void poll()
    {
        Enumeration<JarEntry> entryEnumeration = jar.entries();

        while (entryEnumeration.hasMoreElements())
        {
            JarEntry entry = entryEnumeration.nextElement();

            entries.put(entry.getName(), new JarSourceEntry(this, jar, entry));
        }

        polled = true;
    }

    private final Map<String, SourceEntry> entries = new HashMap<>();

    private volatile boolean polled = false;

    private final URL url;

    protected final JarFile jar;
}
