package com.theredpixelteam.upm4j.loader.exception;

import javax.annotation.Nullable;

public class PluginMountingException extends Exception {
    public PluginMountingException()
    {
    }

    public PluginMountingException(@Nullable String msg)
    {
        super(msg);
    }

    public PluginMountingException(@Nullable Throwable cause)
    {
        super(cause);
    }

    public PluginMountingException(@Nullable String msg,
                                   @Nullable Throwable cause)
    {
        super(msg, cause);
    }

    protected PluginMountingException(@Nullable String message,
                                      @Nullable Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
