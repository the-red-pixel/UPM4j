package com.theredpixelteam.upm4j.loader.source;

import com.theredpixelteam.redtea.function.BiConsumer;
import com.theredpixelteam.redtea.function.Consumer;
import com.theredpixelteam.redtea.util.FilteredIterator;
import com.theredpixelteam.redtea.util.Optional;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

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

    public Optional<SourceEntry> getEntry(@Nonnull String name);

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
