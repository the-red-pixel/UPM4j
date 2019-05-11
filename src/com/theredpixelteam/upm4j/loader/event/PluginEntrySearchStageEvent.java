package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.loader.PluginClassDiscoveringPolicy;
import com.theredpixelteam.upm4j.loader.attribution.processor.Barrier;
import com.theredpixelteam.upm4j.loader.source.PluginSourceEntry;
import org.objectweb.asm.tree.AnnotationNode;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Objects;

public abstract class PluginEntrySearchStageEvent implements UPMEvent {
    PluginEntrySearchStageEvent(@Nonnull UPMContext context,
                                @Nonnull PluginClassDiscoveringPolicy policy,
                                @Nonnull Barrier barrier)
    {
        this.context = Objects.requireNonNull(context, "context");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.barrier = Objects.requireNonNull(barrier, "barrier");
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public @Nonnull PluginClassDiscoveringPolicy getPolicy()
    {
        return policy;
    }

    public @Nonnull
    Barrier getBarrier()
    {
        return barrier;
    }

    private final Barrier barrier;

    private final PluginClassDiscoveringPolicy policy;

    private final UPMContext context;

    static class CancellablePluginEntrySearchStageEvent extends PluginEntrySearchStageEvent
        implements Cancellable
    {
        CancellablePluginEntrySearchStageEvent(@Nonnull UPMContext context,
                                               @Nonnull PluginClassDiscoveringPolicy policy,
                                               @Nonnull Barrier barrier)
        {
            super(context, policy, barrier);
        }

        @Override
        public boolean isCancelled()
        {
            return cancelled;
        }

        @Override
        public void cancel()
        {
            cancelled = true;
        }

        boolean cancelled;
    }

    public static abstract class EntryEvent extends PluginEntrySearchStageEvent
    {
        EntryEvent(@Nonnull UPMContext context,
                                     @Nonnull PluginClassDiscoveringPolicy policy,
                                     @Nonnull Barrier barrier,
                                     @Nonnull PluginSourceEntry entry)
        {
            super(context, policy, barrier);
            this.entry = Objects.requireNonNull(entry, "entry");
        }

        public @Nonnull PluginSourceEntry getEntry()
        {
            return entry;
        }

        private final PluginSourceEntry entry;
    }

    public static abstract class CancellableEntryEvent extends CancellablePluginEntrySearchStageEvent
    {
        CancellableEntryEvent(@Nonnull UPMContext context,
                                     @Nonnull PluginClassDiscoveringPolicy policy,
                                     @Nonnull Barrier barrier,
                                     @Nonnull PluginSourceEntry entry)
        {
            super(context, policy, barrier);
            this.entry = Objects.requireNonNull(entry, "entry");
        }

        public @Nonnull PluginSourceEntry getEntry()
        {
            return entry;
        }

        private final PluginSourceEntry entry;
    }

    public static class EntryScan extends CancellableEntryEvent
    {
        public EntryScan(@Nonnull UPMContext context,
                         @Nonnull PluginClassDiscoveringPolicy policy,
                         @Nonnull Barrier barrier,
                         @Nonnull PluginSourceEntry entry)
        {
            super(context, policy, barrier, entry);
        }
    }

    public static class EntryScanException extends EntryEvent
    {
        public EntryScanException(@Nonnull UPMContext context,
                                  @Nonnull PluginClassDiscoveringPolicy policy,
                                  @Nonnull Barrier barrier,
                                  @Nonnull PluginSourceEntry entry,
                                  @Nonnull Exception cause)
        {
            super(context, policy, barrier, entry);
            this.cause = Objects.requireNonNull(cause, "cause");
        }

        public @Nonnull Exception getCause()
        {
            return cause;
        }

        private final Exception cause;
    }

    public static class ClassEntryFound extends CancellableEntryEvent
    {
        public ClassEntryFound(@Nonnull UPMContext context,
                               @Nonnull PluginClassDiscoveringPolicy policy,
                               @Nonnull Barrier barrier,
                               @Nonnull PluginSourceEntry entry,
                               @Nonnull String className)
        {
            super(context, policy, barrier, entry);
            this.className = Objects.requireNonNull(className, "classname");
        }

        public @Nonnull String getClassName()
        {
            return className;
        }

        private final String className;
    }

    public static class ConfigurationEntryFound extends CancellableEntryEvent
    {
        public ConfigurationEntryFound(@Nonnull UPMContext context,
                                       @Nonnull PluginClassDiscoveringPolicy policy,
                                       @Nonnull Barrier barrier,
                                       @Nonnull PluginSourceEntry entry)
        {
            super(context, policy, barrier, entry);
        }
    }

    public static class AnnotationEntryFound extends CancellableEntryEvent
    {
        public AnnotationEntryFound(@Nonnull UPMContext context,
                                    @Nonnull PluginClassDiscoveringPolicy policy,
                                    @Nonnull Barrier barrier,
                                    @Nonnull PluginSourceEntry entry,
                                    @Nonnull Class<? extends Annotation> annotationType,
                                    @Nonnull AnnotationNode annotationNode)
        {
            super(context, policy, barrier, entry);
            this.annotationType = Objects.requireNonNull(annotationType, "annotationType");
            this.annotationNode = Objects.requireNonNull(annotationNode, "annotationNode");
        }

        public @Nonnull AnnotationNode getAnnotationNode()
        {
            return annotationNode;
        }

        public @Nonnull Class<? extends Annotation> getAnnotationType()
        {
            return annotationType;
        }

        private final Class<? extends Annotation> annotationType;

        private final AnnotationNode annotationNode;
    }

    public static class SubclassEntryFound extends CancellableEntryEvent
    {
        public SubclassEntryFound(@Nonnull UPMContext context,
                                  @Nonnull PluginClassDiscoveringPolicy policy,
                                  @Nonnull Barrier barrier,
                                  @Nonnull PluginSourceEntry entry,
                                  @Nonnull Class<?> superclass)
        {
            super(context, policy, barrier, entry);
            this.superclass = Objects.requireNonNull(superclass, "superclass");
        }

        public @Nonnull Class<?> getSuperclass()
        {
            return superclass;
        }

        private final Class<?> superclass;
    }

    public abstract static class EntryNotFoundEvent extends PluginEntrySearchStageEvent
    {
        EntryNotFoundEvent(@Nonnull UPMContext context,
                           @Nonnull PluginClassDiscoveringPolicy policy,
                           @Nonnull Barrier barrier,
                           @Nonnull String entryName)
        {
            super(context, policy, barrier);
            this.entryName = Objects.requireNonNull(entryName, "entryName");
        }

        public @Nonnull String getEntryName()
        {
            return entryName;
        }

        private final String entryName;
    }

    public static class ClassEntryNotFound extends EntryNotFoundEvent
    {
        public ClassEntryNotFound(@Nonnull UPMContext context,
                                  @Nonnull PluginClassDiscoveringPolicy policy,
                                  @Nonnull Barrier barrier,
                                  @Nonnull String entryName)
        {
            super(context, policy, barrier, entryName);
        }
    }

    public static class ConfigurationEntryNotFound extends EntryNotFoundEvent
    {
        public ConfigurationEntryNotFound(@Nonnull UPMContext context,
                                          @Nonnull PluginClassDiscoveringPolicy policy,
                                          @Nonnull Barrier barrier,
                                          @Nonnull String entryName)
        {
            super(context, policy, barrier, entryName);
        }
    }
}
