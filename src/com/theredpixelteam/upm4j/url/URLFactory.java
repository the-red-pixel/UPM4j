package com.theredpixelteam.upm4j.url;

import com.theredpixelteam.redtea.util.ShouldNotReachHere;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;

public class URLFactory {
    private URLFactory()
    {
    }

    public static URL inMemoryURL(InMemoryResources resources, String host)
    {
        return inMemoryURL(resources, host, "");
    }

    public static URL inMemoryURL(InMemoryResources resources, String host, String file)
    {
        return createURL(InMemoryURLStreamHandler.PROTOCOL, host, file, new InMemoryURLStreamHandler(resources));
    }

    public static URL createURL(String protocol, String host, String file, URLStreamHandler handler)
    {
        try {
            return new URL(null, protocol + "://" + host + "/" + file, handler);
        } catch (IOException e) {
            // unused
            throw new ShouldNotReachHere(e);
        }
    }
}
