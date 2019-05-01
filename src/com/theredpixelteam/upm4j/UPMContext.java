package com.theredpixelteam.upm4j;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.loader.*;
import com.theredpixelteam.upm4j.plugin.PluginStateTree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class UPMContext {
    UPMContext(@Nullable String name,
               @Nonnull UPMClassLoaderProvider classLoaderProvider,
               @Nonnull PluginFileDiscoveringPolicy fileDiscoveringPolicy,
               @Nonnull PluginDiscoveringPolicy discoveringPolicy,
               @Nonnull PluginClassLoadingPolicy classLoadingPolicy,
               @Nonnull PluginInvocationPolicy invocationPolicy,
               @Nonnull PluginStateTree pluginStateTree)
    {
        this.name = name;
        this.classLoaderProvider = classLoaderProvider;
        this.fileDiscoveringPolicy = fileDiscoveringPolicy;
        this.discoveringPolicy = discoveringPolicy;
        this.classLoadingPolicy = classLoadingPolicy;
        this.invocationPolicy = invocationPolicy;
        this.pluginStateTree = pluginStateTree;
    }

    public @Nonnull UPMClassLoaderProvider getClassLoaderProvider()
    {
        return classLoaderProvider;
    }

    public @Nonnull PluginClassLoadingPolicy getClassLoadingPolicy()
    {
        return classLoadingPolicy;
    }

    public @Nonnull PluginDiscoveringPolicy getDiscoveringPolicy()
    {
        return discoveringPolicy;
    }

    public @Nonnull PluginFileDiscoveringPolicy getFileDiscoveringPolicy()
    {
        return fileDiscoveringPolicy;
    }

    public @Nonnull PluginInvocationPolicy getInvocationPolicy()
    {
        return invocationPolicy;
    }

    public @Nullable Optional<String> getName()
    {
        return Optional.ofNullable(name);
    }

    public static @Nonnull Builder builder()
    {
        return new Builder();
    }

    private final String name;

    private final PluginInvocationPolicy invocationPolicy;

    private final PluginFileDiscoveringPolicy fileDiscoveringPolicy;

    private final PluginDiscoveringPolicy discoveringPolicy;

    private final PluginClassLoadingPolicy classLoadingPolicy;

    private final UPMClassLoaderProvider classLoaderProvider;

    private final PluginStateTree pluginStateTree;

    public static final class Builder
    {
        Builder()
        {
        }

        public @Nonnull Builder invocationPolicy(@Nonnull PluginInvocationPolicy invocationPolicy)
        {
            this.invocationPolicy = Objects.requireNonNull(invocationPolicy);
            return this;
        }

        public @Nonnull Builder fileDiscoveringPolicy(@Nonnull PluginFileDiscoveringPolicy fileDiscoveringPolicy)
        {
            this.fileDiscoveringPolicy = Objects.requireNonNull(fileDiscoveringPolicy);
            return this;
        }

        public @Nonnull Builder classLoadingPolicy(@Nonnull PluginClassLoadingPolicy classLoadingPolicy)
        {
            this.classLoadingPolicy = Objects.requireNonNull(classLoadingPolicy);
            return this;
        }

        public @Nonnull Builder classLoaderPolicy(@Nonnull UPMClassLoaderProvider classLoaderProvider)
        {
            this.classLoaderProvider = Objects.requireNonNull(classLoaderProvider);
            return this;
        }

        public @Nonnull Builder discoveringPolicy(@Nonnull PluginDiscoveringPolicy discoveringPolicy)
        {
            this.discoveringPolicy = Objects.requireNonNull(discoveringPolicy);
            return this;
        }

        public @Nonnull Builder pluginStateTree(@Nonnull PluginStateTree pluginStateTree)
        {
            this.pluginStateTree = pluginStateTree;
            return this;
        }

        public @Nonnull Builder name(@Nullable String name)
        {
            this.name = name;
            return this;
        }

        public @Nullable String getName()
        {
            return name;
        }

        public @Nullable UPMClassLoaderProvider getClassLoaderProvider()
        {
            return classLoaderProvider;
        }

        public @Nullable PluginClassLoadingPolicy getClassLoadingPolicy()
        {
            return classLoadingPolicy;
        }

        public @Nullable PluginDiscoveringPolicy getDiscoveringPolicy()
        {
            return discoveringPolicy;
        }

        public @Nullable PluginFileDiscoveringPolicy getFileDiscoveringPolicy()
        {
            return fileDiscoveringPolicy;
        }

        public @Nullable PluginInvocationPolicy getInvocationPolicy()
        {
            return invocationPolicy;
        }

        public @Nonnull PluginStateTree getPluginStateTree()
        {
            return pluginStateTree;
        }

        public @Nonnull UPMContext build()
        {
            return new UPMContext(
                    name,
                    Objects.requireNonNull(classLoaderProvider, "ClassLoaderPolicy"),
                    Objects.requireNonNull(fileDiscoveringPolicy, "FileDiscoveringPolicy"),
                    Objects.requireNonNull(discoveringPolicy, "DiscoveringPolicy"),
                    Objects.requireNonNull(classLoadingPolicy, "ClassLoadingPolicy"),
                    Objects.requireNonNull(invocationPolicy, "InvocationPolicy"),
                    Objects.requireNonNull(pluginStateTree, "PluginStateTree")
            );
        }

        private String name;

        private PluginInvocationPolicy invocationPolicy;

        private PluginFileDiscoveringPolicy fileDiscoveringPolicy;

        private PluginDiscoveringPolicy discoveringPolicy;

        private PluginClassLoadingPolicy classLoadingPolicy;

        private UPMClassLoaderProvider classLoaderProvider;

        private PluginStateTree pluginStateTree;
    }
}
