package com.theredpixelteam.upm4j.plugin;

import javax.annotation.Nonnull;

public interface Plugin {
    public @Nonnull PluginState getState();

    public @Nonnull PluginAttribution getAttribution();
}
