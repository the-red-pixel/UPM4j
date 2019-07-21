package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.upm4j.UPMContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.WeakHashMap;

public class SimplePluginClassLoaderProvider implements PluginClassLoaderProvider {
    public SimplePluginClassLoaderProvider(@Nullable ClassLoader parent,
                                           boolean global,
                                           boolean checkBytesRef,
                                           boolean checkClassName)
    {
        this.parent = parent;
        this.global = global;
        this.checkBytesRef = checkBytesRef;
        this.checkClassName = checkClassName;

        this.globalMap = global ? new WeakHashMap<>() : null;
    }

    public SimplePluginClassLoaderProvider(boolean global,
                                           boolean checkBytesRef,
                                           boolean checkClassName)
    {
        this(null, global, checkBytesRef, checkClassName);
    }

    @Override
    public PluginClassLoader provide(@Nonnull UPMContext context)
    {
        if (global)
            return globalMap.computeIfAbsent(context,
                    ctx -> new PluginClassLoader(parent, ctx, checkBytesRef, checkClassName, true));

        return new PluginClassLoader(parent, context, checkBytesRef, checkClassName, false);
    }

    private final WeakHashMap<UPMContext, PluginClassLoader> globalMap;

    private final ClassLoader parent;

    private final boolean checkBytesRef;

    private final boolean checkClassName;

    private final boolean global;
}
