package com.theredpixelteam.upm4j.loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.ProtectionDomain;

public interface ClassDefineExposed {
    public @Nonnull Class<?> define(@Nullable String name,
                                    @Nonnull byte[] byts,
                                    int off,
                                    int len);

    public @Nonnull Class<?> define(@Nullable String name,
                                    @Nonnull byte[] byts,
                                    int off,
                                    int len,
                                    @Nullable ProtectionDomain protectionDomain);
}
