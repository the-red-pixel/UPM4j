package com.theredpixelteam.upm4j.loader.exception;

public class PluginVerificationException extends Exception {
    public PluginVerificationException()
    {
    }

    public PluginVerificationException(String msg)
    {
        super(msg);
    }

    public PluginVerificationException(Throwable cause)
    {
        super(cause);
    }

    public PluginVerificationException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
