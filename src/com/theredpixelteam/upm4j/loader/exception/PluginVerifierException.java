package com.theredpixelteam.upm4j.loader.exception;

import com.theredpixelteam.upm4j.loader.PluginVerifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class PluginVerifierException extends PluginMountingException {
    public PluginVerifierException(@Nonnull PluginVerifier source)
    {
        this.source = Objects.requireNonNull(source);
    }

    public PluginVerifierException(@Nonnull PluginVerifier source,
                                   @Nullable String msg)
    {
        super(msg);
        this.source = Objects.requireNonNull(source);
    }

    public PluginVerifierException(@Nonnull PluginVerifier source,
                                   @Nullable Throwable cause)
    {
        super(cause);
        this.source = Objects.requireNonNull(source);
    }

    public PluginVerifierException(@Nonnull PluginVerifier source,
                                   @Nullable String msg,
                                   @Nullable Throwable cause)
    {
        super(msg, cause);
        this.source = Objects.requireNonNull(source);
    }

    public @Nonnull PluginVerifier getSource()
    {
        return source;
    }

    private final PluginVerifier source;
}
