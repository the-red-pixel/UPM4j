package com.theredpixelteam.upm4j.loader;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public abstract class PluginDiscoveringPolicy {
    PluginDiscoveringPolicy(Type type)
    {
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    public static PluginDiscoveringPolicy.ScanDirectory ofScanDirectory(File directory)
    {
        return new ScanDirectory(Objects.requireNonNull(directory));
    }

    public static PluginDiscoveringPolicy.SpecificFiles ofSpecificFiles(File... files)
    {
        return ofSpecificFiles(Arrays.asList(files));
    }

    public static PluginDiscoveringPolicy.SpecificFiles ofSpecificFiles(Collection<File> files)
    {
        return new SpecificFiles(new HashSet<>(files));
    }

    public static class ScanDirectory extends PluginDiscoveringPolicy
    {
        ScanDirectory(File directory)
        {
            super(Type.SCAN_DIRECTORY);
            this.directory = directory;
        }

        public File getDirectory()
        {
            return directory;
        }

        private final File directory;
    }

    public static class SpecificFiles extends PluginDiscoveringPolicy
    {
        SpecificFiles(Collection<File> files)
        {
            super(Type.SPECIFIC_FILES);
            this.files = files;
        }

        public Collection<File> getFiles()
        {
            return files;
        }

        private final Collection<File> files;
    }

    private final Type type;

    public static enum Type
    {
        SCAN_DIRECTORY,
        SPECIFIC_FILES
    }
}
