package com.theredpixelteam.upm4j;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.redtea.util.ShouldNotReachHere;
import com.theredpixelteam.upm4j.invoke.ClassicInvokerProviders;
import com.theredpixelteam.upm4j.invoke.InvokerProvider;
import com.theredpixelteam.upm4j.loader.*;
import com.theredpixelteam.upm4j.plugin.PluginStateTree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings("all")
public class UPMContext {
    UPMContext(@Nullable String name,
               @Nonnull UPMClassLoaderProvider classLoaderProvider,
               @Nonnull PluginFileDiscoveringPolicy fileDiscoveringPolicy,
               @Nonnull PluginInstanceDiscoveringPolicy discoveringPolicy,
               @Nonnull PluginClassDiscoveringPolicy classDiscoveringPolicy,
               @Nonnull InvokerProvider invokerProvider,
               @Nonnull PluginStateTree pluginStateTree,
               @Nullable SubscriberExceptionHandler eventBusExceptionHandler)
    {
        this.name = name;
        this.classLoaderProvider = classLoaderProvider;
        this.fileDiscoveringPolicy = fileDiscoveringPolicy;
        this.discoveringPolicy = discoveringPolicy;
        this.classDiscoveringPolicy = classDiscoveringPolicy;
        this.invokerProvider = invokerProvider;
        this.pluginStateTree = pluginStateTree;
        this.eventBus = eventBusExceptionHandler == null ?
                new EventBus() : new EventBus(eventBusExceptionHandler);
    }

    public @Nonnull UPMClassLoaderProvider getClassLoaderProvider()
    {
        return classLoaderProvider;
    }

    public @Nonnull PluginClassDiscoveringPolicy getClassDiscoveringPolicy()
    {
        return classDiscoveringPolicy;
    }

    public @Nonnull
    PluginInstanceDiscoveringPolicy getInstanceDiscoveringPolicy()
    {
        return discoveringPolicy;
    }

    public @Nonnull PluginFileDiscoveringPolicy getFileDiscoveringPolicy()
    {
        return fileDiscoveringPolicy;
    }

    public @Nonnull InvokerProvider getInvokerProvider()
    {
        return invokerProvider;
    }

    public @Nonnull PluginStateTree getPluginStateTree()
    {
        return pluginStateTree;
    }

    public @Nonnull Optional<String> getName()
    {
        return Optional.ofNullable(name);
    }

    public @Nonnull EventBus getEventBus()
    {
        return eventBus;
    }

    public static @Nonnull Builder builder()
    {
        return new Builder();
    }

    private final EventBus eventBus;

    private final String name;

    private final InvokerProvider invokerProvider;

    private final PluginFileDiscoveringPolicy fileDiscoveringPolicy;

    private final PluginInstanceDiscoveringPolicy discoveringPolicy;

    private final PluginClassDiscoveringPolicy classDiscoveringPolicy;

    private final UPMClassLoaderProvider classLoaderProvider;

    private final PluginStateTree pluginStateTree;

    public static final class Builder
    {
        Builder()
        {
        }

        public @Nonnull Builder invocationPolicy(@Nonnull PluginInvocationPolicy invocationPolicy)
        {
            Objects.requireNonNull(invocationPolicy);

            switch (invocationPolicy)
            {
                case ASM_INVOKE:
                    return invokerProvider(ClassicInvokerProviders.ASM);

                case REFLECTION:
                    return invokerProvider(ClassicInvokerProviders.REFLECTION);
            }

            throw new ShouldNotReachHere();
        }

        public @Nonnull Builder invokerProvider(@Nonnull InvokerProvider invokerProvider)
        {
            this.invokerProvider = Objects.requireNonNull(invokerProvider);
            return this;
        }

        public @Nonnull Builder fileDiscoveringPolicy(@Nonnull PluginFileDiscoveringPolicy fileDiscoveringPolicy)
        {
            this.fileDiscoveringPolicy = Objects.requireNonNull(fileDiscoveringPolicy);
            return this;
        }

        public @Nonnull Builder classDiscoveringPolicy(@Nonnull PluginClassDiscoveringPolicy classDiscoveringPolicy)
        {
            this.classDiscoveringPolicy = Objects.requireNonNull(classDiscoveringPolicy);
            return this;
        }

        public @Nonnull Builder classLoaderPolicy(@Nonnull UPMClassLoaderProvider classLoaderProvider)
        {
            this.classLoaderProvider = Objects.requireNonNull(classLoaderProvider);
            return this;
        }

        public @Nonnull Builder instanceDiscoveringPolicy(@Nonnull PluginInstanceDiscoveringPolicy discoveringPolicy)
        {
            this.instanceDiscoveringPolicy = Objects.requireNonNull(discoveringPolicy);
            return this;
        }

        public @Nonnull Builder pluginStateTree(@Nonnull PluginStateTree pluginStateTree)
        {
            this.pluginStateTree = Objects.requireNonNull(pluginStateTree);
            return this;
        }

        public @Nonnull Builder name(@Nullable String name)
        {
            this.name = name;
            return this;
        }

        public @Nullable Builder eventBusExceptionHandler(@Nullable SubscriberExceptionHandler handler)
        {
            this.eventBusExceptionHandler = handler;
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

        public @Nullable PluginClassDiscoveringPolicy getClassDiscoveringPolicy()
        {
            return classDiscoveringPolicy;
        }

        public @Nullable
        PluginInstanceDiscoveringPolicy getDiscoveringPolicy()
        {
            return instanceDiscoveringPolicy;
        }

        public @Nullable PluginFileDiscoveringPolicy getFileDiscoveringPolicy()
        {
            return fileDiscoveringPolicy;
        }

        public @Nullable InvokerProvider getInvokerProvider()
        {
            return invokerProvider;
        }

        public @Nullable SubscriberExceptionHandler getEventBusExceptionHandler()
        {
            return eventBusExceptionHandler;
        }

        public @Nullable PluginStateTree getPluginStateTree()
        {
            return pluginStateTree;
        }

        public @Nonnull UPMContext build()
        {
            return new UPMContext(
                    name,
                    Objects.requireNonNull(classLoaderProvider, "ClassLoaderPolicy"),
                    Objects.requireNonNull(fileDiscoveringPolicy, "FileDiscoveringPolicy"),
                    Objects.requireNonNull(instanceDiscoveringPolicy, "InstanceDiscoveringPolicy"),
                    Objects.requireNonNull(classDiscoveringPolicy, "ClassDiscoveringPolicy"),
                    Objects.requireNonNull(invokerProvider, "InvokerProvider"),
                    Objects.requireNonNull(pluginStateTree, "PluginStateTree"),
                    eventBusExceptionHandler
            );
        }

        private String name;

        private SubscriberExceptionHandler eventBusExceptionHandler;

        private InvokerProvider invokerProvider;

        private PluginFileDiscoveringPolicy fileDiscoveringPolicy;

        private PluginInstanceDiscoveringPolicy instanceDiscoveringPolicy;

        private PluginClassDiscoveringPolicy classDiscoveringPolicy;

        private UPMClassLoaderProvider classLoaderProvider;

        private PluginStateTree pluginStateTree;
    }
}
