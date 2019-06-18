package com.theredpixelteam.upm4j.invoke;

import org.kucro3.jam2.invoke.ConstructorInvoker;
import org.kucro3.jam2.invoke.FieldInvoker;
import org.kucro3.jam2.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface InvokerProvider {
    public MethodInvoker provideMethodInvoker(Method method);

    public FieldInvoker provideFieldInvoker(Field field);

    public ConstructorInvoker provideConstructorInvoker(Constructor<?> constructor);
}
