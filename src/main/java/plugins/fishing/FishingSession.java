/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.fishing;

import java.time.Instant;

class FishingSession {
    private Instant lastFishCaught;

    FishingSession() {
    }

    public Instant getLastFishCaught() {
        return this.lastFishCaught;
    }

    public void setLastFishCaught(Instant lastFishCaught) {
        this.lastFishCaught = lastFishCaught;
    }
}

