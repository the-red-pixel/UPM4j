package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.upm4j.loader.attribution.FixedClassAttributionProvider;
import com.theredpixelteam.upm4j.loader.source.PluginSource;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;
import org.kucro3.jam2.util.Jam2Util;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class PluginClassDiscoveringPolicy {
    PluginClassDiscoveringPolicy(Type type)
    {
        this.type = type;
    }

    public abstract @Nonnull Collection<PluginAttribution> search(@Nonnull PluginSource source)
            throws IOException;

    public @Nonnull Type getType()
    {
        return type;
    }

    private final Type type;

    public static enum Type
    {
        FIXED,
        CONFIGURATED,
        SCAN_ANNOTATION,
        SCAN_SUBCLASS
    }

    public static class Fixed extends PluginClassDiscoveringPolicy
    {
        Fixed(@Nonnull Collection<String> names,
              @Nonnull FixedClassAttributionProvider provider)
        {
            super(Type.FIXED);

            this.names = Objects.requireNonNull(names, "names");
            this.provider = Objects.requireNonNull(provider, "provider");
        }

        public @Nonnull Collection<String> getClassNames()
        {
            return names;
        }

        public @Nonnull Collection<PluginAttribution> search(@Nonnull PluginSource source)
                throws IOException
        {
            Objects.requireNonNull(source);

            List<PluginAttribution> attributions = new ArrayList<>();

            for (String name : names)
            {
                String sourceName =
                        Jam2Util.fromInternalNameToResource(
                        Jam2Util.fromCanonicalToInternalName(name));

                source.getEntry(name).ifPresent((entry) -> attributions.addAll(provider.provide(name, entry)));
            }

            return attributions;
        }

        private final FixedClassAttributionProvider provider;

        private final Collection<String> names;
    }

    // TODO
}
