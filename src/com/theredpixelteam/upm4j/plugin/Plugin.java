package com.theredpixelteam.upm4j.plugin;

import com.theredpixelteam.redtea.util.Optional;

import javax.annotation.Nonnull;

public interface Plugin {
    public @Nonnull String getIdentity();

    public @Nonnull Optional<String> getName();
}
