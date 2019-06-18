package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.UPMContext;
import com.theredpixelteam.upm4j.loader.event.PluginVerificationStageEvent;
import com.theredpixelteam.upm4j.loader.exception.PluginVerifierException;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import java.util.*;

public class PluginVerificationManager {
    public boolean verify(@Nonnull UPMContext context,
                          @Nonnull PluginAttribution attribution)
            throws PluginVerifierException
    {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(attribution, "attribution");

        if (postStageStart(context, attribution))
        {
            postStageCancelled(context, attribution);

            return true;
        }

        boolean rejected = false;
        for (PluginVerifier verifier : verifierMap.values())
        {
            if (postVerificationStart(context, attribution, verifier))
            {
                postVerificationCancelled(context, attribution, verifier);

                continue; // pass on cancellation
            }

            PluginVerifier.Result result;
            try {
                result = verifier.verify(attribution);
            } catch (Exception e) {
                if (!postVerificationFailed(context, attribution, verifier, e))
                    throw new PluginVerifierException(verifier, e);

                postVerificationFailureCancelled(context, attribution, verifier, e);

                continue; // pass on cancellation
            }

            if (result.isPassed())
                postVerificationPassed(context, attribution, verifier, result);
            else
            {
                if (postVerificationRejected(context, attribution, verifier, result))
                {
                    postVerificationRejectionCancelled(context, attribution, verifier, result);

                    continue; // pass on cancellation
                }

                rejected = true;

                break;
            }
        }

        return !rejected;
    }

    public boolean hasVerifier()
    {
        return !verifierMap.isEmpty();
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

    public static boolean postStageStart(@Nonnull UPMContext context,
                                         @Nonnull PluginAttribution attribution)
    {
        PluginVerificationStageEvent.Start event
                = new PluginVerificationStageEvent.Start(context, attribution);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postStageCancelled(@Nonnull UPMContext context,
                                          @Nonnull PluginAttribution attribution)
    {
        context.getEventBus().post(new PluginVerificationStageEvent.Cancelled(context, attribution));
    }

    public static boolean postVerificationStart(@Nonnull UPMContext context,
                                                @Nonnull PluginAttribution attribution,
                                                @Nonnull PluginVerifier verifier)
    {
        PluginVerificationStageEvent.VerificationStart event
                = new PluginVerificationStageEvent.VerificationStart(context, attribution, verifier);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postVerificationCancelled(@Nonnull UPMContext context,
                                                 @Nonnull PluginAttribution attribution,
                                                 @Nonnull PluginVerifier verifier)
    {
        context.getEventBus()
                .post(new PluginVerificationStageEvent.VerificationCancelled(context, attribution, verifier));
    }

    public static void postVerificationPassed(@Nonnull UPMContext context,
                                              @Nonnull PluginAttribution attribution,
                                              @Nonnull PluginVerifier verifier,
                                              @Nonnull PluginVerifier.Result result)
    {
        context.getEventBus()
                .post(new PluginVerificationStageEvent.VerificationPassed(context, attribution, verifier, result));
    }

    public static boolean postVerificationRejected(@Nonnull UPMContext context,
                                                   @Nonnull PluginAttribution attribution,
                                                   @Nonnull PluginVerifier verifier,
                                                   @Nonnull PluginVerifier.Result result)
    {
        PluginVerificationStageEvent.VerificationRejected event
                = new PluginVerificationStageEvent.VerificationRejected(context, attribution, verifier, result);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postVerificationRejectionCancelled(@Nonnull UPMContext context,
                                                          @Nonnull PluginAttribution attribution,
                                                          @Nonnull PluginVerifier verifier,
                                                          @Nonnull PluginVerifier.Result result)
    {
        context.getEventBus()
                .post(new PluginVerificationStageEvent.VerificationRejectionCancelled(
                        context, attribution, verifier, result));
    }

    public static boolean postVerificationFailed(@Nonnull UPMContext context,
                                                 @Nonnull PluginAttribution attribution,
                                                 @Nonnull PluginVerifier verifier,
                                                 @Nonnull Exception cause)
    {
        PluginVerificationStageEvent.VerificationFailed event =
                new PluginVerificationStageEvent.VerificationFailed(context, attribution, verifier, cause);

        context.getEventBus().post(event);

        return event.isCancelled();
    }

    public static void postVerificationFailureCancelled(@Nonnull UPMContext context,
                                                        @Nonnull PluginAttribution attribution,
                                                        @Nonnull PluginVerifier verifier,
                                                        @Nonnull Exception cause)
    {
        context.getEventBus().post(
                new PluginVerificationStageEvent.VerificationFailureCancelled(context, attribution, verifier, cause));
    }

    private final Map<String, PluginVerifier> verifierMap = new HashMap<>();
}
