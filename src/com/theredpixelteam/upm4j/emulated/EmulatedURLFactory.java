package com.theredpixelteam.upm4j.emulated;

import com.theredpixelteam.upm4j.url.URLFactory;

import javax.annotation.Nonnull;
import java.net.URL;

public final class EmulatedURLFactory {
    private EmulatedURLFactory()
    {
    }

    public static @Nonnull URL create(@Nonnull EmulatedTranscation transcation)
    {
        return URLFactory.create(
                "emulated",
                transcation.getRoot().getName().orElse("emulated"),
                transcation.getPath(),
                new EmulatedURLStreamHandler(transcation));
    }
}
