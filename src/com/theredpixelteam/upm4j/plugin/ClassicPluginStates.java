package com.theredpixelteam.upm4j.plugin;

public class ClassicPluginStates {
    private ClassicPluginStates()
    {
    }

    /**
     * DISCOVERED: When the source is once successfully discovered.
     */
    public static final PluginState DISCOVERED = () -> "DISCOVERED";

    /**
     * LOADED: When the source successfully passed the loading process
     *   (including class transformation stage).
     */
    public static final PluginState LOADED = () -> "LOADED";

    /**
     * ENABLED: When the source successfully passed the enabling process.
     */
    public static final PluginState ENABLED = () -> "ENABLED";

    /**
     * DISABLED: When the source successfully passed the disabling process.
     */
    public static final PluginState DISABLED = () -> "DISABLED";

    /**
     * UNLOADED: When the source successfully passed the unloading process.
     */
    public static final PluginState UNLOADED = () -> "UNLOADED";

    /**
     * HANG: When exception occurred and unhandled in this source.
     */
    public static final PluginState HANG = () -> "HANG";
}
