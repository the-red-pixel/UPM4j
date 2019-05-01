package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.source.PluginSource;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PluginDiscoverer {
    public PluginDiscoverer(@Nonnull UPMContext context,
                            @Nonnull PluginSource source)
    {
        this.source = Objects.requireNonNull(source, "source");
        this.classLoader = context.getClassLoaderProvider().provide();
        this.policy = context.getDiscoveringPolicy();
    }

    public @Nonnull PluginSource getSource()
    {
        return source;
    }

    public @Nonnull PluginDiscoveringPolicy getPolicy()
    {
        return policy;
    }

    public @Nonnull UPMClassLoader getClassLoader()
    {
        return classLoader;
    }

    private final PluginSource source;

    private final PluginDiscoveringPolicy policy;

    private final UPMClassLoader classLoader;
}
