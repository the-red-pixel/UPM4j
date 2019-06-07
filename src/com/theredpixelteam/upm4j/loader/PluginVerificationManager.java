package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.util.*;

public class PluginVerificationManager {
    // TODO

    public boolean verify(PluginAttribution attribution)
    {

    }

    public boolean addVerifier(@Nonnull PluginVerifier verifier)
    {
        return verifierMap.put(verifier.getIdentity(), verifier) != null;
    }

    public boolean removeVerifier(@Nonnull String identity)
    {
        return verifierMap.remove(Objects.requireNonNull(identity)) != null;
    }

    public @Nonnull Optional<PluginVerifier> getVerifier(@Nonnull String identity)
    {
        return Optional.ofNullable(verifierMap.get(Objects.requireNonNull(identity)));
    }

    public @Nonnull Collection<PluginVerifier> getAllVerifiers()
    {
        return Collections.unmodifiableCollection(verifierMap.values());
    }

    private final Map<String, PluginVerifier> verifierMap = new HashMap<>();
}
