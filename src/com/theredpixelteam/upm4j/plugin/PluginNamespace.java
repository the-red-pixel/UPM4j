package com.theredpixelteam.upm4j.plugin;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.loader.exception.PluginIdentityDuplicationException;
import com.theredpixelteam.upm4j.loader.exception.PluginMountingException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PluginNamespace {
    public PluginNamespace()
    {
    }

    public void registerPlugin(Plugin plugin)
            throws PluginMountingException
    {
        if (plugins.putIfAbsent(plugin.getAttribution().getIdentity(), plugin) != null)
            throw new PluginIdentityDuplicationException(plugin.getAttribution().getIdentity());
    }

    public Optional<Plugin> getPlugin(String identity)
    {
        return Optional.ofNullable(plugins.get(identity));
    }

    public Collection<Plugin> getAllPlugins()
    {
        return Collections.unmodifiableCollection(plugins.values());
    }

    public void loadAll()
    {
        for (Plugin plugin : plugins.values())
            plugin.load();
    }

    public void enableAll()
    {
        for (Plugin plugin : plugins.values())
            plugin.enable();
    }

    public void disableAll()
    {
        for (Plugin plugin : plugins.values())
            plugin.disable();
    }

    public void unloadAll()
    {
        for (Plugin plugin : plugins.values())
            plugin.unload();
    }

    public boolean hasPlugin(String identity)
    {
        return plugins.containsKey(identity);
    }

    private final Map<String, Plugin> plugins = new HashMap<>();
}
