package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.redtea.util.Pair;
import com.theredpixelteam.upm4j.loader.tweaker.ClassTweaker;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class UPMClassLoader extends URLClassLoader {
    public UPMClassLoader(boolean global)
    {
        super(new URL[0]);
        this.global = global;
    }

    @Override
    public void addURL(@Nonnull URL url)
    {
        super.addURL(Objects.requireNonNull(url));
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

    @Override
    protected Class<?> findClass(String name)
    {
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

    public boolean isGlobal()
    {
        return global;
    }

    public boolean isIndividual()
    {
        return !global;
    }

    private final boolean global;

    private final Set<PluginAttribution> attachmentSet = new HashSet<>();

    private final LinkedList<Pair<ClassTweaker, Set<String>>> tweakersWaitingForDependencies = new LinkedList<>();

    private final LinkedHashSet<ClassTweaker> tweakingPipeline = new LinkedHashSet<>();

    private final Map<String, ClassTweaker> tweakerMap = new HashMap<>();
}
