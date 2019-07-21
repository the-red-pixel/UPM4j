package com.theredpixelteam.upm4j.invoke;

import com.theredpixelteam.upm4j.loader.ClassDefineExposed;
import org.kucro3.jam2.invoke.ConstructorInvoker;
import org.kucro3.jam2.invoke.FieldInvoker;
import org.kucro3.jam2.invoke.MethodInvoker;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassicInvokerProviders {
    private ClassicInvokerProviders()
    {
    }

    public static final InvokerProvider ASM = new InvokerProvider() {
        @Override
        public @Nonnull MethodInvoker provideMethodInvoker(@Nonnull Method method,
                                                           @Nonnull ClassLoader classLoader)
        {
            return MethodInvoker.newInvokerByASM(method, checkcast(classLoader)::define);
        }

        @Override
        public @Nonnull FieldInvoker provideFieldInvoker(@Nonnull Field field,
                                                @Nonnull ClassLoader classLoader)
        {
            return FieldInvoker.newInvokerByASM(field, checkcast(classLoader)::define);
        }

        @Override
        public @Nonnull ConstructorInvoker provideConstructorInvoker(@Nonnull Constructor<?> constructor,
                                                                     @Nonnull ClassLoader classLoader)
        {
            return ConstructorInvoker.newInvokerByASM(constructor, checkcast(classLoader)::define);
        }

        private ClassDefineExposed checkcast(@Nonnull ClassLoader classLoader)
        {
            if (!(classLoader instanceof ClassDefineExposed))
                throw new UnsupportedOperationException("ClassLoader (" + classLoader + ") isn't class define exposed");

            return (ClassDefineExposed) classLoader;
        }
    };

    public static final InvokerProvider REFLECTION = new InvokerProvider() {
        @Override
        public @Nonnull MethodInvoker provideMethodInvoker(@Nonnull Method method,
                                                           @Nonnull ClassLoader classLoader)
        {
            return MethodInvoker.newInvokerByReflection(method);
        }

        @Override
        public @Nonnull FieldInvoker provideFieldInvoker(@Nonnull Field field,
                                                         @Nonnull ClassLoader classLoader)
        {
            return FieldInvoker.newInvokeByReflection(field);
        }

        @Override
        public @Nonnull ConstructorInvoker provideConstructorInvoker(@Nonnull Constructor<?> constructor,
                                                                     @Nonnull ClassLoader classLoader)
        {
            return ConstructorInvoker.newInvokerByReflection(constructor);
        }
    };
}
