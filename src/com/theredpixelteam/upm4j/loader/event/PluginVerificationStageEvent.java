package com.theredpixelteam.upm4j.loader.event;

import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.event.UPMEvent;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PluginVerificationStageEvent implements UPMEvent {
    protected PluginVerificationStageEvent(@Nonnull UPMContext context,
                                           @Nonnull PluginAttribution plugin)
    {
        this.context = Objects.requireNonNull(context, "context");
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public @Nonnull UPMContext getContext()
    {
        return context;
    }

    public @Nonnull PluginAttribution getPlugin()
    {
        return plugin;
    }

    private final UPMContext context;

    private final PluginAttribution plugin;

}
