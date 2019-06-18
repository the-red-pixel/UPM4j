package com.theredpixelteam.upm4j.loader.exception;

import javax.annotation.Nullable;

public class PluginSourceIOException extends PluginMountingException {
    public PluginSourceIOException(@Nullable String msg)
    {
        super(msg);
    }

    public PluginSourceIOException(@Nullable Throwable cause)
    {
        super(cause);
    }

    public PluginSourceIOException(@Nullable String msg,
                                   @Nullable Throwable cause)
    {
        super(msg, cause);
    }
}
