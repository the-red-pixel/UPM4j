package com.theredpixelteam.upm4j.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;

public class URLFactory {
    private URLFactory()
    {
    }

    public static URL create(String protocol, String host, String file, URLStreamHandler handler)
    {
        try {
            return new URL(null, protocol + "://" + host + "/" + file, handler);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
