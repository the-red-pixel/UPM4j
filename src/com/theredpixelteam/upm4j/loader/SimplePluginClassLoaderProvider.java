package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.upm4j.UPMContext;

import javax.annotation.Nonnull;
import java.util.WeakHashMap;

public class SimplePluginClassLoaderProvider implements PluginClassLoaderProvider {
    public SimplePluginClassLoaderProvider(boolean global, boolean checkBytesRef)
    {
        this.global = global;
        this.checkBytesRef = checkBytesRef;

        this.globalMap = global ? new WeakHashMap<>() : null;
    }

    @Override
    public PluginClassLoader provide(@Nonnull UPMContext context)
    {
        if (global)
            return globalMap.computeIfAbsent(context,
                    ctx -> new PluginClassLoader(ctx, checkBytesRef, true));

        return new PluginClassLoader(context, checkBytesRef, false);
    }

    private final WeakHashMap<UPMContext, PluginClassLoader> globalMap;

    private final boolean checkBytesRef;

    private final boolean global;
}
