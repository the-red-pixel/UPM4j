package com.theredpixelteam.upm4j.inject;

import com.theredpixelteam.redtea.util.Cluster;
import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.invoke.InvokerProvider;
import org.kucro3.jam2.invoke.ConstructorInvoker;
import org.kucro3.jam2.invoke.MethodInvoker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class PluginInjector {
    PluginInjector(@Nonnull PluginInjectionPattern pattern,
                   @Nonnull MethodInvoker invoker)
    {
        this.pattern = pattern;
        this.invoker = invoker;
    }

    public Object inject(@Nullable Object obj,
                         @Nonnull Cluster arguments)
            throws InvocationTargetException
    {
        Objects.requireNonNull(arguments, "arguments");

        assert pattern.getParamCount() == invoker.getArgumentCount();

        Object[] args = new Object[pattern.getParamCount()];
        Factory[] factories = pattern.getParamFactories();

        for (int i = 0; i < factories.length; i++)
            args[i] = factories[i].produce(arguments);

        return invoker.invoke(obj, args);
    }

    public @Nonnull PluginInjectionPattern getPattern()
    {
        return pattern;
    }

    public @Nonnull MethodInvoker getInvoker()
    {
        return invoker;
    }

    public static @Nonnull Optional<PluginInjector> ofConstructor(@Nonnull InvokerProvider invokerProvider,
                                                                  @Nonnull PluginInjection injection,
                                                                  @Nonnull Class<?> type)
    {
        Objects.requireNonNull(invokerProvider, "invokerProvider");
        Objects.requireNonNull(injection, "injection");
        Objects.requireNonNull(type, "type");

        for (PluginInjectionPattern pattern : injection.getPatterns())
        {
            Constructor<?> constructor;

            if ((constructor = getConstructorSilently(type, pattern.getParamTypes())) == null)
                continue;

            ConstructorInvoker invoker = invokerProvider.provideConstructorInvoker(constructor);

            return Optional.of(new PluginInjector(pattern, invoker));
        }

        return Optional.empty();
    }

    private static @Nullable Constructor<?> getConstructorSilently(@Nonnull Class<?> declaringClass,
                                                                   @Nonnull Class<?>[] paramTypes)
    {
        try {
            return declaringClass.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static @Nullable Optional<PluginInjector> ofMethod(@Nonnull InvokerProvider invokerProvider,
                                                              @Nonnull PluginInjection injection,
                                                              @Nonnull Class<?> type,
                                                              @Nonnull String methodName)
    {
        Objects.requireNonNull(invokerProvider, "invokerProvider");
        Objects.requireNonNull(injection, "injection");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(methodName, "methdoName");

        for (PluginInjectionPattern pattern : injection.getPatterns())
        {
            Method method;

            if ((method = getMethodSilently(type, methodName, pattern.getParamTypes())) == null)
                continue;

            MethodInvoker invoker = invokerProvider.provideMethodInvoker(method);

            return Optional.of(new PluginInjector(pattern, invoker));
        }

        return Optional.empty();
    }

    private static @Nullable Method getMethodSilently(@Nonnull Class<?> declaringClass,
                                                      @Nonnull String name,
                                                      @Nonnull Class<?>[] paramTypes)
    {
        try {
            return declaringClass.getMethod(name, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private final PluginInjectionPattern pattern;

    private final MethodInvoker invoker;
}
