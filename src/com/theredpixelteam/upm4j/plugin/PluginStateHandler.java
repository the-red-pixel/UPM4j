package com.theredpixelteam.upm4j.plugin;

import javax.annotation.Nonnull;

public interface PluginStateHandler {
    public @Nonnull PluginState trySetState(@Nonnull Plugin plugin, @Nonnull PluginState nextState);

    public boolean canLoad(@Nonnull Plugin plugin);

    public boolean canEnable(@Nonnull Plugin plugin);

    public boolean canDisable(@Nonnull Plugin plugin);

    public boolean canUnload(@Nonnull Plugin plugin);

    public boolean tryLoad(@Nonnull Plugin plugin);

    public boolean tryEnable(@Nonnull Plugin plugin);

    public boolean tryDisable(@Nonnull Plugin plugin);

    public boolean tryUnload(@Nonnull Plugin plugin);

    public boolean isEnabled(@Nonnull PluginState state);

    public boolean isDisabled(@Nonnull PluginState state);
}
