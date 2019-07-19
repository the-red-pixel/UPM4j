package com.theredpixelteam.upm4j.emulated.file;

import com.theredpixelteam.upm4j.emulated.Emulated;
import com.theredpixelteam.upm4j.emulated.EmulatedTranscation;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public class FileEmulated implements Emulated {
    public FileEmulated(@Nonnull File root)
    {
        this.root = Objects.requireNonNull(root);
    }

    @Override
    public @Nonnull Optional<EmulatedTranscation> acquire(@Nonnull String path)
    {
        path = reformatPath(path);

        if (transcations.containsKey(path))
            return Optional.empty();
        else
        {
            File file = new File(root, path);

            FileEmulatedTranscation transcation
                    = new FileEmulatedTranscation(this, file, path, file.getName());

            if (transcations.putIfAbsent(path, transcation) == null)
            {
                transcation.enable();

                return Optional.of(transcation);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean idle(@Nonnull String path)
    {
        return !(transcations.containsKey(reformatPath(path)) || RESERVED.contains(new File(path).getName()));
    }

    @Override
    public @Nonnull String[] list(@Nonnull String path)
    {
        File[] files = new File(root, path).listFiles(File::isFile);

        if (files == null)
            return new String[0];

        String[] paths = new String[files.length];

        int prefLen = root.getAbsolutePath().length();
        for (int i = 0; i < paths.length; i++)
            paths[i] = files[i].getAbsolutePath().substring(prefLen);

        return paths;
    }

    public @Nonnull File getRoot()
    {
        return root;
    }

    void unregister(FileEmulatedTranscation transcation)
    {
        transcations.remove(transcation.getPath(), transcation);
    }

    private static String reformatPath(String path)
    {
        if (!path.startsWith("\\"))
            return "\\" + path;
        return path;
    }

    static {
        Set<String> reserved = new HashSet<>();

        String os = System.getProperty("os.name");

        if (os.startsWith("Windows"))
            reserved.addAll(Arrays.asList(
                    "CON", "PRN", "AUX", "NUL",
                    "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
                    "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
            ));

        RESERVED = reserved;
    }

    private static final Set<String> RESERVED;

    private final File root;

    private final Map<String, EmulatedTranscation> transcations = new ConcurrentHashMap<>();
}
