package com.theredpixelteam.upm4j.loader.attribution.processor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Barrier {
    Barrier()
    {
        this(Integer.MAX_VALUE);
    }

    Barrier(int maxCount)
    {
        this.maxCount = maxCount;
    }

    public static Barrier barrier()
    {
        return new Barrier();
    }

    public static Barrier barrier(int maxCount)
    {
        return new Barrier(maxCount);
    }

    public synchronized boolean count()
    {
        if (blocked)
            return false;

        count++;
        checkCount();

        return true;
    }

    void checkCount()
    {
        if (count == maxCount)
            block();
    }

    public void clear()
    {
        count = 0;
        blocked = false;
    }

    public boolean isBlocked()
    {
        return blocked;
    }

    public void block()
    {
        blocked = true;
    }

    public @Nonnull
    Optional<Object> putAttachment(@Nonnull Object key,
                                   @Nonnull Object attachment)
    {
        return Optional.ofNullable(attachments.put(
                Objects.requireNonNull(key, "key"),
                Objects.requireNonNull(attachment, "attachment")));
    }

    public boolean hasAttachment(@Nonnull Object key)
    {
        return getAttachment(key).isPresent();
    }

    public @Nonnull Optional<Object> getAttachment(@Nonnull Object key)
    {
        return Optional.ofNullable(attachments.get(Objects.requireNonNull(key)));
    }

    private int count;

    private final int maxCount;

    private volatile boolean blocked;

    private final Map<Object, Object> attachments = new HashMap<>();
}
