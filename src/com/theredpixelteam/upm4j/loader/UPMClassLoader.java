package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.redtea.util.Pair;
import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.source.Source;
import com.theredpixelteam.upm4j.loader.source.SourceEntry;
import com.theredpixelteam.upm4j.loader.tweaker.ClassTweaker;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.jar.Manifest;

public class UPMClassLoader extends ClassLoader {
    public UPMClassLoader(@Nonnull UPMContext context, boolean global)
    {
        this.context = Objects.requireNonNull(context, "context");
        this.global = global;
    }

    public @Nonnull Optional<ClassTweaker> getTweaker(@Nonnull String name)
    {
        return Optional.ofNullable(tweakerMap.get(Objects.requireNonNull(name)));
    }

    public boolean hasTweaker(@Nonnull String name)
    {
        return tweakerMap.containsKey(Objects.requireNonNull(name));
    }

    public boolean registerTweaker(@Nonnull ClassTweaker tweaker)
    {
        synchronized (tweakerLock)
        {
            if (tweakerMap.putIfAbsent(tweaker.getName(), tweaker) != null)
                return false;

            Pair<ClassTweaker, Set<String>> tweakerMark = null;
            for (String dependency : tweaker.getDependencies())
            {
                if (isDependencyAvailable(dependency))
                    continue;

                if (tweakerMark == null)
                    tweakerMark = Pair.of(tweaker, new HashSet<>());

                tweakerMark.second().add(dependency);
            }

            if (tweakerMark != null)
                return true;

            tweakingPipeline.add(tweaker);

            relaxWaitingTweakers();

            return true;
        }
    }

    @Override
    protected Class<?> findClass(@Nonnull String name) throws ClassNotFoundException
    {
        if (invalidClasses.contains(name))
            throw new ClassNotFoundException(name);

        Class<?> clazz;
        if ((clazz = classCache.get(name)) != null)
            return clazz;

        String sourceName = name.replace(".", "/") + ".class";

        Source source = null;
        SourceEntry entry = null;
        synchronized (sourceLock)
        {
            for (Source src : sources.values())
            {
                Optional<SourceEntry> e = src.getEntry(sourceName);

                if (e.isPresent())
                {
                    source = src;
                    entry = e.get();
                    break;
                }
            }
        }

        if (source == null)
            return super.findClass(name);

        /*
        int dot = name.indexOf('.');
        String packageName = dot == -1 ? "" : name.substring(0, dot);

        if (getPackage(name) == null)
        {

        }
        */ // TODO Further package & manifest operation

        byte[] byts;
        try {
            byts = entry.getBytes();
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }

        synchronized (tweakerLock)
        {
            // TODO Tweaking operation
        }

        // TODO

        return null;
    }

    boolean isDependencyAvailable(String dependency)
    {
        ClassTweaker dependedTweaker;
        if ((dependedTweaker = tweakerMap.get(dependency)) != null)
            if (tweakingPipeline.contains(dependedTweaker))
                return true;

        return false;
    }

    void relaxWaitingTweakers()
    {
        if (tweakersWaitingForDependencies.isEmpty())
            return;

        ListIterator<Pair<ClassTweaker, Set<String>>> iterator =
                tweakersWaitingForDependencies.listIterator();

        while (iterator.hasNext())
        {
            Pair<ClassTweaker, Set<String>> tweakerMark = iterator.next();

            ClassTweaker tweaker = tweakerMark.first();
            Set<String> depSet = tweakerMark.second();

            depSet.removeIf(this::isDependencyAvailable);

            if (depSet.isEmpty())
            {
                iterator.remove();
                tweakingPipeline.add(tweaker);
            }
        }
    }

    public boolean addSource(Source source)
    {
        synchronized (sourceLock)
        {
            return sources.putIfAbsent(source.getName(), source) != null;
        }
    }

    public boolean removeSource(Source source)
    {
        synchronized (sourceLock)
        {
            return this.sources.remove(source.getName(), source);
        }
    }

    public boolean removeSource(String name)
    {
        synchronized (sourceLock)
        {
            return this.sources.remove(name) != null;
        }
    }

    public @Nonnull Optional<Source> getSource(String name)
    {
        return Optional.ofNullable(this.sources.get(name));
    }

    public boolean isGlobal()
    {
        return global;
    }

    public boolean isIndividual()
    {
        return !global;
    }

    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    private final boolean global;

    private final UPMContext context;

    private final Set<PluginAttribution> attachmentSet = new HashSet<>();

    private final LinkedList<Pair<ClassTweaker, Set<String>>> tweakersWaitingForDependencies = new LinkedList<>();

    private final LinkedHashSet<ClassTweaker> tweakingPipeline = new LinkedHashSet<>();

    private final Map<String, ClassTweaker> tweakerMap = new HashMap<>();

    private final Object tweakerLock = new Object();

    private final Map<String, Source> sources = new HashMap<>();

    private final Object sourceLock = new Object();

    private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    private final Set<String> invalidClasses = new ConcurrentSkipListSet<>();

    private static final Manifest EMPTY_MANIFEST = new Manifest();
}
