package com.theredpixelteam.upm4j;

import com.theredpixelteam.upm4j.loader.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class UPMGlobal {
    UPMGlobal(@Nonnull PluginClassLoaderPolicy classLoaderPolicy,
              @Nonnull PluginFileDiscoveringPolicy fileDiscoveringPolicy,
              @Nonnull PluginDiscoveringPolicy discoveringPolicy,
              @Nonnull PluginClassLoadingPolicy classLoadingPolicy,
              @Nonnull PluginInvocationPolicy invocationPolicy)
    {
        this.classLoaderPolicy = classLoaderPolicy;
        this.fileDiscoveringPolicy = fileDiscoveringPolicy;
        this.discoveringPolicy = discoveringPolicy;
        this.classLoadingPolicy = classLoadingPolicy;
        this.invocationPolicy = invocationPolicy;
    }

    public @Nonnull PluginClassLoaderPolicy getClassLoaderPolicy()
    {
        return classLoaderPolicy;
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

    public static @Nonnull Builder builder()
    {
        return new Builder();
    }

    private final PluginInvocationPolicy invocationPolicy;

    private final PluginFileDiscoveringPolicy fileDiscoveringPolicy;

    private final PluginDiscoveringPolicy discoveringPolicy;

    private final PluginClassLoadingPolicy classLoadingPolicy;

    private final PluginClassLoaderPolicy classLoaderPolicy;

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

        public @Nonnull Builder classLoaderPolicy(@Nonnull PluginClassLoaderPolicy classLoaderPolicy)
        {
            this.classLoaderPolicy = Objects.requireNonNull(classLoaderPolicy);
            return this;
        }

        public @Nonnull Builder discoveringPolicy(@Nonnull PluginDiscoveringPolicy discoveringPolicy)
        {
            this.discoveringPolicy = Objects.requireNonNull(discoveringPolicy);
            return this;
        }

        public @Nullable PluginClassLoaderPolicy getClassLoaderPolicy()
        {
            return classLoaderPolicy;
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

        public @Nonnull UPMGlobal build()
        {
            return new UPMGlobal(
                    Objects.requireNonNull(classLoaderPolicy, "ClassLoaderPolicy"),
                    Objects.requireNonNull(fileDiscoveringPolicy, "FileDiscoveringPolicy"),
                    Objects.requireNonNull(discoveringPolicy, "DiscoveringPolicy"),
                    Objects.requireNonNull(classLoadingPolicy, "ClassLoadingPolicy"),
                    Objects.requireNonNull(invocationPolicy, "InvocationPolicy")
            );
        }

        private PluginInvocationPolicy invocationPolicy;

        private PluginFileDiscoveringPolicy fileDiscoveringPolicy;

        private PluginDiscoveringPolicy discoveringPolicy;

        private PluginClassLoadingPolicy classLoadingPolicy;

        private PluginClassLoaderPolicy classLoaderPolicy;
    }
}
