package com.theredpixelteam.upm4j.invoke;

import org.kucro3.jam2.invoke.ConstructorInvoker;
import org.kucro3.jam2.invoke.FieldInvoker;
import org.kucro3.jam2.invoke.MethodInvoker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface InvokerProvider {
    public @Nonnull MethodInvoker provideMethodInvoker(@Nonnull Method method,
                                                       @Nonnull ClassLoader classLoader);

    public default @Nonnull MethodInvoker provideMethodInvoker(@Nonnull Method method)
    {
        return provideMethodInvoker(method, this.getClass().getClassLoader());
    }

    public @Nonnull FieldInvoker provideFieldInvoker(@Nonnull Field field,
                                                     @Nonnull ClassLoader classLoader);

    public default @Nonnull FieldInvoker provideFieldInvoker(@Nonnull Field field)
    {
        return provideFieldInvoker(field, this.getClass().getClassLoader());
    }

    public @Nonnull ConstructorInvoker provideConstructorInvoker(@Nonnull Constructor<?> constructor,
                                                                 @Nonnull ClassLoader classLoader);

    public default @Nonnull ConstructorInvoker provideConstructorInvoker(@Nonnull Constructor<?> constructor)
    {
        return provideConstructorInvoker(constructor, this.getClass().getClassLoader());
    }
}
