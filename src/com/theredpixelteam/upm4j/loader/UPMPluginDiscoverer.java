package com.theredpixelteam.upm4j.loader;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.*;

public class UPMPluginDiscoverer {
    public UPMPluginDiscoverer(PluginDiscoveringPolicy policy)
    {
        this.policy = Objects.requireNonNull(policy);
    }

    public void discover()
    {
        discover((f, n) -> true);
    }

    public void discover(FileFilter filter)
    {
        discover((f, n) -> filter.accept(f));
    }

    public synchronized void discover(FilenameFilter filter)
    {
        switch (policy.getType())
        {
            case SCAN_DIRECTORY:
                PluginDiscoveringPolicy.ScanDirectory scanDirectory =
                        (PluginDiscoveringPolicy.ScanDirectory) policy;

                File directory = scanDirectory.getDirectory();

                if (!directory.isDirectory())
                    return;

                File[] files = directory.listFiles(filter);

                if (files == null)
                    return;

                discovered.addAll(Arrays.asList(files));

                break;

            case SPECIFIC_FILES:
                PluginDiscoveringPolicy.SpecificFiles specificFiles =
                        (PluginDiscoveringPolicy.SpecificFiles) policy;

                for (File file : specificFiles.getFiles())
                    if (file.exists())
                        discovered.add(file);

                break;
        }
    }

    public Set<File> getDiscovered()
    {
        return Collections.unmodifiableSet(discovered);
    }

    public int filter(FilenameFilter filter)
    {
        return filter((file) -> filter.accept(file, file.getName()));
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
