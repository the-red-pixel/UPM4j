package com.theredpixelteam.upm4j.loader.exception;

import javax.annotation.Nullable;

public class PluginIdentityDuplicationException extends PluginMountingException {
    public PluginIdentityDuplicationException(@Nullable String msg)
    {
        super(msg);
    }
}
