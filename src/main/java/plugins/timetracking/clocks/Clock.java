/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.clocks;

import java.time.Instant;

abstract class Clock {
    protected String name;
    protected long lastUpdate;
    protected boolean active;

    Clock(String name) {
        this.name = name;
        this.lastUpdate = Instant.now().getEpochSecond();
        this.active = false;
    }

    abstract long getDisplayTime();

    abstract void setDuration(long var1);

    abstract boolean start();

    abstract boolean pause();

    abstract void reset();

    public String getName() {
        return this.name;
    }

    public long getLastUpdate() {
        return this.lastUpdate;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Clock() {
    }

    public Clock(String name, long lastUpdate, boolean active) {
        this.name = name;
        this.lastUpdate = lastUpdate;
        this.active = active;
    }
}

