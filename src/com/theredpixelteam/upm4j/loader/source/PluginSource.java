package com.theredpixelteam.upm4j.loader.source;

import com.theredpixelteam.redtea.function.BiConsumer;
import com.theredpixelteam.redtea.function.Consumer;
import com.theredpixelteam.redtea.util.FilteredIterator;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public interface PluginSource {
    public @Nonnull String getName();

    public @Nonnull Iterator<PluginSourceEntry> getEntries();

    public default @Nonnull
    Iterator<PluginSourceEntry> getEntries(@Nonnull SourceEntryFilter filter)
    {
        Objects.requireNonNull(filter);

        return FilteredIterator.of(getEntries(), filter::accept);
    }

    public default @Nonnull
    Iterator<PluginSourceEntry> getEntries(@Nonnull SourceEntryNameFilter filter)
    {
        return getEntries(filter, (entry, ioException) -> {/* mute */});
    }

    public default @Nonnull
    Iterator<PluginSourceEntry> getEntries(@Nonnull SourceEntryNameFilter filter,
                                           @Nonnull BiConsumer<PluginSourceEntry, IOException> ioExceptionHandler)
    {
        Objects.requireNonNull(filter);

        return FilteredIterator.of(getEntries(), (entry) -> {
            try {
                return filter.accept(entry.getName());
            } catch (IOException e) {
                ioExceptionHandler.accept(entry, e);
                return false;
            }
        });
    }

    public Optional<PluginSourceEntry> getEntry(@Nonnull String name);

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
