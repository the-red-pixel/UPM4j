package com.theredpixelteam.upm4j.invoke;

import org.kucro3.jam2.invoke.ConstructorInvoker;
import org.kucro3.jam2.invoke.FieldInvoker;
import org.kucro3.jam2.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassicInvokerProviders {
    private ClassicInvokerProviders()
    {
    }

    public static final InvokerProvider ASM = new InvokerProvider() {
        @Override
        public MethodInvoker provideMethodInvoker(Method method)
        {
            return MethodInvoker.newInvokerByASM(method);
        }

        @Override
        public FieldInvoker provideFieldInvoker(Field field)
        {
            return FieldInvoker.newInvokerByASM(field);
        }

        @Override
        public ConstructorInvoker provideConstructorInvoker(Constructor<?> constructor)
        {
            return ConstructorInvoker.newInvokerByASM(constructor);
        }
    };

    public static final InvokerProvider REFLECTION = new InvokerProvider() {
        @Override
        public MethodInvoker provideMethodInvoker(Method method)
        {
            return MethodInvoker.newInvokerByReflection(method);
        }

        @Override
        public FieldInvoker provideFieldInvoker(Field field)
        {
            return FieldInvoker.newInvokeByReflection(field);
        }

        @Override
        public ConstructorInvoker provideConstructorInvoker(Constructor<?> constructor)
        {
            return ConstructorInvoker.newInvokerByReflection(constructor);
        }
    };
}
