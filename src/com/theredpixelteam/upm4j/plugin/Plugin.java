package com.theredpixelteam.upm4j.plugin;

import javax.annotation.Nonnull;

public interface Plugin {
    public @Nonnull PluginState getState();

    public @Nonnull PluginAttribution getAttribution();

    public boolean isLoaded();

    public boolean isEnabled();

    public boolean isDisabled();

    public boolean isUnloaded();

    public boolean load();

    public boolean unload();

    public boolean enable();

    public boolean disable();
}
