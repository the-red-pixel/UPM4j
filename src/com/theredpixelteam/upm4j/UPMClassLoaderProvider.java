package com.theredpixelteam.upm4j;

import com.theredpixelteam.upm4j.loader.PluginClassLoaderPolicy;
import com.theredpixelteam.upm4j.loader.UPMClassLoader;

public interface UPMClassLoaderProvider {
    public UPMClassLoader provide();

    public PluginClassLoaderPolicy getPolicy();
}
