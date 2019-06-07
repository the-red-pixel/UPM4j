package com.theredpixelteam.upm4j.loader;

import com.theredpixelteam.redtea.util.Optional;
import com.theredpixelteam.upm4j.plugin.PluginAttribution;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface PluginVerifier {
    public @Nonnull String getIdentity();

    public @Nonnull String getName();

    public @Nonnull Optional<String> getDescription();

    public Result verify(@Nonnull PluginAttribution attribution);

    public static class Result
    {
        Result(boolean passed,
               @Nullable String message,
               @Nullable Throwable throwable)
        {
            this.passed = passed;
            this.message = message;
            this.throwable = throwable;
        }

        public boolean isPassed()
        {
            return passed;
        }

        public @Nonnull Optional<String> getMessage()
        {
            return Optional.ofNullable(message);
        }

        public @Nonnull Optional<Throwable> getThrowable()
        {
            return Optional.ofNullable(throwable);
        }

        public static Builder builder()
        {
            return new Builder();
        }

        public static Result passed()
        {
            return builder().passed(true).build();
        }

        public static Result passed(@Nonnull String message)
        {
            return builder().passed(true).message(Objects.requireNonNull(message)).build();
        }

        public static Result rejected()
        {
            return builder()/*.passed(false)*/.build();
        }

        public static Result rejected(@Nonnull String message)
        {
            return builder()/*.passed(false)*/.message(Objects.requireNonNull(message)).build();
        }

        public static Result rejected(@Nonnull Throwable throwable)
        {
            return builder()/*.passed(false)*/.throwable(Objects.requireNonNull(throwable)).build();
        }

        public static Result rejected(@Nonnull String message,
                                      @Nonnull Throwable throwable)
        {
            return builder()/*.passed(false)*/
                    .message(Objects.requireNonNull(message, "message"))
                    .throwable(Objects.requireNonNull(throwable, "throwable"))
                    .build();
        }

        private final String message;

        private final Throwable throwable;

        private final boolean passed;

        public static class Builder
        {
            Builder()
            {
            }

            public @Nonnull Builder passed(boolean passed)
            {
                this.passed = passed;
                return this;
            }

            public @Nonnull Builder throwable(Throwable throwable)
            {
                this.throwable = throwable;
                return this;
            }

            public @Nonnull Builder message(String message)
            {
                this.message = message;
                return this;
            }

            public @Nullable Throwable getThrowable()
            {
                return throwable;
            }

            public @Nullable String getMessage()
            {
                return message;
            }

            public boolean isPassed()
            {
                return passed;
            }

            public Result build()
            {
                return new Result(passed, message, throwable);
            }

            private String message;

            private Throwable throwable;

            private boolean passed;
        }
    }
}
