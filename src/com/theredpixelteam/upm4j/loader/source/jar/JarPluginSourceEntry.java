package com.theredpixelteam.upm4j.loader.source.jar;

import com.theredpixelteam.redtea.util.io.IOUtils;
import com.theredpixelteam.upm4j.loader.source.PluginSourceEntry;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarPluginSourceEntry implements PluginSourceEntry {
    protected JarPluginSourceEntry(@Nonnull JarFile jar,
                                   @Nonnull JarEntry entry)
    {
        this.jar = Objects.requireNonNull(jar, "jar");
        this.entry = Objects.requireNonNull(entry, "entry");
    }

    @Override
    public @Nonnull String getName()
    {
        return entry.getName();
    }

    @Override
    public @Nonnull byte[] getBytes() throws IOException
    {
        if (bytes != null)
            return bytes;

        return this.bytes = IOUtils.readFully(jar.getInputStream(entry));
    }

    protected byte[] bytes = null;

    protected final JarFile jar;

    protected final JarEntry entry;
}
