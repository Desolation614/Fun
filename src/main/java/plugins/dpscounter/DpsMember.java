/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.dpscounter;

import java.time.Duration;
import java.time.Instant;

class DpsMember {
    private final String name;
    private Instant start;
    private Instant end;
    private int damage;

    void addDamage(int amount) {
        if (this.start == null) {
            this.start = Instant.now();
        }
        this.damage += amount;
    }

    float getDps() {
        if (this.start == null) {
            return 0.0f;
        }
        Instant now = this.end == null ? Instant.now() : this.end;
        int diff = (int)(now.toEpochMilli() - this.start.toEpochMilli()) / 1000;
        if (diff == 0) {
            return 0.0f;
        }
        return (float)this.damage / (float)diff;
    }

    void pause() {
        this.end = Instant.now();
    }

    boolean isPaused() {
        return this.start == null || this.end != null;
    }

    void unpause() {
        if (this.end == null) {
            return;
        }
        this.start = this.start.plus(Duration.between(this.end, Instant.now()));
        this.end = null;
    }

    void reset() {
        this.damage = 0;
        this.start = this.end = Instant.now();
    }

    Duration elapsed() {
        return Duration.between(this.start, this.end == null ? Instant.now() : this.end);
    }

    public DpsMember(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Instant getStart() {
        return this.start;
    }

    public Instant getEnd() {
        return this.end;
    }

    public int getDamage() {
        return this.damage;
    }
}

