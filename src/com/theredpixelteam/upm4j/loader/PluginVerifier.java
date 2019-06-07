package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.loader.exception.PluginVerificationException;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;

public interface PluginVerifier {
    public @Nonnull String getIdentity();

    public @Nonnull String getName();

    public @Nonnull Optional<String> getDescription();

    public Result verify(@Nonnull PluginAttribution attribution)
            throws PluginVerificationException;

    public static class Result
    {

    }
}
