package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.event.AbstractCancellableUPMEvent;
import com.theredpixelteam.upm4j.loader.PluginDiscoverer;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PluginDiscoveredEvent extends AbstractCancellableUPMEvent {
    public PluginDiscoveredEvent(PluginDiscoverer discoverer, PluginAttribution discovered)
    {
        super(discoverer.getContext());
        this.discoverer = Objects.requireNonNull(discoverer, "discoverer");
        this.discovered = Objects.requireNonNull(discovered, "discovered");
    }

    public @Nonnull PluginAttribution getDiscovered()
    {
        return discovered;
    }

    public @Nonnull PluginDiscoverer getDiscoverer()
    {
        return discoverer;
    }

    private final PluginDiscoverer discoverer;

    private final PluginAttribution discovered;
}
