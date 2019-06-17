package com.theredpixelteam.upm4j.plugin;

import javax.annotation.Nullable;

public class PluginTargetException extends Exception {
    public PluginTargetException()
    {
    }

    public PluginTargetException(@Nullable String msg)
    {
        super(msg);
    }

    public PluginTargetException(@Nullable Throwable cause)
    {
        super(cause);
    }

    public PluginTargetException(@Nullable String msg,
                                 @Nullable Throwable cause)
    {
        super(msg, cause);
    }

    protected PluginTargetException(@Nullable String message,
                                    @Nullable Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
