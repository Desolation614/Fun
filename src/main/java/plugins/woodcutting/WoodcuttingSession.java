/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.woodcutting;

import java.time.Duration;
import java.time.Instant;

class WoodcuttingSession {
    private final Instant start = Instant.now();
    private Instant lastChopping;
    private int logsCut;
    private int logsPerHr;
    private int bark;
    private int barkPerHr;
    private boolean active = true;

    WoodcuttingSession() {
    }

    void setLastChopping() {
        this.lastChopping = Instant.now();
        this.setActive(true);
    }

    void incrementLogsCut() {
        ++this.logsCut;
        Duration elapsed = Duration.between(this.start, Instant.now());
        long elapsedMs = elapsed.toMillis();
        if (this.logsCut >= 3 && elapsedMs > 0L) {
            this.logsPerHr = (int)((double)this.logsCut * (double)Duration.ofHours(1L).toMillis() / (double)elapsedMs);
        }
    }

    void incrementBark(int num) {
        this.bark += num;
        Duration elapsed = Duration.between(this.start, Instant.now());
        long elapsedMs = elapsed.toMillis();
        if (elapsedMs > 0L) {
            this.barkPerHr = (int)((double)this.bark * (double)Duration.ofHours(1L).toMillis() / (double)elapsedMs);
        }
    }

    Instant getLastChopping() {
        return this.lastChopping;
    }

    int getLogsCut() {
        return this.logsCut;
    }

    int getLogsPerHr() {
        return this.logsPerHr;
    }

    int getBark() {
        return this.bark;
    }

    int getBarkPerHr() {
        return this.barkPerHr;
    }

    boolean isActive() {
        return this.active;
    }

    void setActive(boolean active) {
        this.active = active;
    }
}

