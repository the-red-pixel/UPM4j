package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.event.PluginFileDiscoveredEvent;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.*;

public class PluginFileDiscoverer {
    public PluginFileDiscoverer(@Nonnull UPMContext context)
    {
        this.context = context;
        this.policy = context.getFileDiscoveringPolicy();
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
                PluginFileDiscoveringPolicy.ScanDirectory scanDirectory =
                        (PluginFileDiscoveringPolicy.ScanDirectory) policy;

                File directory = scanDirectory.getDirectory();

                if (!directory.isDirectory())
                    return;

                File[] files = directory.listFiles(filter);

                if (files == null)
                    return;

                for (File file : files)
                    postAndRegster(file);

                break;

            case SPECIFIC_FILES:
                PluginFileDiscoveringPolicy.SpecificFiles specificFiles =
                        (PluginFileDiscoveringPolicy.SpecificFiles) policy;

                for (File file : specificFiles.getFiles())
                    if (file.exists())
                        postAndRegster(file);

                break;
        }
    }

    private void postAndRegster(File file)
    {
        PluginFileDiscoveredEvent event;

        context.getEventBus()
                .post(event = new PluginFileDiscoveredEvent(this, file));

        if (!event.isCancelled())
            discovered.add(file);
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

    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public void reset()
    {
        discovered.clear();
    }

    public void addFile(@Nonnull File file)
    {
        discovered.add(Objects.requireNonNull(file));
    }

    public boolean containsFile(@Nonnull File file)
    {
        return discovered.contains(Objects.requireNonNull(file));
    }

    public boolean removeFile(@Nonnull File file)
    {
        return discovered.remove(Objects.requireNonNull(file));
    }

    private final UPMContext context;

    private final Set<File> discovered = new HashSet<>();

    private final PluginFileDiscoveringPolicy policy;
}
