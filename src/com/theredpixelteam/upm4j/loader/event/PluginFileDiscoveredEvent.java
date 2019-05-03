package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.event.AbstractCancellableUPMEvent;
import com.theredpixelteam.upm4j.loader.PluginFileDiscoverer;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Objects;

public class PluginFileDiscoveredEvent extends AbstractCancellableUPMEvent {
    public PluginFileDiscoveredEvent(@Nonnull PluginFileDiscoverer discoverer,
                                     @Nonnull File file)
    {
        super(discoverer.getContext());
        this.discoverer = Objects.requireNonNull(discoverer, "discoverer");
        this.file = Objects.requireNonNull(file, "file");
    }

    public @Nonnull PluginFileDiscoverer getDiscoverer()
    {
        return discoverer;
    }

    public @Nonnull File getFile()
    {
        return file;
    }

    private final PluginFileDiscoverer discoverer;

    private final File file;
}
