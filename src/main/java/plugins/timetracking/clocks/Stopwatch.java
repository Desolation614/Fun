/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.clocks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import net.runelite.client.plugins.timetracking.clocks.Clock;

class Stopwatch
extends Clock {
    private long elapsed = 0L;
    private List<Long> laps = new ArrayList<Long>();

    Stopwatch(String name) {
        super(name);
    }

    @Override
    long getDisplayTime() {
        if (!this.active) {
            return this.elapsed;
        }
        return Math.max(0L, this.elapsed + (Instant.now().getEpochSecond() - this.lastUpdate));
    }

    @Override
    void setDuration(long duration) {
        this.elapsed = duration;
    }

    @Override
    boolean start() {
        if (!this.active) {
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
            this.elapsed = Math.max(0L, this.elapsed + (Instant.now().getEpochSecond() - this.lastUpdate));
            this.lastUpdate = Instant.now().getEpochSecond();
            return true;
        }
        return false;
    }

    void lap() {
        this.laps.add(this.getDisplayTime());
    }

    @Override
    void reset() {
        this.active = false;
        this.elapsed = 0L;
        this.laps.clear();
        this.lastUpdate = Instant.now().getEpochSecond();
    }

    public long getElapsed() {
        return this.elapsed;
    }

    public List<Long> getLaps() {
        return this.laps;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public void setLaps(List<Long> laps) {
        this.laps = laps;
    }

    public Stopwatch(long elapsed, List<Long> laps) {
        this.elapsed = elapsed;
        this.laps = laps;
    }
}

