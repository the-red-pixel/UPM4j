package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.loader.PluginEntryDiscoverer;
import com.theredpixelteam.upm4j.loader.attribution.processor.Barrier;
import com.theredpixelteam.upm4j.loader.source.PluginSource;
import com.theredpixelteam.upm4j.loader.source.PluginSourceEntry;
import org.objectweb.asm.tree.AnnotationNode;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Objects;

public abstract class PluginEntrySearchStageEvent implements UPMEvent {
    protected PluginEntrySearchStageEvent(@Nonnull UPMContext context,
                                          @Nonnull PluginSource source,
                                          @Nonnull PluginEntryDiscoverer policy,
                                          @Nonnull Barrier barrier)
    {
        this.context = Objects.requireNonNull(context, "context");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.barrier = Objects.requireNonNull(barrier, "barrier");
        this.source = Objects.requireNonNull(source, "source");
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public @Nonnull PluginEntryDiscoverer getDiscoverer()
    {
        return policy;
    }

    public @Nonnull Barrier getBarrier()
    {
        return barrier;
    }

    public @Nonnull PluginSource getSource()
    {
        return source;
    }

    private final Barrier barrier;

    private final PluginEntryDiscoverer policy;

    private final UPMContext context;

    private final PluginSource source;

    static class CancellablePluginEntrySearchStageEvent extends PluginEntrySearchStageEvent
        implements Cancellable
    {
        CancellablePluginEntrySearchStageEvent(@Nonnull UPMContext context,
                                               @Nonnull PluginSource source,
                                               @Nonnull PluginEntryDiscoverer policy,
                                               @Nonnull Barrier barrier)
        {
            super(context, source, policy, barrier);
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

    public static abstract class AbstractEntryEvent extends PluginEntrySearchStageEvent
    {
        protected AbstractEntryEvent(@Nonnull UPMContext context,
                                     @Nonnull PluginSource source,
                                     @Nonnull PluginEntryDiscoverer policy,
                                     @Nonnull Barrier barrier,
                                     @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier);
            this.entry = Objects.requireNonNull(entry, "entry");
        }

        public @Nonnull PluginSourceEntry getEntry()
        {
            return entry;
        }

        private final PluginSourceEntry entry;
    }

    public static abstract class AbstractCancellableEntryEvent extends CancellablePluginEntrySearchStageEvent
    {
        protected AbstractCancellableEntryEvent(@Nonnull UPMContext context,
                                                @Nonnull PluginSource source,
                                                @Nonnull PluginEntryDiscoverer policy,
                                                @Nonnull Barrier barrier,
                                                @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier);
            this.entry = Objects.requireNonNull(entry, "entry");
        }

        public @Nonnull PluginSourceEntry getEntry()
        {
            return entry;
        }

        private final PluginSourceEntry entry;
    }

    public static class Start extends CancellablePluginEntrySearchStageEvent
    {
        public Start(@Nonnull UPMContext context,
                     @Nonnull PluginSource source,
                     @Nonnull PluginEntryDiscoverer policy,
                     @Nonnull Barrier barrier)
        {
            super(context, source, policy, barrier);
        }
    }

    public static class Cancelled extends PluginEntrySearchStageEvent
    {
        public Cancelled(@Nonnull UPMContext context,
                         @Nonnull PluginSource source,
                         @Nonnull PluginEntryDiscoverer policy,
                         @Nonnull Barrier barrier)
        {
            super(context, source, policy, barrier);
        }
    }

    public static class BlockedByBarrier extends PluginEntrySearchStageEvent
    {
        public BlockedByBarrier(@Nonnull UPMContext context,
                                   @Nonnull PluginSource source,
                                   @Nonnull PluginEntryDiscoverer policy,
                                   @Nonnull Barrier barrier)
        {
            super(context, source, policy, barrier);
        }
    }

    public static class EntryScan extends AbstractCancellableEntryEvent
    {
        public EntryScan(@Nonnull UPMContext context,
                         @Nonnull PluginSource source,
                         @Nonnull PluginEntryDiscoverer policy,
                         @Nonnull Barrier barrier,
                         @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier, entry);
        }
    }

    public static class EntryCancelled extends AbstractEntryEvent
    {
        public EntryCancelled(@Nonnull UPMContext context,
                              @Nonnull PluginSource source,
                              @Nonnull PluginEntryDiscoverer policy,
                              @Nonnull Barrier barrier,
                              @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier, entry);
        }
    }

    public static class EntryScanException extends AbstractEntryEvent
    {
        public EntryScanException(@Nonnull UPMContext context,
                                  @Nonnull PluginSource source,
                                  @Nonnull PluginEntryDiscoverer policy,
                                  @Nonnull Barrier barrier,
                                  @Nonnull PluginSourceEntry entry,
                                  @Nonnull Exception cause)
        {
            super(context, source, policy, barrier, entry);
            this.cause = Objects.requireNonNull(cause, "cause");
        }

        public @Nonnull Exception getCause()
        {
            return cause;
        }

        private final Exception cause;
    }

    public static class EntryFound extends AbstractCancellableEntryEvent
    {
        public EntryFound(@Nonnull UPMContext context,
                          @Nonnull PluginSource source,
                          @Nonnull PluginEntryDiscoverer policy,
                          @Nonnull Barrier barrier,
                          @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier, entry);
        }
    }

    public static class ClassEntryFound extends AbstractCancellableEntryEvent
    {
        public ClassEntryFound(@Nonnull UPMContext context,
                               @Nonnull PluginSource source,
                               @Nonnull PluginEntryDiscoverer policy,
                               @Nonnull Barrier barrier,
                               @Nonnull String className,
                               @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier, entry);
            this.className = Objects.requireNonNull(className, "classname");
        }

        public @Nonnull String getClassName()
        {
            return className;
        }

        private final String className;
    }

    public static class ConfigurationEntryFound extends AbstractCancellableEntryEvent
    {
        public ConfigurationEntryFound(@Nonnull UPMContext context,
                                       @Nonnull PluginSource source,
                                       @Nonnull PluginEntryDiscoverer policy,
                                       @Nonnull Barrier barrier,
                                       @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier, entry);
        }
    }

    public static class AnnotationEntryFound extends AbstractCancellableEntryEvent
    {
        public AnnotationEntryFound(@Nonnull UPMContext context,
                                    @Nonnull PluginSource source,
                                    @Nonnull PluginEntryDiscoverer policy,
                                    @Nonnull Barrier barrier,
                                    @Nonnull PluginSourceEntry entry,
                                    @Nonnull Class<? extends Annotation> annotationType,
                                    @Nonnull AnnotationNode annotationNode)
        {
            super(context, source, policy, barrier, entry);
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

    public static class SubclassEntryFound extends AbstractCancellableEntryEvent
    {
        public SubclassEntryFound(@Nonnull UPMContext context,
                                  @Nonnull PluginSource source,
                                  @Nonnull PluginEntryDiscoverer policy,
                                  @Nonnull Barrier barrier,
                                  @Nonnull PluginSourceEntry entry,
                                  @Nonnull Class<?> superclass)
        {
            super(context, source, policy, barrier, entry);
            this.superclass = Objects.requireNonNull(superclass, "superclass");
        }

        public @Nonnull Class<?> getSuperclass()
        {
            return superclass;
        }

        private final Class<?> superclass;
    }

    public abstract static class AbstractEntryNotFoundEvent extends PluginEntrySearchStageEvent
    {
        protected AbstractEntryNotFoundEvent(@Nonnull UPMContext context,
                                             @Nonnull PluginSource source,
                                             @Nonnull PluginEntryDiscoverer policy,
                                             @Nonnull Barrier barrier,
                                             @Nonnull String entryName)
        {
            super(context, source, policy, barrier);
            this.entryName = Objects.requireNonNull(entryName, "entryName");
        }

        public @Nonnull String getEntryName()
        {
            return entryName;
        }

        private final String entryName;
    }

    public static class EntryNotFound extends AbstractEntryNotFoundEvent
    {
        public EntryNotFound(@Nonnull UPMContext context,
                             @Nonnull PluginSource source,
                             @Nonnull PluginEntryDiscoverer policy,
                             @Nonnull Barrier barrier,
                             @Nonnull String entryName)
        {
            super(context, source, policy, barrier, entryName);
        }
    }

    public static class ClassEntryNotFound extends AbstractEntryNotFoundEvent
    {
        public ClassEntryNotFound(@Nonnull UPMContext context,
                                  @Nonnull PluginSource source,
                                  @Nonnull PluginEntryDiscoverer policy,
                                  @Nonnull Barrier barrier,
                                  @Nonnull String entryName)
        {
            super(context, source, policy, barrier, entryName);
        }
    }

    public static class ConfigurationEntryNotFound extends AbstractEntryNotFoundEvent
    {
        public ConfigurationEntryNotFound(@Nonnull UPMContext context,
                                          @Nonnull PluginSource source,
                                          @Nonnull PluginEntryDiscoverer policy,
                                          @Nonnull Barrier barrier,
                                          @Nonnull String entryName)
        {
            super(context, source, policy, barrier, entryName);
        }
    }

    public static class EntryIgnored extends AbstractEntryEvent
    {
        public EntryIgnored(@Nonnull UPMContext context,
                            @Nonnull PluginSource source,
                            @Nonnull PluginEntryDiscoverer policy,
                            @Nonnull Barrier barrier,
                            @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier, entry);
        }
    }

    public static class EntryProcessCancelled extends AbstractEntryEvent
    {
        public EntryProcessCancelled(@Nonnull UPMContext context,
                                     @Nonnull PluginSource source,
                                     @Nonnull PluginEntryDiscoverer policy,
                                     @Nonnull Barrier barrier,
                                     @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier, entry);
        }
    }

    public static class ClassEntryProcessCancelled extends AbstractEntryEvent
    {
        public ClassEntryProcessCancelled(@Nonnull UPMContext context,
                                          @Nonnull PluginSource source,
                                          @Nonnull PluginEntryDiscoverer policy,
                                          @Nonnull Barrier barrier,
                                          @Nonnull PluginSourceEntry entry,
                                          @Nonnull String className)
        {
            super(context, source, policy, barrier, entry);
            this.className = Objects.requireNonNull(className, "classname");
        }

        public @Nonnull String getClassName()
        {
            return className;
        }

        private final String className;
    }

    public static class ConfigurationEntryProcessCancelled extends AbstractEntryEvent
    {
        public ConfigurationEntryProcessCancelled(@Nonnull UPMContext context,
                                                  @Nonnull PluginSource source,
                                                  @Nonnull PluginEntryDiscoverer policy,
                                                  @Nonnull Barrier barrier,
                                                  @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier, entry);
        }
    }

    public static class AnnotationEntryProcessCancelled extends AbstractEntryEvent
    {
        public AnnotationEntryProcessCancelled(@Nonnull UPMContext context,
                                               @Nonnull PluginSource source,
                                               @Nonnull PluginEntryDiscoverer policy,
                                               @Nonnull Barrier barrier,
                                               @Nonnull PluginSourceEntry entry,
                                               @Nonnull Class<? extends Annotation> annotationType,
                                               @Nonnull AnnotationNode annotationNode)
        {
            super(context, source, policy, barrier, entry);
            this.annotationType = Objects.requireNonNull(annotationType, "annotationType");
            this.annotationNode = Objects.requireNonNull(annotationNode, "annotationNode");
        }

        public @Nonnull Class<? extends Annotation> getAnnotationType()
        {
            return annotationType;
        }

        public @Nonnull AnnotationNode getAnnotationNode()
        {
            return annotationNode;
        }

        private final AnnotationNode annotationNode;

        private final Class<? extends Annotation> annotationType;
    }

    public static class SubclassEntryProcessCancelled extends AbstractEntryEvent
    {
        public SubclassEntryProcessCancelled(@Nonnull UPMContext context,
                                             @Nonnull PluginSource source,
                                             @Nonnull PluginEntryDiscoverer policy,
                                             @Nonnull Barrier barrier,
                                             @Nonnull PluginSourceEntry entry,
                                             @Nonnull Class<?> superclass)
        {
            super(context, source, policy, barrier, entry);
            this.superclass = Objects.requireNonNull(superclass, "superclass");
        }

        public @Nonnull Class<?> getSuperclass()
        {
            return superclass;
        }

        private final Class<?> superclass;
    }

    public static class EntryProcessPassed extends AbstractEntryEvent
    {
        public EntryProcessPassed(@Nonnull UPMContext context,
                                  @Nonnull PluginSource source,
                                  @Nonnull PluginEntryDiscoverer policy,
                                  @Nonnull Barrier barrier, @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier, entry);
        }
    }

    public static class ClassEntryProcessPassed extends AbstractEntryEvent
    {

        public ClassEntryProcessPassed(@Nonnull UPMContext context,
                                       @Nonnull PluginSource source,
                                       @Nonnull PluginEntryDiscoverer policy,
                                       @Nonnull Barrier barrier,
                                       @Nonnull PluginSourceEntry entry,
                                       @Nonnull String className)
        {
            super(context, source, policy, barrier, entry);
            this.className = Objects.requireNonNull(className, "classname");
        }

        public @Nonnull String getClassName()
        {
            return className;
        }

        private final String className;
    }

    public static class ConfigurationEntryProcessPassed extends AbstractEntryEvent
    {
        public ConfigurationEntryProcessPassed(@Nonnull UPMContext context,
                                               @Nonnull PluginSource source,
                                               @Nonnull PluginEntryDiscoverer policy,
                                               @Nonnull Barrier barrier,
                                               @Nonnull PluginSourceEntry entry)
        {
            super(context, source, policy, barrier, entry);
        }
    }

    public static class AnnotationEntryProcessPassed extends AbstractEntryEvent
    {
        public AnnotationEntryProcessPassed(@Nonnull UPMContext context,
                                            @Nonnull PluginSource source,
                                            @Nonnull PluginEntryDiscoverer policy,
                                            @Nonnull Barrier barrier,
                                            @Nonnull PluginSourceEntry entry,
                                            @Nonnull Class<? extends Annotation> annotationType,
                                            @Nonnull AnnotationNode annotationNode)
        {
            super(context, source, policy, barrier, entry);
            this.annotationType = Objects.requireNonNull(annotationType, "annotationType");
            this.annotationNode = Objects.requireNonNull(annotationNode, "annotationNode");
        }

        public @Nonnull Class<? extends Annotation> getAnnotationType()
        {
            return annotationType;
        }

        public @Nonnull AnnotationNode getAnnotationNode()
        {
            return annotationNode;
        }

        private final AnnotationNode annotationNode;

        private final Class<? extends Annotation> annotationType;
    }

    public static class SubclassEntryProcessPassed extends AbstractEntryEvent
    {
        public SubclassEntryProcessPassed(@Nonnull UPMContext context,
                                          @Nonnull PluginSource source,
                                          @Nonnull PluginEntryDiscoverer policy,
                                          @Nonnull Barrier barrier,
                                          @Nonnull PluginSourceEntry entry,
                                          @Nonnull Class<?> superclass)
        {
            super(context, source, policy, barrier, entry);
            this.superclass = Objects.requireNonNull(superclass, "superclass");
        }

        public @Nonnull Class<?> getSuperclass()
        {
            return superclass;
        }

        private final Class<?> superclass;
    }
}
