package com.theredpixelteam.upm.loader;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UPMPluginDiscoverer {
    public UPMPluginDiscoverer(PluginDiscoveringPolicy policy)
    {
        this.policy = policy;
    }

    public void discover()
    {
        // TODO
    }

    public Set<File> getDiscovered()
    {
        return Collections.unmodifiableSet(discovered);
    }

    public int filter(FilenameFilter filter)
    {
        int count = 0;

        Iterator<File> iter = discovered.iterator();
        while (iter.hasNext())
        {
            File file = iter.next();

            if (!filter.accept(file, file.getName()))
                iter.remove();

            count++;
        }

        return count;
    }

    public int filter(FileFilter filter)
    {
        int count = 0;

        Iterator<File> iter = discovered.iterator();
        while (iter.hasNext())
        {
            File file = iter.next();

            if (!filter.accept(file))
                iter.remove();

            count++;
        }

        return count;
    }

    public int count()
    {
        return discovered.size();
    }

    public void reset()
    {
        discovered.clear();
    }

    public void addFile(File file)
    {
        discovered.add(file);
    }

    public boolean containsFile(File file)
    {
        return discovered.contains(file);
    }

    public boolean removeFile(File file)
    {
        return discovered.remove(file);
    }

    private final Set<File> discovered = new HashSet<>();

    private final PluginDiscoveringPolicy policy;
}
