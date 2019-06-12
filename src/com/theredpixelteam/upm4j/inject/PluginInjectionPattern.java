package com.theredpixelteam.upm4j.inject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class PluginInjectionPattern {
    PluginInjectionPattern(@Nonnull Factory[] paramFactories,
                           @Nonnull Class<?>[] paramTypes)
    {
        assert paramFactories.length == paramTypes.length;

        this.paramTypes = paramTypes;
        this.paramFactories = paramFactories;

        this.paramCount = paramTypes.length;
    }

    public @Nonnull Class<?>[] getParamTypes()
    {
        return paramTypes.clone();
    }

    public @Nonnull Factory[] getParamFactories()
    {
        return paramFactories.clone();
    }

    public int getParamCount()
    {
        return paramCount;
    }

    public static @Nonnull Builder builder()
    {
        return new Builder();
    }

    public static @Nonnull PluginInjectionPattern empty()
    {
        return EMPTY;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(paramFactories) * 31 + Arrays.hashCode(paramTypes);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof PluginInjectionPattern))
            return false;

        PluginInjectionPattern object = (PluginInjectionPattern) obj;

        if (paramCount != object.paramCount)
            return false;

        if (!Arrays.equals(paramTypes, object.paramTypes))
            return false;

        return Arrays.equals(paramFactories, object.paramFactories);
    }

    private final Factory[] paramFactories;

    private final Class<?>[] paramTypes;

    private final int paramCount;

    private static final PluginInjectionPattern EMPTY = new PluginInjectionPattern(new Factory[0], new Class<?>[0]);

    public static class Builder
    {
        Builder()
        {
        }

        public @Nonnull Builder append(@Nonnull Class<?> type,
                                       @Nonnull Factory factory)
        {
            paramTypes.add(Objects.requireNonNull(type,"type"));
            paramFactories.add(Objects.requireNonNull(factory, "factory"));

            return this;
        }

        public @Nonnull Builder remove(int index)
        {
            paramTypes.remove(index);
            paramFactories.remove(index);

            return this;
        }

        public @Nonnull Builder removeFirst(@Nonnull Class<?> paramType)
        {
            for (int i = 0; i < paramTypes.size(); i++)
                if (paramTypes.get(i).equals(paramType))
                {
                    paramTypes.remove(i);
                    paramFactories.remove(i);

                    break;
                }

            return this;
        }

        public @Nonnull Builder removeLast(@Nonnull Class<?> paramType)
        {
            for (int i = paramTypes.size() - 1; i > -1; i--)
                if (paramTypes.get(i).equals(paramType))
                {
                    paramTypes.remove(i);
                    paramFactories.remove(i);

                    break;
                }

            return this;
        }

        public @Nonnull PluginInjectionPattern build()
        {
            return new PluginInjectionPattern(
                    paramFactories.toArray(new Factory[0]),
                    paramTypes.toArray(new Class<?>[0])
            );
        }

        private final List<Class<?>> paramTypes = new ArrayList<>();

        private final List<Factory> paramFactories = new ArrayList<>();
    }
}
