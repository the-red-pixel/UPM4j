package com.theredpixelteam.upm4j.loader;

public interface UPMClassLoaderProvider {
    public UPMClassLoader provide();

    public PluginClassLoaderPolicy getPolicy();
}
