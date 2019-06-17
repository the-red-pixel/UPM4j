package com.theredpixelteam.upm4j.plugin;

import com.theredpixelteam.upm4j.UPMContext;

import javax.annotation.Nonnull;

public interface Plugin {
    public @Nonnull PluginState getState();

    public @Nonnull PluginAttribution getAttribution();

    public @Nonnull Class<?> getMainClass();

    public boolean isLoaded();

    public boolean isEnabled();

    public boolean load();

    public boolean unload();

    public boolean enable();

    public boolean disable();

    public @Nonnull UPMContext getContext();
}
