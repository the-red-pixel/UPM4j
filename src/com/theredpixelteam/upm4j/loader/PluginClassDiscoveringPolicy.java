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
                                                                  @Nonnull PluginSource source)
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



    // TODO

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
                                                             @Nonnull PluginSource source)
                throws IOException
        {
            Objects.requireNonNull(source);

            AttributionWorkflow workflow = new AttributionWorkflow(context);

            for (String name : names)
            {
                String sourceName =
                        Jam2Util.fromInternalNameToResource(
                        Jam2Util.fromCanonicalToInternalName(name));

                source.getEntry(sourceName).ifPresent(
                        (entry) -> processor.provide(workflow, name, entry));
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
                                                             @Nonnull PluginSource source)
                throws IOException
        {
            AttributionWorkflow workflow = new AttributionWorkflow(context);

            processor.provide(workflow, source);

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
                                                             @Nonnull PluginSource source)
                throws IOException
        {
            AttributionWorkflow workflow = new AttributionWorkflow(context);

            for (String file : files)
                source.getEntry(file).ifPresent((entry) -> processor.provide(workflow, entry));

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
                                                             @Nonnull PluginSource source)
                throws IOException
        {
            AttributionWorkflow workflow = new AttributionWorkflow(context);

            for (PluginSourceEntry entry :
                    source.getEntries((SourceEntryNameFilter) name -> name.endsWith(".class")))
            {
                try {
                    ClassReader reader = new ClassReader(entry.getBytes());
                    ClassNode classNode = new ClassNode();

                    reader.accept(classNode, 0);

                    for (Class<? extends Annotation> annotationType : annotationTypes)
                        Annotations.getAnnotationNode(classNode, annotationType).ifPresent(
                                node -> processor.process(workflow, annotationType, node, entry));

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
        public @Nonnull Collection<PluginAttribution> search(@Nonnull UPMContext context, @Nonnull PluginSource source)
                throws IOException
        {
            AttributionWorkflow workflow = new AttributionWorkflow(context);

            for (PluginSourceEntry entry :
                    source.getEntries((SourceEntryNameFilter) name -> name.endsWith(".class")))
            {
                try {
                    ClassReader reader = new ClassReader(entry.getBytes());
                    ClassNode classNode = new ClassNode();

                    reader.accept(classNode, 0);

                    Class<?> superclass;
                    if (classNode.superName != null
                            && (superclass = superclasses.get(classNode.superName)) != null)
                        processor.process(workflow, superclass, entry);

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
                                                             @Nonnull PluginSource source)
                throws IOException
        {
            AttributionWorkflow workflow = new AttributionWorkflow(context);

            processor.process(workflow, source);

            return workflow.buildAll();
        }

        private final CustomAttributionProcessor processor;
    }
}
