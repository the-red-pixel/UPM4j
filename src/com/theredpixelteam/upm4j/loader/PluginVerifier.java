package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.redtea.util.Optional;

import javax.annotation.Nonnull;

public interface PluginVerifier {
    public @Nonnull String getIdentity();

    public @Nonnull String getName();

    public @Nonnull Optional<String> getDescription();

    // TODO
}
