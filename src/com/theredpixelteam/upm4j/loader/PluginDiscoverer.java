package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.source.PluginSource;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PluginDiscoverer {
    public PluginDiscoverer(@Nonnull UPMContext context,
                            @Nonnull PluginSource source)
    {
        this.context = Objects.requireNonNull(context);

        this.source = Objects.requireNonNull(source, "source");
        this.classLoader = context.getClassLoaderProvider().provide();

        this.entryDiscoverer = context.getEntryDiscoverer();
        this.instancePolicy = context.getInstancePolicy();
    }

    public @Nonnull PluginSource getSource()
    {
        return source;
    }

    public @Nonnull
    PluginInstancePolicy getInstancePolicy()
    {
        return instancePolicy;
    }

    public @Nonnull
    PluginEntryDiscoverer getEntryDiscoverer()
    {
        return entryDiscoverer;
    }

    public @Nonnull UPMClassLoader getClassLoader()
    {
        return classLoader;
    }

    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public void discover()
    {

        // TODO
    }

    private final PluginSource source;

    private final PluginInstancePolicy instancePolicy;

    private final PluginEntryDiscoverer entryDiscoverer;

    private final UPMClassLoader classLoader;

    private final UPMContext context;
}
