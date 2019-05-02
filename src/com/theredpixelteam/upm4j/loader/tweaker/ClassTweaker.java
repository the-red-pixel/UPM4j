package com.theredpixelteam.upm4j.loader.tweaker;

import com.theredpixelteam.redtea.util.Optional;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

public interface ClassTweaker {
    public @Nonnull ClassNode tweak(@Nonnull ClassNode node) throws Exception;

    public @Nonnull String getName();

    public default @Nonnull Optional<String> getDescription()
    {
        return Optional.empty();
    }

    public default @Nonnull Collection<String> getDependencies()
    {
        return Collections.emptySet();
    }
}
