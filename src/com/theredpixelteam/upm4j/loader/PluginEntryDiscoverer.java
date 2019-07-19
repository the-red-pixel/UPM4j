package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.attribution.AttributionWorkflow;
import com.theredpixelteam.upm4j.loader.attribution.processor.*;
import com.theredpixelteam.upm4j.loader.event.PluginEntrySearchStageEvent;
import com.theredpixelteam.upm4j.loader.exception.PluginMountingException;
import com.theredpixelteam.upm4j.loader.exception.PluginSourceIOException;
import com.theredpixelteam.upm4j.source.Source;
import com.theredpixelteam.upm4j.source.SourceEntry;
import com.theredpixelteam.upm4j.source.SourceEntryNameFilter;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;
import org.kucro3.jam2.util.Jam2Util;
import org.kucro3.jam2.util.annotation.Annotations;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

public abstract class PluginEntryDiscoverer {
    PluginEntryDiscoverer(Type type)
    {
        this.type = type;
    }

    public abstract @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                                  @Nonnull Source source,
                                                                  @Nonnull Barrier barrier)
            throws PluginMountingException;

    public @Nonnull Type getType()
    {
        return type;
    }

    public static @Nonnull PluginEntryDiscoverer
        ofFixedClasses(@Nonnull Collection<String> classNames,
                       @Nonnull FixedClassAttributionProcessor processor)
    {
        return new FixedClasses(classNames, processor);
    }

    public static @Nonnull PluginEntryDiscoverer
        ofConfigurationFiles(@Nonnull Collection<String> files,
                             @Nonnull ConfigurationFileAttributionProcessor processor)
    {
        return new ConfigurationFiles(files, processor);
    }

    public static @Nonnull PluginEntryDiscoverer
        ofScanAnnotations(@Nonnull Collection<Class<? extends Annotation>> annotations,
                          @Nonnull AnnotationScanAttributionProcessor processor)
    {
        return new ScanAnnotations(annotations, processor);
    }

    public static @Nonnull PluginEntryDiscoverer
        ofScanSubclasses(@Nonnull Collection<Class<?>> superclasses,
                         @Nonnull SubclassScanAttributionProcessor processor)
    {
        return new ScanSubclasses(superclasses, processor);
    }

    public static @Nonnull PluginEntryDiscoverer
        ofCustom(@Nonnull CustomAttributionProcessor processor)
    {
        return new Custom(processor);
    }

    static void requireNonNull(UPMContext context, Source source, Barrier barrier)
    {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(barrier, "barrier");
    }

    public static boolean postStageStart(@Nonnull UPMContext context,
                                         @Nonnull Source source,
                                         @Nonnull PluginEntryDiscoverer instance,
                                         @Nonnull Barrier barrier)
    {
        PluginEntrySearchStageEvent.Start event = new PluginEntrySearchStageEvent
                .Start(context, source, instance, barrier);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postStageCancelled(@Nonnull UPMContext context,
                                          @Nonnull Source source,
                                          @Nonnull PluginEntryDiscoverer instance,
                                          @Nonnull Barrier barrier)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .Cancelled(context, source, instance, barrier));
    }

    public static void postBlockedByBarrier(@Nonnull UPMContext context,
                                            @Nonnull Source source,
                                            @Nonnull PluginEntryDiscoverer instance,
                                            @Nonnull Barrier barrier)
    {
        context.getEventBus().post(
                new PluginEntrySearchStageEvent.BlockedByBarrier(context, source, instance, barrier));
    }

    public static boolean postClassEntryFound(@Nonnull UPMContext context,
                                              @Nonnull Source source,
                                              @Nonnull PluginEntryDiscoverer instance,
                                              @Nonnull Barrier barrier,
                                              @Nonnull String className,
                                              @Nonnull SourceEntry entry)
    {
        PluginEntrySearchStageEvent.ClassEntryFound event = new PluginEntrySearchStageEvent
                .ClassEntryFound(context, source, instance, barrier, className, entry);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postClassEntryProcessCancelled(@Nonnull UPMContext context,
                                                      @Nonnull Source source,
                                                      @Nonnull PluginEntryDiscoverer instance,
                                                      @Nonnull Barrier barrier,
                                                      @Nonnull String className,
                                                      @Nonnull SourceEntry entry)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .ClassEntryProcessCancelled(context, source, instance, barrier, entry, className));
    }

    public static void postClassEntryProcessPassed(@Nonnull UPMContext context,
                                                   @Nonnull Source source,
                                                   @Nonnull PluginEntryDiscoverer instance,
                                                   @Nonnull Barrier barrier,
                                                   @Nonnull String className,
                                                   @Nonnull SourceEntry entry)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .ClassEntryProcessPassed(context, source, instance, barrier, entry, className));
    }

    public static void postClassEntryNotFound(@Nonnull UPMContext context,
                                              @Nonnull Source source,
                                              @Nonnull PluginEntryDiscoverer instance,
                                              @Nonnull Barrier barrier,
                                              @Nonnull String className)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .ClassEntryNotFound(context, source, instance, barrier, className));
    }

    public static boolean postConfigurationEntryFound(@Nonnull UPMContext context,
                                                      @Nonnull Source source,
                                                      @Nonnull PluginEntryDiscoverer instance,
                                                      @Nonnull Barrier barrier,
                                                      @Nonnull SourceEntry entry)
    {
        PluginEntrySearchStageEvent.ConfigurationEntryFound event =
                new PluginEntrySearchStageEvent.ConfigurationEntryFound(context, source, instance, barrier, entry);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postConfigurationEntryProcessCancelled(@Nonnull UPMContext context,
                                                              @Nonnull Source source,
                                                              @Nonnull PluginEntryDiscoverer instance,
                                                              @Nonnull Barrier barrier,
                                                              @Nonnull SourceEntry entry)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .ConfigurationEntryProcessCancelled(context, source, instance, barrier, entry));
    }

    public static void postConfigurationEntryProcessPassed(@Nonnull UPMContext context,
                                                           @Nonnull Source source,
                                                           @Nonnull PluginEntryDiscoverer instance,
                                                           @Nonnull Barrier barrier,
                                                           @Nonnull SourceEntry entry)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .ConfigurationEntryProcessPassed(context, source, instance, barrier, entry));
    }

    public static void postConfigurationEntryNotFound(@Nonnull UPMContext context,
                                                      @Nonnull Source source,
                                                      @Nonnull PluginEntryDiscoverer instance,
                                                      @Nonnull Barrier barrier,
                                                      @Nonnull String file)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                        .ConfigurationEntryNotFound(context, source, instance, barrier, file));
    }

    public static boolean postEntryScan(@Nonnull UPMContext context,
                                        @Nonnull Source source,
                                        @Nonnull PluginEntryDiscoverer instance,
                                        @Nonnull Barrier barrier,
                                        @Nonnull SourceEntry entry)
    {
        PluginEntrySearchStageEvent.EntryScan event =
                new PluginEntrySearchStageEvent.EntryScan(context, source, instance, barrier, entry);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postEntryCancelled(@Nonnull UPMContext context,
                                          @Nonnull Source source,
                                          @Nonnull PluginEntryDiscoverer instance,
                                          @Nonnull Barrier barrier,
                                          @Nonnull SourceEntry entry)
    {
        context.getEventBus().post(
                new PluginEntrySearchStageEvent.EntryCancelled(context, source, instance, barrier, entry));
    }

    public static void postEntryIgnored(@Nonnull UPMContext context,
                                        @Nonnull Source source,
                                        @Nonnull PluginEntryDiscoverer instance,
                                        @Nonnull Barrier barrier,
                                        @Nonnull SourceEntry entry)
    {
        context.getEventBus().post(
                new PluginEntrySearchStageEvent.EntryIgnored(context, source, instance, barrier, entry));
    }

    public static void postEntryScanException(@Nonnull UPMContext context,
                                              @Nonnull Source source,
                                              @Nonnull PluginEntryDiscoverer instance,
                                              @Nonnull Barrier barrier,
                                              @Nonnull SourceEntry entry,
                                              @Nonnull Exception cause)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .EntryScanException(context, source, instance, barrier, entry, cause));
    }

    public static boolean postAnnotationEntryFound(@Nonnull UPMContext context,
                                                   @Nonnull Source source,
                                                   @Nonnull PluginEntryDiscoverer instance,
                                                   @Nonnull Barrier barrier,
                                                   @Nonnull SourceEntry entry,
                                                   @Nonnull Class<? extends Annotation> annotationType,
                                                   @Nonnull AnnotationNode node)
    {
        PluginEntrySearchStageEvent.AnnotationEntryFound event = new PluginEntrySearchStageEvent
                .AnnotationEntryFound(context, source, instance, barrier, entry, annotationType, node);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postAnnotationEntryProcessCancelled(@Nonnull UPMContext context,
                                                           @Nonnull Source source,
                                                           @Nonnull PluginEntryDiscoverer instance,
                                                           @Nonnull Barrier barrier,
                                                           @Nonnull SourceEntry entry,
                                                           @Nonnull Class<? extends Annotation> annotationType,
                                                           @Nonnull AnnotationNode node)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .AnnotationEntryProcessCancelled(context, source, instance, barrier, entry, annotationType, node));
    }

    public static void postAnnotationEntryProcessPassed(@Nonnull UPMContext context,
                                                        @Nonnull Source source,
                                                        @Nonnull PluginEntryDiscoverer instance,
                                                        @Nonnull Barrier barrier,
                                                        @Nonnull SourceEntry entry,
                                                        @Nonnull Class<? extends Annotation> annotationType,
                                                        @Nonnull AnnotationNode node)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .AnnotationEntryProcessPassed(context, source, instance, barrier, entry, annotationType, node));
    }

    public static boolean postSubclassEntryFound(@Nonnull UPMContext context,
                                                 @Nonnull Source source,
                                                 @Nonnull PluginEntryDiscoverer instance,
                                                 @Nonnull Barrier barrier,
                                                 @Nonnull SourceEntry entry,
                                                 @Nonnull Class<?> superclass)
    {
        PluginEntrySearchStageEvent.SubclassEntryFound event = new PluginEntrySearchStageEvent
                .SubclassEntryFound(context, source, instance, barrier, entry, superclass);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postSubclassEntryProcessCancelled(@Nonnull UPMContext context,
                                                         @Nonnull Source source,
                                                         @Nonnull PluginEntryDiscoverer instance,
                                                         @Nonnull Barrier barrier,
                                                         @Nonnull SourceEntry entry,
                                                         @Nonnull Class<?> superclass)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .SubclassEntryProcessCancelled(context, source, instance, barrier, entry, superclass));
    }

    public static void postSubclassEntryProcessPassed(@Nonnull UPMContext context,
                                                      @Nonnull Source source,
                                                      @Nonnull PluginEntryDiscoverer instance,
                                                      @Nonnull Barrier barrier,
                                                      @Nonnull SourceEntry entry,
                                                      @Nonnull Class<?> superclass)
    {
        context.getEventBus().post(new PluginEntrySearchStageEvent
                .SubclassEntryProcessPassed(context, source, instance, barrier, entry, superclass));
    }

    private final Type type;

    public static enum Type
    {
        FIXED_CLASSES,
        CONFIGURATION_FILES,
        SCAN_ANNOTATIONS,
        SCAN_SUBCLASSES,
        CUSTOM
    }

    public static class FixedClasses extends PluginEntryDiscoverer
    {
        FixedClasses(@Nonnull Collection<String> names,
                     @Nonnull FixedClassAttributionProcessor processor)
        {
            super(Type.FIXED_CLASSES);

            this.names = new ArrayList<>(names);
            this.processor = Objects.requireNonNull(processor, "processor");
        }

        public @Nonnull Collection<String> getClassNames()
        {
            return names;
        }

        public @Nonnull FixedClassAttributionProcessor getProcessor()
        {
            return processor;
        }

        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull Source source,
                                                             @Nonnull Barrier barrier)
                throws PluginMountingException
        {
            requireNonNull(context, source, barrier);

            if (postStageStart(context, source, this, barrier))
            {
                postStageCancelled(context, source, this, barrier);

                return Collections.emptyList();
            }

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            for (String name : names)
            {
                if (barrier.isBlocked())
                {
                    postBlockedByBarrier(context, source, this, barrier);

                    break;
                }

                String sourceName =
                        Jam2Util.fromInternalNameToResource(
                        Jam2Util.fromCanonicalToInternalName(name));

                SourceEntry entry = source.getEntry(sourceName).orElse(null);

                if (entry == null)
                {
                    postClassEntryNotFound(context, source, this, barrier, name);

                    continue;
                }

                if (postClassEntryFound(context, source, this, barrier, name, entry))
                {
                    postClassEntryProcessCancelled(context, source, this, barrier, name, entry);

                    continue;
                }

                try {
                    processor.process(workflow, name, entry, barrier);
                } catch (IOException e) {
                    barrier.block();
                    throw new PluginSourceIOException(e);
                }

                postClassEntryProcessPassed(context, source, this, barrier, name, entry);
            }

            return workflow.buildAll();
        }

        private final FixedClassAttributionProcessor processor;

        private final Collection<String> names;
    }

    public static class ConfigurationFiles extends PluginEntryDiscoverer
    {
        ConfigurationFiles(@Nonnull Collection<String> files,
                           @Nonnull ConfigurationFileAttributionProcessor processor)
        {
            super(Type.CONFIGURATION_FILES);

            this.files = Collections.unmodifiableList(new ArrayList<>(files));
            this.processor = Objects.requireNonNull(processor, "processor");
        }

        public @Nonnull Collection<String> getFiles()
        {
            return files;
        }

        public @Nonnull ConfigurationFileAttributionProcessor getProcessor()
        {
            return processor;
        }

        @Override
        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull Source source,
                                                             @Nonnull Barrier barrier)
                throws PluginMountingException
        {
            requireNonNull(context, source, barrier);

            if (postStageStart(context, source, this, barrier))
            {
                postStageCancelled(context, source, this, barrier);

                return Collections.emptyList();
            }

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            for (String file : files)
            {
                if (barrier.isBlocked())
                {
                    postBlockedByBarrier(context, source, this, barrier);

                    break;
                }

                SourceEntry entry = source.getEntry(file).orElse(null);

                if (entry == null)
                {
                    postConfigurationEntryNotFound(context, source, this, barrier, file);

                    continue;
                }

                if (postConfigurationEntryFound(context, source, this, barrier, entry))
                {
                    postConfigurationEntryProcessCancelled(context, source, this, barrier, entry);

                    continue;
                }

                try {
                    processor.process(workflow, entry, barrier);
                } catch (IOException e) {
                    barrier.block();
                    throw new PluginSourceIOException(e);
                }

                postConfigurationEntryProcessPassed(context, source, this, barrier, entry);
            }

            return workflow.buildAll();
        }

        private final Collection<String> files;

        private final ConfigurationFileAttributionProcessor processor;
    }

    public static class ScanAnnotations extends PluginEntryDiscoverer
    {
        ScanAnnotations(@Nonnull Collection<Class<? extends Annotation>> annotationTypes,
                        @Nonnull AnnotationScanAttributionProcessor processor)
        {
            super(Type.SCAN_ANNOTATIONS);

            this.annotationTypes = Collections.unmodifiableList(new ArrayList<>(annotationTypes));
            this.processor = Objects.requireNonNull(processor, "processor");
        }

        public @Nonnull Collection<Class<? extends Annotation>> getAnnotationTypes()
        {
            return annotationTypes;
        }

        public @Nonnull AnnotationScanAttributionProcessor getProcessor()
        {
            return processor;
        }

        @Override
        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull Source source,
                                                             @Nonnull Barrier barrier)
                throws PluginMountingException
        {
            requireNonNull(context, source, barrier);

            if (postStageStart(context, source, this, barrier))
            {
                postStageCancelled(context, source, this, barrier);

                return Collections.emptyList();
            }

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            L: for (SourceEntry entry :
                    source.getEntries((SourceEntryNameFilter) name -> name.endsWith(".class")))
            {
                if (barrier.isBlocked())
                {
                    postBlockedByBarrier(context, source, this, barrier);

                    break;
                }

                if (postEntryScan(context, source, this, barrier, entry))
                {
                    postEntryCancelled(context, source, this, barrier, entry);

                    continue;
                }

                try {
                    ClassReader reader = new ClassReader(entry.getBytes());
                    ClassNode classNode = new ClassNode();

                    reader.accept(classNode, 0);

                    boolean visited = false;
                    for (Class<? extends Annotation> annotationType : annotationTypes)
                    {
                        AnnotationNode node = Annotations.getAnnotationNode(classNode, annotationType).orElse(null);

                        if (node == null)
                            continue;

                        visited = true;

                        if (postAnnotationEntryFound(context, source, this, barrier, entry, annotationType, node))
                        {
                            postAnnotationEntryProcessCancelled(context, source, this, barrier, entry, annotationType, node);

                            continue;
                        }

                        processor.process(workflow, annotationType, node, entry, barrier);

                        postAnnotationEntryProcessPassed(context, source, this, barrier, entry, annotationType, node);

                        if (barrier.isBlocked())
                        {
                            postBlockedByBarrier(context, source, this, barrier);

                            break L;
                        }
                    }

                    if (!visited)
                        postEntryIgnored(context, source, this, barrier, entry);

                } catch (IOException e) {
                    barrier.block();
                    throw new PluginSourceIOException(e);
                } catch (Exception e) {
                    postEntryScanException(context, source, this, barrier, entry, e);
                }
            }

            return workflow.buildAll();
        }

        private final Collection<Class<? extends Annotation>> annotationTypes;

        private final AnnotationScanAttributionProcessor processor;
    }

    public static class ScanSubclasses extends PluginEntryDiscoverer
    {
        ScanSubclasses(@Nonnull Collection<Class<?>> superclasses,
                       @Nonnull SubclassScanAttributionProcessor processor)
        {
            super(Type.SCAN_SUBCLASSES);

            this.superclasses = new HashMap<>();
            this.processor = Objects.requireNonNull(processor, "processor");

            for (Class<?> superclass : superclasses)
                this.superclasses.put(Jam2Util.toInternalName(superclass), superclass);
        }

        public @Nonnull Collection<Class<?>> getSuperclasses()
        {
            return Collections.unmodifiableCollection(superclasses.values());
        }

        public @Nonnull SubclassScanAttributionProcessor getProcessor()
        {
            return processor;
        }

        @Override
        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull Source source,
                                                             @Nonnull Barrier barrier)
                throws PluginMountingException
        {
            requireNonNull(context, source, barrier);

            if (postStageStart(context, source, this, barrier))
            {
                postStageCancelled(context, source, this, barrier);

                return Collections.emptyList();
            }

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            L: for (SourceEntry entry :
                    source.getEntries((SourceEntryNameFilter) name -> name.endsWith(".class")))
            {
                if (barrier.isBlocked())
                {
                    postBlockedByBarrier(context, source, this, barrier);

                    break;
                }

                if (postEntryScan(context, source, this, barrier, entry))
                {
                    postEntryCancelled(context, source, this, barrier, entry);

                    continue;
                }

                try {
                    ClassReader reader = new ClassReader(entry.getBytes());
                    ClassNode classNode = new ClassNode();

                    reader.accept(classNode, 0);

                    Class<?> superclass;
                    if (classNode.superName != null
                            && (superclass = superclasses.get(classNode.superName)) != null)
                    {
                        if (postSubclassEntryFound(context, source, this, barrier, entry, superclass))
                        {
                            postSubclassEntryProcessCancelled(context, source, this, barrier, entry, superclass);

                            continue;
                        }

                        processor.process(workflow, superclass, entry, barrier);

                        postSubclassEntryProcessPassed(context, source, this, barrier, entry, superclass);
                    }
                    else
                        postEntryIgnored(context, source, this, barrier, entry);

                } catch (IOException e) {
                    barrier.block();
                    throw new PluginSourceIOException(e);
                } catch (Exception e) {
                    postEntryScanException(context, source, this, barrier, entry, e);
                }
            }

            return workflow.buildAll();
        }

        private final Map<String, Class<?>> superclasses;

        private final SubclassScanAttributionProcessor processor;
    }

    public static class Custom extends PluginEntryDiscoverer
    {
        Custom(@Nonnull CustomAttributionProcessor processor)
        {
            super(Type.CUSTOM);

            this.processor = Objects.requireNonNull(processor);
        }

        @Override
        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull Source source,
                                                             @Nonnull Barrier barrier)
                throws PluginMountingException
        {
            requireNonNull(context, source, barrier);

            if (postStageStart(context, source, this, barrier))
            {
                postStageCancelled(context, source, this, barrier);

                return Collections.emptyList();
            }

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            try {
                processor.process(workflow, source, barrier);
            } catch (IOException e) {
                barrier.block();
                throw new PluginSourceIOException(e);
            }

            return workflow.buildAll();
        }

        private final CustomAttributionProcessor processor;
    }

}
