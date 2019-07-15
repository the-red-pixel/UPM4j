package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.loader.PluginClassLoader;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class PluginClassLoaderEvent implements UPMEvent {
    protected PluginClassLoaderEvent(@Nonnull PluginClassLoader classLoader)
    {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return classLoader.getContext();
    }

    public @Nonnull
    PluginClassLoader getClassLoader()
    {
        return classLoader;
    }

    private final PluginClassLoader classLoader;

    public static class ClassMountFailure extends PluginClassLoaderEvent
    {
        public ClassMountFailure(@Nonnull PluginClassLoader classLoader,
                                 @Nonnull String className,
                                 @Nonnull byte[] byts,
                                 @Nonnull Exception cause)
        {
            super(classLoader);
            this.className = Objects.requireNonNull(className, "className");
            this.byts = Objects.requireNonNull(byts, "classBytes");
            this.cause = Objects.requireNonNull(cause, "cause");
        }

        public @Nonnull String getClassName()
        {
            return className;
        }

        public @Nonnull ByteBuffer getClassBytes()
        {
            return ByteBuffer.wrap(byts).asReadOnlyBuffer();
        }

        public @Nonnull Exception getCause()
        {
            return cause;
        }

        private final String className;

        private final byte[] byts;

        private final Exception cause;
    }

    public static class ClassNameChanged extends PluginClassLoaderEvent
    {
        public ClassNameChanged(@Nonnull PluginClassLoader classLoader,
                                @Nonnull String oldClassName,
                                @Nonnull String newClassName,
                                @Nonnull byte[] byts)
        {
            super(classLoader);

            this.oldClassName = Objects.requireNonNull(oldClassName, "oldClassName");
            this.newClassName = Objects.requireNonNull(newClassName, "newClassName");
            this.byts = Objects.requireNonNull(byts, "classBytes");
        }

        public void reject()
        {
            rejected = true;
        }

        public boolean isRejected()
        {
            return rejected;
        }

        public @Nonnull String getOldClassName()
        {
            return oldClassName;
        }

        public @Nonnull String getNewClassName()
        {
            return newClassName;
        }

        public @Nonnull byte[] getClassBytes()
        {
            return byts;
        }

        private boolean rejected;

        private final String oldClassName;

        private final String newClassName;

        private final byte[] byts;
    }

    public static class ClassNameChangeRejected extends PluginClassLoaderEvent
    {
        public ClassNameChangeRejected(@Nonnull PluginClassLoader classLoader,
                                       @Nonnull String oldClassName,
                                       @Nonnull String newClassName,
                                       @Nonnull byte[] byts)
        {
            super(classLoader);

            this.oldClassName = Objects.requireNonNull(oldClassName, "oldClassName");
            this.newClassName = Objects.requireNonNull(newClassName, "newClassName");
            this.byts = Objects.requireNonNull(byts, "classBytes");
        }

        public @Nonnull String getOldClassName()
        {
            return oldClassName;
        }

        public @Nonnull String getNewClassName()
        {
            return newClassName;
        }

        public @Nonnull byte[] getClassBytes()
        {
            return byts;
        }

        private final String oldClassName;

        private final String newClassName;

        private final byte[] byts;
    }

    public static class ClassMountPassed extends PluginClassLoaderEvent
    {
        public ClassMountPassed(@Nonnull PluginClassLoader classLoader,
                                @Nonnull Class<?> classInstance)
        {
            super(classLoader);

            this.classInstance = Objects.requireNonNull(classInstance, "classInstance");
        }

        public @Nonnull Class<?> getClassInstance()
        {
            return classInstance;
        }

        private final Class<?> classInstance;
    }

    public static class PackageDefinitionFailure extends PluginClassLoaderEvent
    {
        public PackageDefinitionFailure(@Nonnull PluginClassLoader classLoader,
                                        @Nonnull String packageName,
                                        @Nonnull Exception cause)
        {
            super(classLoader);

            this.packageName = Objects.requireNonNull(packageName, "package");
            this.cause = Objects.requireNonNull(cause, "cause");
        }

        public @Nonnull String getPackageName()
        {
            return packageName;
        }

        public @Nonnull Exception getCause()
        {
            return cause;
        }

        private final String packageName;

        private final Exception cause;
    }

    public static class PackageDefinitionPassed extends PluginClassLoaderEvent
    {
        public PackageDefinitionPassed(@Nonnull PluginClassLoader classLoader,
                                       @Nonnull Package pack)
        {
            super(classLoader);

            this.pack = Objects.requireNonNull(pack, "package");
        }

        public @Nonnull Package getPackage()
        {
            return pack;
        }

        private final Package pack;
    }
}
