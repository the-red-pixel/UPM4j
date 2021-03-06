package com.theredpixelteam.upm4j.loader.attribution.processor;

import com.theredpixelteam.upm4j.loader.attribution.AttributionWorkflow;
import com.theredpixelteam.upm4j.source.SourceEntry;
import org.objectweb.asm.tree.AnnotationNode;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.annotation.Annotation;

public interface AnnotationScanAttributionProcessor {
    public void process(@Nonnull AttributionWorkflow workflow,
                        @Nonnull Class<? extends Annotation> annotationType,
                        @Nonnull AnnotationNode annotationNode,
                        @Nonnull SourceEntry entry,
                        @Nonnull Barrier barrier)
            throws IOException;
}
