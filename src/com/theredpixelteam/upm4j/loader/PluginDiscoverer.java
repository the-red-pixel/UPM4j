package com.theredpixelteam.upm4j.loader;

import java.io.File;

public class PluginDiscoverer {
    public PluginDiscoverer(File file, UPMClassLoader loader, PluginDiscoveringPolicy policy)
    {
        this.file = file;
    }

    public File getFile()
    {
        return file;
    }

    private final File file;
}
