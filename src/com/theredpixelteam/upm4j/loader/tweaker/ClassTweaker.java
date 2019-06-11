package com.theredpixelteam.upm4j.loader.tweaker;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.loader.UPMClassLoader;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

public interface ClassTweaker {
    public @Nonnull byte[] tweak(@Nonnull byte[] classBytes) throws Exception;

    public @Nonnull String getName();

    public default @Nonnull Optional<String> getDescription()
    {
        return Optional.empty();
    }

    public default @Nonnull Collection<String> getDependencies()
    {
        return Collections.emptySet();
    }

    public default void onRegister(@Nonnull UPMClassLoader upmClassLoader) {}
}
