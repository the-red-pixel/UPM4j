package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.loader.UPMClassLoader;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.Objects;

public class UPMClassLoaderEvent implements UPMEvent {
    protected UPMClassLoaderEvent(@Nonnull UPMClassLoader classLoader)
    {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return classLoader.getContext();
    }

    public @Nonnull UPMClassLoader getClassLoader()
    {
        return classLoader;
    }

    private final UPMClassLoader classLoader;

    public static class ClassMountFailure extends UPMClassLoaderEvent
    {
        public ClassMountFailure(@Nonnull UPMClassLoader classLoader,
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

        public @Nonnull ByteBuffer getClassByts()
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
}
