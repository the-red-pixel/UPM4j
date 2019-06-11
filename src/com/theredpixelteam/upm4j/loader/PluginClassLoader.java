package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.redtea.util.Pair;
import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.event.PluginClassLoaderEvent;
import com.theredpixelteam.upm4j.loader.source.Source;
import com.theredpixelteam.upm4j.loader.source.SourceEntry;
import com.theredpixelteam.upm4j.loader.tweaker.ClassTweaker;
import com.theredpixelteam.upm4j.loader.tweaker.ClassTweakerNamespace;
import com.theredpixelteam.upm4j.loader.tweaker.event.ClassTweakEvent;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.jar.Manifest;

public class PluginClassLoader extends ClassLoader {
    public PluginClassLoader(@Nonnull UPMContext context,
                             @Nonnull ClassTweakerNamespace tweakers,
                             boolean checkBytsRef,
                             boolean global)
    {
        this.context = Objects.requireNonNull(context, "context");
        this.tweakers = Objects.requireNonNull(tweakers, "tweakers");
        this.checkBytesRef = checkBytsRef;
        this.global = global;
    }

    public @Nonnull ClassTweakerNamespace getTweakers()
    {
        return tweakers;
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

        if (postTweakStart(context, name, byts))
            postTweakCancelled(context, name, byts);
        else
            synchronized (tweakers.getTweakerLock())
            {
                Set<String> cancelledTweakers = new HashSet<>();

                TWEAKER_WORKFLOW:
                for (ClassTweaker tweaker : tweakers)
                {
                    if (!cancelledTweakers.isEmpty()) // check if depending tweaker cancelled
                        for (String dependency : tweaker.getDependencies())
                            if (cancelledTweakers.contains(dependency))
                            {
                                cancelledTweakers.add(tweaker.getName());

                                postTweakerCancelled(context, name, byts, tweaker,
                                        ClassTweakEvent.TweakerCancelled.Cause.DEPENDENCY);

                                continue TWEAKER_WORKFLOW;
                            }

                    if (postTweakerEnter(context, name, byts, tweaker))
                    {
                        cancelledTweakers.add(tweaker.getName());

                        postTweakerCancelled(context, name, byts, tweaker,
                                ClassTweakEvent.TweakerCancelled.Cause.EVENT);

                        continue;
                    }

                    try {
                        byte[] oldRef = byts;

                        byts = tweaker.tweak(byts);

                        if (checkBytesRef && (byts == oldRef)) // check byte array ref
                            postTweakerIdenticalBytesRef(context, name, byts, tweaker);
                    } catch (Exception e) {
                        if (postTweakerFailure(context, name, byts, tweaker, e))
                        {
                            cancelledTweakers.add(tweaker.getName());

                            postTweakerFailureIgnored(context, name, byts, tweaker, e);

                            continue;
                        }

                        invalidClasses.add(name);

                        throw new ClassNotFoundException(name, e);
                    }
                }
            }

        // TODO Check class name

        try {
            clazz = this.defineClass(name, byts, 0, byts.length);
        } catch (Exception e) {
            if ((clazz = classCache.get(name)) != null) // concurrent failure maybe, just mute
                return clazz;

            postClassMountFailure(this, name, byts, e);

            invalidClasses.add(name);
            throw new ClassNotFoundException(name, e);
        }

        classCache.put(name, clazz);

        return clazz;
    }

    public static boolean postTweakStart(@Nonnull UPMContext context,
                                         @Nonnull String className,
                                         @Nonnull byte[] classBytes)
    {
        ClassTweakEvent.Start event = new ClassTweakEvent.Start(context, className, classBytes);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postTweakCancelled(@Nonnull UPMContext context,
                                          @Nonnull String className,
                                          @Nonnull byte[] classBytes)
    {
        context.getEventBus().post(new ClassTweakEvent.Cancelled(context, className, classBytes));
    }

    public static boolean postTweakerEnter(@Nonnull UPMContext context,
                                           @Nonnull String className,
                                           @Nonnull byte[] classBytes,
                                           @Nonnull ClassTweaker tweaker)
    {
        ClassTweakEvent.TweakerEnter event =
                new ClassTweakEvent.TweakerEnter(context, className, classBytes, tweaker);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postTweakerCancelled(@Nonnull UPMContext context,
                                            @Nonnull String className,
                                            @Nonnull byte[] classBytes,
                                            @Nonnull ClassTweaker tweaker,
                                            @Nonnull ClassTweakEvent.TweakerCancelled.Cause cause)
    {
        context.getEventBus().post(
                new ClassTweakEvent.TweakerCancelled(context, className, classBytes, tweaker, cause));
    }

    public static void postTweakerIdenticalBytesRef(@Nonnull UPMContext context,
                                                    @Nonnull String className,
                                                    @Nonnull byte[] classBytes,
                                                    @Nonnull ClassTweaker tweaker)
    {
        context.getEventBus().post(
                new ClassTweakEvent.TweakerIdenticalBytesRefWarning(context, className, classBytes, tweaker));
    }

    public static boolean postTweakerFailure(@Nonnull UPMContext context,
                                             @Nonnull String className,
                                             @Nonnull byte[] classBytes,
                                             @Nonnull ClassTweaker tweaker,
                                             @Nonnull Exception exception)
    {
        ClassTweakEvent.TweakerFailure event =
                new ClassTweakEvent.TweakerFailure(context, className, classBytes, tweaker, exception);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postTweakerFailureIgnored(@Nonnull UPMContext context,
                                                 @Nonnull String className,
                                                 @Nonnull byte[] classBytes,
                                                 @Nonnull ClassTweaker tweaker,
                                                 @Nonnull Exception exception)
    {
        context.getEventBus().post(
                new ClassTweakEvent.TweakerFailureIgnored(context, className, classBytes, tweaker, exception));
    }

    public static void postClassMountFailure(@Nonnull PluginClassLoader classLoader,
                                             @Nonnull String className,
                                             @Nonnull byte[] classBytes,
                                             @Nonnull Exception exception)
    {
        classLoader.getContext().getEventBus().post(
                new PluginClassLoaderEvent.ClassMountFailure(classLoader, className, classBytes, exception));
    }

    public boolean addSource(Source source)
    {
        synchronized (sourceLock)
        {
            return sources.putIfAbsent(source.getName(), source) != null;
        }
    }

    public boolean ifCheckBytesRef()
    {
        return checkBytesRef;
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

    private final boolean checkBytesRef;

    private final UPMContext context;

    private final Set<PluginAttribution> attachmentSet = new HashSet<>();

    private final ClassTweakerNamespace tweakers;

    private final Map<String, Source> sources = new HashMap<>();

    private final Object sourceLock = new Object();

    private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    private final Set<String> invalidClasses = new ConcurrentSkipListSet<>();

    private static final Manifest EMPTY_MANIFEST = new Manifest();
}
