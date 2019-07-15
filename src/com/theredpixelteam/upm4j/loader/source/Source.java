package com.theredpixelteam.upm4j.loader.source;

import com.theredpixelteam.redtea.function.BiConsumer;
import com.theredpixelteam.redtea.function.Consumer;
import com.theredpixelteam.redtea.util.FilteredIterator;
import com.theredpixelteam.redtea.util.Optional;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public interface Source {
    public @Nonnull String getName();

    public @Nonnull Iterator<SourceEntry> getEntries();

    public default @Nonnull
    Iterable<SourceEntry> getEntries(@Nonnull SourceEntryFilter filter)
    {
        Objects.requireNonNull(filter);

        return () -> FilteredIterator.of(getEntries(), filter::accept);
    }

    public default @Nonnull
    Iterable<SourceEntry> getEntries(@Nonnull SourceEntryNameFilter filter)
    {
        return getEntries(filter, (entry, ioException) -> {/* mute */});
    }

    public default @Nonnull
    Iterable<SourceEntry> getEntries(@Nonnull SourceEntryNameFilter filter,
                                     @Nonnull BiConsumer<SourceEntry, IOException> ioExceptionHandler)
    {
        Objects.requireNonNull(filter);

        return () -> FilteredIterator.of(getEntries(), (entry) -> {
            try {
                return filter.accept(entry.getName());
            } catch (IOException e) {
                ioExceptionHandler.accept(entry, e);
                return false;
            }
        });
    }

    public default @Nonnull Optional<SourceEntry> getManifestEntry()
    {
        Optional<SourceEntry> entry = getEntry(JarFile.MANIFEST_NAME);

        if (entry.isPresent())
            return Optional.of(entry.get());

        return Optional.empty();
    }

    public default @Nonnull Optional<Manifest> getManifest() throws IOException
    {
        Optional<SourceEntry> sourceEntry = getManifestEntry();

        if (sourceEntry.isPresent())
            return Optional.of(new Manifest(new ByteArrayInputStream(sourceEntry.get().getBytes())));

        return Optional.empty();
    }

    public default @Nonnull Optional<URL> getURL()
    {
        return Optional.empty();
    }

    public @Nonnull Optional<SourceEntry> getEntry(@Nonnull String name);

    public default boolean hasEntry(@Nonnull String name)
    {
        return getEntry(name).isPresent();
    }

    public default boolean close()
    {
        return close((ioException) -> {});
    }

    public boolean close(@Nonnull Consumer<IOException> ioExceptionHandler);
}
