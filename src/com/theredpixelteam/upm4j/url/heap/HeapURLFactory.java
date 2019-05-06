package com.theredpixelteam.upm4j.url.heap;

import com.theredpixelteam.upm4j.url.URLFactory;

import java.net.URL;

public class HeapURLFactory {
    private HeapURLFactory()
    {
    }

    public static URL create(HeapResources resources, String host)
    {
        return create(resources, host, "");
    }

    public static URL create(HeapResources resources, String host, String file)
    {
        return URLFactory.create(HeapURLStreamHandler.PROTOCOL, host, file, new HeapURLStreamHandler(resources));
    }

    public static URL create(String protocol, HeapResources resources, String host)
    {
        return create(protocol, resources, host, "");
    }

    public static URL create(String protocol, HeapResources resources, String host, String file)
    {
        return URLFactory.create(protocol, host, file, new HeapURLStreamHandler(protocol, resources));
    }
}
