/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.util;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

public enum RSTimeUnit implements TemporalUnit
{
    CLIENT_TICKS("Client tick", Duration.ofMillis(20L)),
    GAME_TICKS("Game tick", Duration.ofMillis(600L));

    private final String name;
    private final Duration duration;

    private RSTimeUnit(String name, Duration estimatedDuration) {
        this.name = name;
        this.duration = estimatedDuration;
    }

    @Override
    public boolean isDurationEstimated() {
        return false;
    }

    @Override
    public boolean isDateBased() {
        return false;
    }

    @Override
    public boolean isTimeBased() {
        return true;
    }

    @Override
    public boolean isSupportedBy(Temporal temporal) {
        return temporal.isSupported(this);
    }

    @Override
    public <R extends Temporal> R addTo(R temporal, long amount) {
        return (R)temporal.plus(amount, this);
    }

    @Override
    public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
        return temporal1Inclusive.until(temporal2Exclusive, this);
    }

    @Override
    public String toString() {
        return this.name + " (" + this.duration.toMillis() + "ms)";
    }

    public String getName() {
        return this.name;
    }

    @Override
    public Duration getDuration() {
        return this.duration;
    }
}

