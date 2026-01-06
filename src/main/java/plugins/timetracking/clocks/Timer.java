/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.clocks;

import java.time.Instant;
import net.runelite.client.plugins.timetracking.clocks.Clock;

class Timer
extends Clock {
    private long duration;
    private long remaining;
    private transient boolean warning;
    private boolean loop;

    Timer(String name, long duration) {
        super(name);
        this.duration = duration;
        this.remaining = duration;
        this.warning = false;
    }

    @Override
    long getDisplayTime() {
        if (!this.active) {
            return this.remaining;
        }
        return Math.max(0L, this.remaining - (Instant.now().getEpochSecond() - this.lastUpdate));
    }

    @Override
    boolean start() {
        if (!this.active && this.duration > 0L) {
            if (this.remaining <= 0L) {
                this.remaining = this.duration;
                this.warning = false;
            }
            this.lastUpdate = Instant.now().getEpochSecond();
            this.active = true;
            return true;
        }
        return false;
    }

    @Override
    boolean pause() {
        if (this.active) {
            this.active = false;
            this.remaining = Math.max(0L, this.remaining - (Instant.now().getEpochSecond() - this.lastUpdate));
            this.lastUpdate = Instant.now().getEpochSecond();
            return true;
        }
        return false;
    }

    @Override
    void reset() {
        this.active = false;
        this.remaining = this.duration;
        this.lastUpdate = Instant.now().getEpochSecond();
    }

    boolean isWarning() {
        return this.warning && this.remaining > 0L;
    }

    public long getDuration() {
        return this.duration;
    }

    public long getRemaining() {
        return this.remaining;
    }

    public boolean isLoop() {
        return this.loop;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public Timer(long duration, long remaining, boolean warning, boolean loop) {
        this.duration = duration;
        this.remaining = remaining;
        this.warning = warning;
        this.loop = loop;
    }
}

