package com.theredpixelteam.upm4j.plugin;

import com.theredpixelteam.upm4j.loader.source.SourceEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PluginAttribution {
    PluginAttribution(@Nonnull String mainClass,
                      @Nonnull SourceEntry mainEntry,
                      @Nonnull String identity,
                      @Nullable String name,
                      @Nullable String version,
                      @Nullable String description,
                      @Nullable String website,
                      @Nonnull Collection<String> authors,
                      @Nonnull Map<String, Object> extraAttributions)
    {
        this.mainClass = Objects.requireNonNull(mainClass, "mainClass");
        this.mainEntry = Objects.requireNonNull(mainEntry, "mainEntry");
        this.identity = Objects.requireNonNull(identity, "identity");
        this.name = name;
        this.version = version;
        this.description = description;
        this.website = website;
        this.authors = Objects.requireNonNull(authors);
        this.extraAttributions = Objects.requireNonNull(extraAttributions);
    }

    public @Nonnull String getIdentity()
    {
        return this.identity;
    }

    public @Nonnull Optional<String> getName()
    {
        return Optional.ofNullable(this.name);
    }

    public @Nonnull String getMainClass()
    {
        return mainClass;
    }

    public @Nonnull SourceEntry getMainEntry()
    {
        return mainEntry;
    }

    public @Nonnull Optional<String> getVersion()
    {
        return Optional.ofNullable(this.version);
    }

    public @Nonnull Optional<String> getDescription()
    {
        return Optional.ofNullable(this.description);
    }

    public @Nonnull Optional<String> getWebsite()
    {
        return Optional.ofNullable(website);
    }

    public @Nonnull Collection<String> getAuthors()
    {
        return this.authors;
    }

    public @Nonnull Map<String, Object> getExtraAttributes()
    {
        return extraAttributions;
    }

    public @Nonnull Optional<Object> getAttribute(@Nonnull String name)
    {
        return Optional.ofNullable(extraAttributions.get(Objects.requireNonNull(name)));
    }

    @SuppressWarnings("unchecked")
    public @Nonnull <T> Optional<T> getAttribute(@Nonnull String name,
                                                 @Nonnull Class<T> type)
    {
        Object attr = extraAttributions.get(Objects.requireNonNull(name));

        if (!type.isInstance(attr))
            return Optional.empty();

        return Optional.of((T) attr);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    private final String mainClass;

    private final SourceEntry mainEntry;

    private final String name;

    private final String identity;

    private final String version;

    private final String description;

    private final String website;

    private final Collection<String> authors;

    private final Map<String, Object> extraAttributions;

    public static class Builder
    {
        Builder()
        {
        }

        public @Nonnull Builder identity(@Nonnull String identity)
        {
            this.identity = Objects.requireNonNull(identity);
            return this;
        }

        public @Nonnull Builder name(@Nullable String name)
        {
            this.name = name;
            return this;
        }

        public @Nonnull Builder version(@Nullable String version)
        {
            this.version = version;
            return this;
        }

        public @Nonnull Builder description(@Nullable String description)
        {
            this.description = description;
            return this;
        }

        public @Nonnull Builder website(@Nullable String website)
        {
            this.website = website;
            return this;
        }

        public @Nonnull Builder mainClass(@Nonnull String mainClass)
        {
            this.mainClass = Objects.requireNonNull(mainClass);
            return this;
        }

        public @Nonnull Builder mainEntry(@Nullable SourceEntry mainEntry)
        {
            this.mainEntry = Objects.requireNonNull(mainEntry);
            return this;
        }

        public @Nullable String getIdentity()
        {
            return identity;
        }

        public @Nullable String getName()
        {
            return name;
        }

        public @Nullable String getDescription()
        {
            return description;
        }

        public @Nullable String getVersion()
        {
            return version;
        }

        public @Nullable String getWebsite()
        {
            return website;
        }

        public @Nullable String getMainClass()
        {
            return mainClass;
        }

        public @Nullable SourceEntry getMainEntry()
        {
            return mainEntry;
        }

        public @Nonnull Collection<String> getAuthors()
        {
            return authors;
        }

        public @Nonnull Map<String, Object> getExtraAttributions()
        {
            return extraAttributions;
        }

        public @Nonnull Builder addAuthor(@Nonnull String name)
        {
            authors.add(name);
            return this;
        }

        public @Nonnull Builder removeAuthor(@Nonnull String name)
        {
            authors.remove(name);
            return this;
        }

        public @Nonnull Builder putAttribute(@Nonnull String name, @Nonnull Object value)
        {
            extraAttributions.put(
                    Objects.requireNonNull(name, "name"),
                    Objects.requireNonNull(value, "value"));
            return this;
        }

        public @Nonnull Builder removeAttribute(@Nonnull String name)
        {
            extraAttributions.remove(Objects.requireNonNull(name));
            return this;
        }

        public PluginAttribution build()
        {
            return new PluginAttribution(
                    mainClass,
                    mainEntry,
                    identity,
                    name,
                    version,
                    description,
                    website,
                    authors,
                    extraAttributions
            );
        }

        private String mainClass;

        private SourceEntry mainEntry;

        private String name;

        private String identity;

        private String version;

        private String description;

        private String website;

        private final Collection<String> authors = new HashSet<>();

        private final Map<String, Object> extraAttributions = new HashMap<>();
    }
}
