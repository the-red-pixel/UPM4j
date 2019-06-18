package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.upm4j.UPMContext;

import javax.annotation.Nonnull;

public interface PluginClassLoaderProvider {
    public PluginClassLoader provide(@Nonnull UPMContext context);
}
