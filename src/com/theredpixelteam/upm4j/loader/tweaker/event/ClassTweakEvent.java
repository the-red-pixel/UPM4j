package com.theredpixelteam.upm4j.loader.tweaker.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.loader.tweaker.ClassTweaker;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class ClassTweakEvent implements UPMEvent {
    protected ClassTweakEvent(@Nonnull UPMContext context,
                              @Nonnull String className,
                              @Nonnull byte[] classBytes)
    {
        this.context = Objects.requireNonNull(context, "context");
        this.className = Objects.requireNonNull(className, "className");
        this.classBytes = Objects.requireNonNull(classBytes, "classBytes");
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public @Nonnull ByteBuffer getClassBytes()
    {
        return ByteBuffer.wrap(classBytes).asReadOnlyBuffer();
    }

    public @Nonnull String getClassName()
    {
        return className;
    }

    private final UPMContext context;

    private final byte[] classBytes;

    private final String className;

    public static abstract class Cancellable extends ClassTweakEvent implements UPMEvent.Cancellable
    {
        protected Cancellable(@Nonnull UPMContext context,
                              @Nonnull String className,
                              @Nonnull byte[] classBytes)
        {
            super(context, className, classBytes);
        }

        @Override
        public boolean isCancelled()
        {
            return cancelled;
        }

        @Override
        public void cancel()
        {
            this.cancelled = true;
        }

        private boolean cancelled;
    }

    public static class Start extends Cancellable
    {
        public Start(@Nonnull UPMContext context,
                     @Nonnull String className,
                     @Nonnull byte[] classBytes)
        {
            super(context, className, classBytes);
        }
    }

    public static class Cancelled extends ClassTweakEvent
    {
        public Cancelled(@Nonnull UPMContext context,
                         @Nonnull String className,
                         @Nonnull byte[] classBytes)
        {
            super(context, className, classBytes);
        }
    }

    public static abstract class TweakerEvent extends ClassTweakEvent
    {
        protected TweakerEvent(@Nonnull UPMContext context,
                               @Nonnull String className,
                               @Nonnull byte[] classBytes,
                               @Nonnull ClassTweaker tweaker)
        {
            super(context, className, classBytes);
            this.tweaker = Objects.requireNonNull(tweaker, "tweaker");
        }

        public @Nonnull ClassTweaker getTweaker()
        {
            return tweaker;
        }

        private final ClassTweaker tweaker;
    }

    public static abstract class CancellableTweakerEvent extends TweakerEvent implements UPMEvent.Cancellable
    {
        protected CancellableTweakerEvent(@Nonnull UPMContext context,
                                          @Nonnull String className,
                                          @Nonnull byte[] classBytes,
                                          @Nonnull ClassTweaker tweaker)
        {
            super(context, className, classBytes, tweaker);
        }

        @Override
        public boolean isCancelled()
        {
            return cancelled;
        }

        @Override
        public void cancel()
        {
            this.cancelled = true;
        }

        private boolean cancelled;
    }

    public static class TweakerEnter extends CancellableTweakerEvent
    {
        public TweakerEnter(@Nonnull UPMContext context,
                            @Nonnull String className,
                            @Nonnull byte[] classBytes,
                            @Nonnull ClassTweaker tweaker)
        {
            super(context, className, classBytes, tweaker);
        }
    }

    public static class TweakerCancelled extends TweakerEvent
    {
        public TweakerCancelled(@Nonnull UPMContext context,
                                @Nonnull String className,
                                @Nonnull byte[] classBytes,
                                @Nonnull ClassTweaker tweaker,
                                @Nonnull Cause cause)
        {
            super(context, className, classBytes, tweaker);
            this.cause = Objects.requireNonNull(cause, "cause");
        }

        public @Nonnull Cause getCause()
        {
            return cause;
        }

        private final Cause cause;

        public static enum Cause
        {
            EVENT,
            DEPENDENCY
        }
    }

    public static class TweakerFailure extends CancellableTweakerEvent
    {
        public TweakerFailure(@Nonnull UPMContext context,
                              @Nonnull String className,
                              @Nonnull byte[] classBytes,
                              @Nonnull ClassTweaker tweaker,
                              @Nonnull Exception exception)
        {
            super(context, className, classBytes, tweaker);
            this.exception = Objects.requireNonNull(exception, "exception");
        }

        public @Nonnull Exception getException()
        {
            return exception;
        }

        private final Exception exception;
    }

    public static class TweakerFailureIgnored extends TweakerEvent
    {
        public TweakerFailureIgnored(@Nonnull UPMContext context,
                                     @Nonnull String className,
                                     @Nonnull byte[] classBytes,
                                     @Nonnull ClassTweaker tweaker,
                                     @Nonnull Exception exception)
        {
            super(context, className, classBytes, tweaker);
            this.exception = Objects.requireNonNull(exception, "exception");
        }

        public @Nonnull Exception getException()
        {
            return exception;
        }

        private final Exception exception;
    }

    public static class TweakerIdenticalBytesRefWarning extends TweakerEvent
    {
        public TweakerIdenticalBytesRefWarning(@Nonnull UPMContext context,
                                                  @Nonnull String className,
                                                  @Nonnull byte[] classBytes,
                                                  @Nonnull ClassTweaker tweaker)
        {
            super(context, className, classBytes, tweaker);
        }
    }
}
