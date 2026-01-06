/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.mining;

import java.time.Instant;

class MiningSession {
    private Instant lastMined;

    MiningSession() {
    }

    void setLastMined() {
        this.lastMined = Instant.now();
    }

    public Instant getLastMined() {
        return this.lastMined;
    }
}

