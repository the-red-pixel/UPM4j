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

        this.classPolicy = context.getClassDiscoveringPolicy();
        this.instancePolicy = context.getInstanceDiscoveringPolicy();
    }

    public @Nonnull PluginSource getSource()
    {
        return source;
    }

    public @Nonnull PluginInstanceDiscoveringPolicy getInstancePolicy()
    {
        return instancePolicy;
    }

    public @Nonnull PluginClassDiscoveringPolicy getClassPolicy()
    {
        return classPolicy;
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
        boolean single = !PluginInstanceDiscoveringPolicy.SINGLE.equals(instancePolicy);
        boolean restricted = PluginInstanceDiscoveringPolicy.RESTRICTED_SINGLE.equals(instancePolicy);

        // TODO
    }

    private final PluginSource source;

    private final PluginInstanceDiscoveringPolicy instancePolicy;

    private final PluginClassDiscoveringPolicy classPolicy;

    private final UPMClassLoader classLoader;

    private final UPMContext context;
}
