/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.smelting;

import java.time.Instant;

class SmeltingSession {
    private int barsSmelted;
    private int cannonBallsSmelted;
    private Instant lastItemSmelted;

    SmeltingSession() {
    }

    void increaseBarsSmelted() {
        ++this.barsSmelted;
        this.lastItemSmelted = Instant.now();
    }

    void increaseCannonBallsSmelted(int amount) {
        this.cannonBallsSmelted += amount;
        this.lastItemSmelted = Instant.now();
    }

    int getBarsSmelted() {
        return this.barsSmelted;
    }

    int getCannonBallsSmelted() {
        return this.cannonBallsSmelted;
    }

    Instant getLastItemSmelted() {
        return this.lastItemSmelted;
    }
}

