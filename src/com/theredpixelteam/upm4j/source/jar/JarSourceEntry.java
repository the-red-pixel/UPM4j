package com.theredpixelteam.upm4j.source.jar;

import com.theredpixelteam.redtea.util.io.IOUtils;
import com.theredpixelteam.upm4j.source.Source;
import com.theredpixelteam.upm4j.source.SourceEntry;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarSourceEntry implements SourceEntry {
    protected JarSourceEntry(@Nonnull JarSource source,
                             @Nonnull JarFile jar,
                             @Nonnull JarEntry entry)
    {
        this.source = Objects.requireNonNull(source, "source");
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


    @Override
    public @Nonnull
    Source getSource()
    {
        return source;
    }

    protected byte[] bytes = null;

    protected final JarSource source;

    protected final JarFile jar;

    protected final JarEntry entry;
}
