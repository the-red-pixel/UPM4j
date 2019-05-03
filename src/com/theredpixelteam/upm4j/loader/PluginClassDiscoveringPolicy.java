package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.attribution.AttributionWorkflow;
import com.theredpixelteam.upm4j.loader.attribution.ConfiguratedAttributionProvider;
import com.theredpixelteam.upm4j.loader.attribution.FixedClassAttributionProvider;
import com.theredpixelteam.upm4j.loader.source.PluginSource;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;
import org.kucro3.jam2.util.Jam2Util;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

public abstract class PluginClassDiscoveringPolicy {
    PluginClassDiscoveringPolicy(Type type)
    {
        this.type = type;
    }

    public abstract @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                                  @Nonnull PluginSource source)
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
        CONFIGURATION_FILES,
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

        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull PluginSource source)
                throws IOException
        {
            Objects.requireNonNull(source);

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            for (String name : names)
            {
                String sourceName =
                        Jam2Util.fromInternalNameToResource(
                        Jam2Util.fromCanonicalToInternalName(name));

                source.getEntry(sourceName).ifPresent(
                        (entry) -> provider.provide(workflow, name, entry));
            }

            return workflow.buildAll();
        }

        private final FixedClassAttributionProvider provider;

        private final Collection<String> names;
    }

    public static class Configurated extends PluginClassDiscoveringPolicy
    {
        Configurated(@Nonnull ConfiguratedAttributionProvider provider)
        {
            super(Type.CONFIGURATED);

            this.provider = provider;
        }

        @Override
        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull PluginSource source)
                throws IOException
        {
            AttributionWorkflow workflow = new AttributionWorkflow(context);

            provider.provide(workflow, source);

            return workflow.buildAll();
        }

        private final ConfiguratedAttributionProvider provider;
    }

    // TODO
}
