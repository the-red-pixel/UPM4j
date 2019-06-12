package com.theredpixelteam.upm4j.inject;

import com.theredpixelteam.redtea.util.ShouldNotReachHere;

import javax.annotation.Nonnull;
import java.util.*;

public final class PluginInjection {
    PluginInjection(@Nonnull PatternPriority priority,
                    @Nonnull LinkedHashMap<Class<?>[], PluginInjectionPattern> patterns)
    {
        this.priority = priority;
        this.patterns = new PluginInjectionPattern[patterns.size()];

        int i;
        switch (priority)
        {
            case FIRST_IN_FIRST:
                i = 0;

                for (PluginInjectionPattern pattern : patterns.values())
                    this.patterns[i++] = pattern;

                break;

            case LAST_IN_FIRST:
                i = this.patterns.length;

                for (PluginInjectionPattern pattern : patterns.values())
                    this.patterns[--i] = pattern;

                break;

            default:
                throw new ShouldNotReachHere();
        }
    }

    public @Nonnull Collection<PluginInjectionPattern> getPatterns()
    {
        return Collections.unmodifiableCollection(Arrays.asList(patterns));
    }

    public @Nonnull PatternPriority getPatternPriority()
    {
        return priority;
    }

    public static @Nonnull Builder builder()
    {
        return new Builder();
    }

    private final PluginInjectionPattern[] patterns;

    private final PatternPriority priority;

    public static enum PatternPriority
    {
        FIRST_IN_FIRST,
        LAST_IN_FIRST
    }

    public static class Builder
    {
        Builder()
        {
        }

        public @Nonnull Builder appendPattern(@Nonnull PluginInjectionPattern pattern)
        {
            Objects.requireNonNull(pattern);
            patterns.put(pattern.getParamTypes(), pattern);

            return this;
        }

        public boolean hasPattern(@Nonnull Class<?>[] paramTypes)
        {
            return patterns.containsKey(Objects.requireNonNull(paramTypes));
        }

        public boolean hasPattern(@Nonnull Class<?>[] paramTypes,
                                  @Nonnull Factory[] factories)
        {
            Objects.requireNonNull(paramTypes, "paramTypes");
            Objects.requireNonNull(factories, "factories");

            PluginInjectionPattern pattern;

            if ((pattern = patterns.get(paramTypes)) == null)
                return false;

            return Arrays.equals(factories, pattern.getParamFactories());
        }

        public boolean removePattern(@Nonnull Class<?>[] paramTypes)
        {
            return patterns.remove(Objects.requireNonNull(paramTypes)) != null;
        }

        public boolean removePattern(@Nonnull Class<?>[] paramTypes,
                                     @Nonnull Factory[] factories)
        {
            Objects.requireNonNull(paramTypes, "paramTypes");
            Objects.requireNonNull(factories, "factories");

            PluginInjectionPattern pattern;

            if ((pattern = patterns.get(paramTypes)) == null)
                return false;

            if (Arrays.equals(factories, pattern.getParamFactories()))
            {
                patterns.remove(paramTypes);

                return true;
            }

            return false;
        }

        public @Nonnull PluginInjection build()
        {
            return new PluginInjection(priority, new LinkedHashMap<>(patterns));
        }

        private final Map<Class<?>[], PluginInjectionPattern> patterns = new LinkedHashMap<>();

        private PatternPriority priority = PatternPriority.FIRST_IN_FIRST;
    }
}
