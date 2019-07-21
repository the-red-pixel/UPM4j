package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.upm4j.UPMContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.WeakHashMap;

public class SimplePluginClassLoaderProvider implements PluginClassLoaderProvider {
    public SimplePluginClassLoaderProvider(@Nullable ClassLoader parent,
                                           boolean global,
                                           int options)
    {
        this.parent = parent;
        this.global = global;
        this.options = options;

        this.globalMap = global ? new WeakHashMap<>() : null;
    }

    public SimplePluginClassLoaderProvider(boolean global,
                                           int options)
    {
        this(null, global, options);
    }

    @Override
    public PluginClassLoader provide(@Nonnull UPMContext context)
    {
        if (global)
            return globalMap.computeIfAbsent(context,
                    ctx -> new PluginClassLoader(parent, ctx, true, options));

        return new PluginClassLoader(parent, context, false, options);
    }

    private final WeakHashMap<UPMContext, PluginClassLoader> globalMap;

    private final ClassLoader parent;

    private final int options;

    private final boolean global;
}
