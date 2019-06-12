package com.theredpixelteam.upm4j.loader.tweaker;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.redtea.util.Pair;

import javax.annotation.Nonnull;
import java.util.*;

public class ClassTweakerNamespace implements Iterable<ClassTweaker> {
    public ClassTweakerNamespace()
    {
    }

    @Override
    public @Nonnull ClassTweakerNamespace clone()
    {
        synchronized (tweakerLock)
        {
            ClassTweakerNamespace dup = new ClassTweakerNamespace();

            dup.tweakersWaitingForDependencies.addAll(tweakersWaitingForDependencies);
            dup.tweakingPipeline.addAll(tweakingPipeline);

            dup.tweakerMap.putAll(tweakerMap);

            return dup;
        }
    }

    public @Nonnull Optional<ClassTweaker> getTweaker(@Nonnull String name)
    {
        return Optional.ofNullable(tweakerMap.get(Objects.requireNonNull(name)));
    }

    public boolean hasTweaker(@Nonnull String name)
    {
        return tweakerMap.containsKey(name);
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

    private boolean isDependencyAvailable(String dependency)
    {
        ClassTweaker dependedTweaker;
        if ((dependedTweaker = tweakerMap.get(dependency)) != null)
            if (tweakingPipeline.contains(dependedTweaker))
                return true;

        return false;
    }

    private void relaxWaitingTweakers()
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

    @Override
    public @Nonnull Iterator<ClassTweaker> iterator()
    {
        return tweakingPipeline.iterator();
    }

    public @Nonnull Object getTweakerLock()
    {
        return tweakerLock;
    }

    private final LinkedList<Pair<ClassTweaker, Set<String>>> tweakersWaitingForDependencies = new LinkedList<>();

    private final LinkedHashSet<ClassTweaker> tweakingPipeline = new LinkedHashSet<>();

    private final Map<String, ClassTweaker> tweakerMap = new HashMap<>();

    private final Object tweakerLock = new Object();
}
