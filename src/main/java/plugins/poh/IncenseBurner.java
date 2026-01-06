/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.poh;

import java.time.Instant;

class IncenseBurner {
    private Instant start;
    private boolean lit;
    private double countdownTimer;
    private double randomTimer;
    private Instant end;

    IncenseBurner() {
    }

    void reset() {
        this.countdownTimer = 0.0;
        this.randomTimer = 0.0;
    }

    public Instant getStart() {
        return this.start;
    }

    public boolean isLit() {
        return this.lit;
    }

    public double getCountdownTimer() {
        return this.countdownTimer;
    }

    public double getRandomTimer() {
        return this.randomTimer;
    }

    public Instant getEnd() {
        return this.end;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
    }

    public void setCountdownTimer(double countdownTimer) {
        this.countdownTimer = countdownTimer;
    }

    public void setRandomTimer(double randomTimer) {
        this.randomTimer = randomTimer;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }
}

