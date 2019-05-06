package com.theredpixelteam.upm4j.url.heap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class HeapURLStreamHandler extends URLStreamHandler {
    public HeapURLStreamHandler(HeapResources resources)
    {
        this(PROTOCOL, resources);
    }

    public HeapURLStreamHandler(String protocol, HeapResources resources)
    {
        this.protocol = protocol;
        this.resources = resources;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException
    {
        if(!u.getProtocol().equals(PROTOCOL))
            throw new MalformedURLException("Unknown protocol: " + u.getProtocol());

        return new HeapURLConnection(resources, u);
    }

    public String getProtocol()
    {
        return protocol;
    }

    private final HeapResources resources;

    private final String protocol;

    public static final String PROTOCOL = "heap";
}
