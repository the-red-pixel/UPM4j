package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.jam3.ClassNameReader;
import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.event.PluginClassLoaderEvent;
import com.theredpixelteam.upm4j.loader.source.Source;
import com.theredpixelteam.upm4j.loader.source.SourceEntry;
import com.theredpixelteam.upm4j.loader.tweaker.ClassTweaker;
import com.theredpixelteam.upm4j.loader.tweaker.ClassTweakerNamespace;
import com.theredpixelteam.upm4j.loader.tweaker.event.ClassTweakEvent;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class PluginClassLoader extends ClassLoader {
    public PluginClassLoader(@Nullable ClassLoader parent,
                             @Nonnull UPMContext context,
                             boolean checkBytsRef,
                             boolean checkClassName,
                             boolean global)
    {
        super(parent);
        this.context = Objects.requireNonNull(context, "context");
        this.tweakers = context.getTweakers().clone();
        this.checkBytesRef = checkBytsRef;
        this.checkClassName = checkClassName;
        this.global = global;
    }

    public PluginClassLoader(@Nonnull UPMContext context,
                             boolean checkBytsRef,
                             boolean checkClassName,
                             boolean global)
    {
        this(null, context, checkBytsRef, checkClassName, global);
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

        String sourceName = name.replace('.', '/') + ".class";

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

        int dot = name.indexOf('.');
        String packageName = dot == -1 ? null : name.substring(0, dot);

        if (packageName != null && getPackage(packageName) == null)
        {
            try {
                Manifest manifest;
                if ((manifest = source.getManifest().orElse(null)) == null)
                {
                    Package pac = definePackage(packageName, null, null,
                                null, null, null, null, null);

                    postPackageDefinitionPassed(this, pac);
                }
                else
                {
                    // referenced to URLClassLoader
                    String specTitle = null, specVersion = null, specVendor = null;
                    String implTitle = null, implVersion = null, implVendor = null;
                    String sealed = null;
                    URL sealBase = null;

                    String path = packageName.replace('.', '/') + '/';

                    Attributes attr = manifest.getAttributes(path);
                    if (attr != null)
                    {
                        specTitle   = attr.getValue(Attributes.Name.SPECIFICATION_TITLE);
                        specVersion = attr.getValue(Attributes.Name.SPECIFICATION_VERSION);
                        specVendor  = attr.getValue(Attributes.Name.SPECIFICATION_VENDOR);
                        implTitle   = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
                        implVersion = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
                        implVendor  = attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
                        sealed      = attr.getValue(Attributes.Name.SEALED);
                    }

                    attr = manifest.getMainAttributes();
                    if (attr != null)
                    {
                        if (specTitle == null)
                            specTitle = attr.getValue(Attributes.Name.SPECIFICATION_TITLE);

                        if (specVersion == null)
                            specVersion = attr.getValue(Attributes.Name.SPECIFICATION_VERSION);

                        if (specVendor == null)
                            specVendor = attr.getValue(Attributes.Name.SPECIFICATION_VENDOR);

                        if (implTitle == null)
                            implTitle = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);

                        if (implVersion == null)
                            implVersion = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);

                        if (implVendor == null)
                            implVendor = attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);

                        if (sealed == null)
                            sealed = attr.getValue(Attributes.Name.SEALED);
                    }

                    Optional<URL> url;
                    if (Boolean.valueOf(sealed) && (url = source.getURL()).isPresent())
                        sealBase = url.get();

                    Package pack =
                            definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);

                    postPackageDefinitionPassed(this, pack);
                }
            } catch (IOException | IllegalArgumentException e) {
                postPackageDefinitionFailure(this, packageName, e);

            }
        }

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

        // check class name
        if (checkClassName)
        {
            String newName = ClassNameReader.from(ByteBuffer.wrap(byts)).replace('/', '.');

            if (!newName.equals(name) && postClassNameChanged(this, name, newName, byts))
            {
                postClassNameChangeRejected(this, name, newName, byts);

                invalidClasses.add(name);
                throw new ClassNotFoundException(name);
            }
        }

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

        postClassMountPassed(this, clazz);

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

    public static boolean postClassNameChanged(@Nonnull PluginClassLoader classLoader,
                                               @Nonnull String oldClassName,
                                               @Nonnull String newClassName,
                                               @Nonnull byte[] classBytes)
    {
        PluginClassLoaderEvent.ClassNameChanged event
                = new PluginClassLoaderEvent.ClassNameChanged(classLoader, oldClassName, newClassName, classBytes);

        classLoader.getContext().getEventBus().post(event);

        return event.isRejected();
    }

    public static void postClassNameChangeRejected(@Nonnull PluginClassLoader classLoader,
                                                   @Nonnull String oldClassName,
                                                   @Nonnull String newClassName,
                                                   @Nonnull byte[] classBytes)
    {
        classLoader.getContext().getEventBus().post(
                new PluginClassLoaderEvent.ClassNameChangeRejected(classLoader, oldClassName, newClassName, classBytes));
    }

    public static void postClassMountPassed(@Nonnull PluginClassLoader classLoader,
                                            @Nonnull Class<?> classInstance)
    {
        classLoader.getContext().getEventBus().post(
                new PluginClassLoaderEvent.ClassMountPassed(classLoader, classInstance));
    }

    public static void postPackageDefinitionFailure(@Nonnull PluginClassLoader classLoader,
                                                    @Nonnull String packageName,
                                                    @Nonnull Exception cause)
    {
        classLoader.getContext().getEventBus().post(
                new PluginClassLoaderEvent.PackageDefinitionFailure(classLoader, packageName, cause));
    }

    public static void postPackageDefinitionPassed(@Nonnull PluginClassLoader classLoader,
                                                   @Nonnull Package pack)
    {
        classLoader.getContext().getEventBus().post(
                new PluginClassLoaderEvent.PackageDefinitionPassed(classLoader, pack));
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

    private final boolean checkClassName;

    private final UPMContext context;

    private final Set<PluginAttribution> attachmentSet = new HashSet<>();

    private final ClassTweakerNamespace tweakers;

    private final Map<String, Source> sources = new HashMap<>();

    private final Object sourceLock = new Object();

    private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    private final Set<String> invalidClasses = new ConcurrentSkipListSet<>();

    private static final Manifest EMPTY_MANIFEST = new Manifest();
}
