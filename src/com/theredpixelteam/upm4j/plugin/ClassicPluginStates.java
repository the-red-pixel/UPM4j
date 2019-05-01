package com.theredpixelteam.upm4j.plugin;

import sun.swing.plaf.windows.ClassicSortArrowIcon;

public class ClassicPluginStates {
    private ClassicPluginStates()
    {
    }

    /**
     * DISCOVERED: When the plugin is once successfully discovered.
     */
    public static final PluginState DISCOVERED = () -> "DISCOVERED";

    /**
     * LOADED: When the plugin successfully passed the loading process
     *   (including class transformation stage).
     */
    public static final PluginState LOADED = () -> "LOADED";

    /**
     * ENABLED: When the plugin successfully passed the enabling process.
     */
    public static final PluginState ENABLED = () -> "ENABLED";

    /**
     * DISABLED: When the plugin successfully passed the disabling process.
     */
    public static final PluginState DISABLED = () -> "DISABLED";

    /**
     * UNLOADED: When the plugin successfully passed the unloading process.
     */
    public static final PluginState UNLOADED = () -> "UNLOADED";

    /**
     * HANG: When exception occurred and unhandled in this plugin.
     */
    public static final PluginState HANG = () -> "HANG";

    public static PluginStateTree getTree()
    {
        return CLASSIC_TREE;
    }

    private static final PluginStateTree CLASSIC_TREE = PluginStateTree.builder()
            .bind(DISCOVERED, LOADED, HANG)
            .bind(LOADED, ENABLED, UNLOADED, HANG)
            .bind(ENABLED, DISABLED, HANG)
            .bind(DISABLED, UNLOADED, ENABLED, HANG)
            .bind(UNLOADED, LOADED, HANG)
            .build();
}
