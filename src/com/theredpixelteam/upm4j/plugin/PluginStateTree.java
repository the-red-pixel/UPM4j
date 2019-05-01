package com.theredpixelteam.upm4j.plugin;

import java.util.*;

public class PluginStateTree {
    PluginStateTree()
    {
    }

    public boolean canBeTransferredTo(PluginState src, PluginState dst)
    {
        Set<PluginState> set = mapped.get(src);

        if (set == null)
            return false;

        return set.contains(dst);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    final Map<PluginState, Set<PluginState>> mapped = new HashMap<>();

    public static class Builder
    {
        Builder()
        {
        }

        public Builder bind(PluginState src, PluginState... dsts)
        {
            Objects.requireNonNull(src);

            if (dsts.length == 0)
                return this;

            mapped.computeIfAbsent(src, (unused) -> new HashSet<>())
                .addAll(Arrays.asList(dsts));

            return this;
        }

        public Builder unbind(PluginState src, PluginState... dsts)
        {
            Objects.requireNonNull(src);

            if (dsts.length == 0)
                return this;

            Set<PluginState> set = mapped.get(src);

            if (set == null)
                return this;

            set.removeAll(Arrays.asList(dsts));

            return this;
        }

        public PluginStateTree build()
        {
            PluginStateTree pluginStateTree = new PluginStateTree();

            for (Map.Entry<PluginState, Set<PluginState>> entry : mapped.entrySet())
                pluginStateTree.mapped.put(entry.getKey(), new HashSet<>(entry.getValue()));

            return pluginStateTree;
        }

        private final Map<PluginState, Set<PluginState>> mapped = new HashMap<>();
    }
}
