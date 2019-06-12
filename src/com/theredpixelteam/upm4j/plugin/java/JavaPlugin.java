package com.theredpixelteam.upm4j.plugin.java;

import com.theredpixelteam.upm4j.plugin.Plugin;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;
import com.theredpixelteam.upm4j.plugin.PluginState;

import javax.annotation.Nonnull;

public class JavaPlugin implements Plugin {
    @Override
    public @Nonnull PluginState getState()
    {
        return null;
    }

    @Override
    public @Nonnull PluginAttribution getAttribution()
    {
        return null;
    }

    @Override
    public @Nonnull Class<?> getMainClass()
    {
        return null;
    }

    @Override
    public boolean isLoaded()
    {
        return false;
    }

    @Override
    public boolean isEnabled()
    {
        return false;
    }

    @Override
    public boolean load()
    {
        return false;
    }

    @Override
    public boolean unload()
    {
        return false;
    }

    @Override
    public boolean enable()
    {
        return false;
    }

    @Override
    public boolean disable()
    {
        return false;
    }
}
