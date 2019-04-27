package com.theredpixelteam.upm4j.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class UPMClassLoader extends URLClassLoader {
    public UPMClassLoader()
    {
        super(new URL[0]);
    }

    @Override
    public void addURL(URL url)
    {
        super.addURL(url);
    }
}
