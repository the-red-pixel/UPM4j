package com.theredpixelteam.upm4j.loader.exception;

import javax.annotation.Nullable;

public class PluginInstancePolicyViolationException extends PluginMountingException {
    public PluginInstancePolicyViolationException(@Nullable String msg)
    {
        super(msg);
    }
}
