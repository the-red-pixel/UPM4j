package com.theredpixelteam.upm4j.plugin;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface PluginStateHandler {
    public @Nonnull PluginState trySetState(@Nonnull Plugin plugin,
                                            @Nonnull PluginState nextState);

    public @Nonnull PluginState getInitialState();

    public boolean canLoad(@Nonnull Plugin plugin);

    public boolean canEnable(@Nonnull Plugin plugin);

    public boolean canDisable(@Nonnull Plugin plugin);

    public boolean canUnload(@Nonnull Plugin plugin);

    public @Nonnull Optional<PluginState> tryLoad(@Nonnull Plugin plugin)
            throws PluginTargetException;

    public @Nonnull Optional<PluginState> tryEnable(@Nonnull Plugin plugin)
            throws PluginTargetException;

    public @Nonnull Optional<PluginState> tryDisable(@Nonnull Plugin plugin)
            throws PluginTargetException;

    public @Nonnull Optional<PluginState> tryUnload(@Nonnull Plugin plugin)
            throws PluginTargetException;

    public boolean isLoaded(@Nonnull PluginState state);

    public boolean isEnabled(@Nonnull PluginState state);
}
