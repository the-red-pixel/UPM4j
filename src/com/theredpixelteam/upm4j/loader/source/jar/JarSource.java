package com.theredpixelteam.upm4j.loader.source.jar;

import com.theredpixelteam.redtea.function.Consumer;
import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.loader.source.Source;
import com.theredpixelteam.upm4j.loader.source.SourceEntry;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarSource implements Source {
    public JarSource(@Nonnull JarFile jar)
    {
        this.jar = Objects.requireNonNull(jar);
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

    protected final JarFile jar;
}
