package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.attribution.AttributionWorkflow;
import com.theredpixelteam.upm4j.loader.attribution.processor.*;
import com.theredpixelteam.upm4j.loader.source.PluginSource;
import com.theredpixelteam.upm4j.loader.source.PluginSourceEntry;
import com.theredpixelteam.upm4j.loader.source.SourceEntryNameFilter;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;
import org.kucro3.jam2.util.Jam2Util;
import org.kucro3.jam2.util.annotation.Annotations;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

public abstract class PluginClassDiscoveringPolicy {
    PluginClassDiscoveringPolicy(Type type)
    {
        this.type = type;
    }

    public abstract @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                                  @Nonnull PluginSource source,
                                                                  @Nonnull Barrier barrier)
            throws IOException;

    public @Nonnull Type getType()
    {
        return type;
    }

    public static @Nonnull PluginClassDiscoveringPolicy
        ofFixedClasses(@Nonnull Collection<String> classNames,
                       @Nonnull FixedClassAttributionProcessor processor)
    {
        return new Fixed(classNames, processor);
    }

    public static @Nonnull PluginClassDiscoveringPolicy
        ofConfigurated(@Nonnull ConfiguratedAttributionProcessor processor)
    {
        return new Configurated(processor);
    }

    public static @Nonnull PluginClassDiscoveringPolicy
        ofConfigurationFiles(@Nonnull Collection<String> files,
                             @Nonnull ConfigurationFileAttributionProcessor processor)
    {
        return new ConfigurationFiles(files, processor);
    }

    public static @Nonnull PluginClassDiscoveringPolicy
        ofScanAnnotations(@Nonnull Collection<Class<? extends Annotation>> annotations,
                          @Nonnull AnnotationScanAttributionProcessor processor)
    {
        return new ScanAnnotations(annotations, processor);
    }

    public static @Nonnull PluginClassDiscoveringPolicy
        ofScanSubclasses(@Nonnull Collection<Class<?>> superclasses,
                         @Nonnull SubclassScanAttributionProcessor processor)
    {
        return new ScanSubclasses(superclasses, processor);
    }

    public static @Nonnull PluginClassDiscoveringPolicy
        ofCustom(@Nonnull CustomAttributionProcessor processor)
    {
        return new Custom(processor);
    }

    public static Barrier barrier()
    {
        return new Barrier();
    }

    public static Barrier barrier(int maxCount)
    {
        return new Barrier(maxCount);
    }

    static void requireNonNull(UPMContext context, PluginSource source, Barrier barrier)
    {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(barrier, "barrier");
    }

    private final Type type;

    public static enum Type
    {
        FIXED,
        CONFIGURATED,
        CONFIGURATION_FILES,
        SCAN_ANNOTATIONS,
        SCAN_SUBCLASSES,
        CUSTOM
    }

    public static class Fixed extends PluginClassDiscoveringPolicy
    {
        Fixed(@Nonnull Collection<String> names,
              @Nonnull FixedClassAttributionProcessor processor)
        {
            super(Type.FIXED);

            this.names = new ArrayList<>(names);
            this.processor = Objects.requireNonNull(processor, "processor");
        }

        public @Nonnull Collection<String> getClassNames()
        {
            return names;
        }

        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull PluginSource source,
                                                             @Nonnull Barrier barrier)
                throws IOException
        {
            requireNonNull(context, source, barrier);

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            for (String name : names)
            {
                if (barrier.isBlocked())
                    break;

                String sourceName =
                        Jam2Util.fromInternalNameToResource(
                        Jam2Util.fromCanonicalToInternalName(name));

                source.getEntry(sourceName).ifPresent(
                        (entry) -> processor.provide(workflow, name, entry, barrier));
            }

            return workflow.buildAll();
        }

        private final FixedClassAttributionProcessor processor;

        private final Collection<String> names;
    }

    public static class Configurated extends PluginClassDiscoveringPolicy
    {
        Configurated(@Nonnull ConfiguratedAttributionProcessor processor)
        {
            super(Type.CONFIGURATED);

            this.processor = Objects.requireNonNull(processor);
        }

        @Override
        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull PluginSource source,
                                                             @Nonnull Barrier barrier)
                throws IOException
        {
            requireNonNull(context, source, barrier);

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            processor.provide(workflow, source, barrier);

            return workflow.buildAll();
        }

        private final ConfiguratedAttributionProcessor processor;
    }

    public static class ConfigurationFiles extends PluginClassDiscoveringPolicy
    {
        ConfigurationFiles(@Nonnull Collection<String> files,
                           @Nonnull ConfigurationFileAttributionProcessor processor)
        {
            super(Type.CONFIGURATION_FILES);

            this.files = new ArrayList<>(files);
            this.processor = Objects.requireNonNull(processor, "processor");
        }

        @Override
        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull PluginSource source,
                                                             @Nonnull Barrier barrier)
                throws IOException
        {
            requireNonNull(context, source, barrier);

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            for (String file : files)
            {
                if (barrier.isBlocked())
                    break;

                source.getEntry(file).ifPresent((entry) -> processor.provide(workflow, entry, barrier));
            }

            return workflow.buildAll();
        }

        private final Collection<String> files;

        private final ConfigurationFileAttributionProcessor processor;
    }

    public static class ScanAnnotations extends PluginClassDiscoveringPolicy
    {
        ScanAnnotations(@Nonnull Collection<Class<? extends Annotation>> annotationTypes,
                        @Nonnull AnnotationScanAttributionProcessor processor)
        {
            super(Type.SCAN_ANNOTATIONS);

            this.annotationTypes = new ArrayList<>(annotationTypes);
            this.processor = Objects.requireNonNull(processor, "processor");
        }

        @Override
        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull PluginSource source,
                                                             @Nonnull Barrier barrier)
                throws IOException
        {
            requireNonNull(context, source, barrier);

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            L: for (PluginSourceEntry entry :
                    source.getEntries((SourceEntryNameFilter) name -> name.endsWith(".class")))
            {
                if (barrier.isBlocked())
                    break;

                try {
                    ClassReader reader = new ClassReader(entry.getBytes());
                    ClassNode classNode = new ClassNode();

                    reader.accept(classNode, 0);

                    for (Class<? extends Annotation> annotationType : annotationTypes)
                    {
                        Annotations.getAnnotationNode(classNode, annotationType).ifPresent(
                                node -> processor.process(workflow, annotationType, node, entry, barrier));

                        if (barrier.isBlocked())
                            break L;
                    }

                } catch (IOException e) {
                    throw e;
                } catch (Exception e) {
                    // ignore
                }
            }

            return workflow.buildAll();
        }

        private final Collection<Class<? extends Annotation>> annotationTypes;

        private final AnnotationScanAttributionProcessor processor;
    }

    public static class ScanSubclasses extends PluginClassDiscoveringPolicy
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

        @Override
        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull PluginSource source,
                                                             @Nonnull Barrier barrier)
                throws IOException
        {
            requireNonNull(context, source, barrier);

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            L: for (PluginSourceEntry entry :
                    source.getEntries((SourceEntryNameFilter) name -> name.endsWith(".class")))
            {
                if (barrier.isBlocked())
                    break;

                try {
                    ClassReader reader = new ClassReader(entry.getBytes());
                    ClassNode classNode = new ClassNode();

                    reader.accept(classNode, 0);

                    Class<?> superclass;
                    if (classNode.superName != null
                            && (superclass = superclasses.get(classNode.superName)) != null)
                        processor.process(workflow, superclass, entry, barrier);

                } catch (IOException e) {
                    throw e;
                } catch (Exception e) {
                    // ignore
                }
            }

            return workflow.buildAll();
        }

        private final Map<String, Class<?>> superclasses;

        private final SubclassScanAttributionProcessor processor;
    }

    public static class Custom extends PluginClassDiscoveringPolicy
    {
        Custom(@Nonnull CustomAttributionProcessor processor)
        {
            super(Type.CUSTOM);

            this.processor = Objects.requireNonNull(processor);
        }

        @Override
        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context,
                                                             @Nonnull PluginSource source,
                                                             @Nonnull Barrier barrier)
                throws IOException
        {
            requireNonNull(context, source, barrier);

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            processor.process(workflow, source, barrier);

            return workflow.buildAll();
        }

        private final CustomAttributionProcessor processor;
    }

    public static class Barrier {
        Barrier()
        {
            this(Integer.MAX_VALUE);
        }

        Barrier(int maxCount)
        {
            this.maxCount = maxCount;
        }

        public synchronized boolean count()
        {
            if (blocked)
                return false;

            count++;
            checkCount();

            return true;
        }

        void checkCount()
        {
            if (count == maxCount)
                block();
        }

        public void clear()
        {
            count = 0;
            blocked = false;
        }

        public boolean isBlocked()
        {
            return blocked;
        }

        public void block()
        {
            blocked = true;
        }

        public @Nonnull Optional<Object> putAttachment(@Nonnull Object key,
                                                       @Nonnull Object attachment)
        {
            return Optional.ofNullable(attachments.put(
                    Objects.requireNonNull(key, "key"),
                    Objects.requireNonNull(attachment, "attachment")));
        }

        public boolean hasAttachment(@Nonnull Object key)
        {
            return getAttachment(key).isPresent();
        }

        public @Nonnull Optional<Object> getAttachment(@Nonnull Object key)
        {
            return Optional.ofNullable(attachments.get(Objects.requireNonNull(key)));
        }

        private int count;

        private final int maxCount;

        private volatile boolean blocked;

        private final Map<Object, Object> attachments = new HashMap<>();
    }
}
