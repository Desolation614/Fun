/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.mining;

import java.time.Instant;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mining.Rock;

class RockRespawn {
    private final Rock rock;
    private final WorldPoint worldPoint;
    private final Instant startTime;
    private final int respawnTime;
    private final int zOffset;

    boolean isExpired() {
        return Instant.now().isAfter(this.startTime.plusMillis(this.respawnTime));
    }

    public RockRespawn(Rock rock, WorldPoint worldPoint, Instant startTime, int respawnTime, int zOffset) {
        this.rock = rock;
        this.worldPoint = worldPoint;
        this.startTime = startTime;
        this.respawnTime = respawnTime;
        this.zOffset = zOffset;
    }

    public Rock getRock() {
        return this.rock;
    }

    public WorldPoint getWorldPoint() {
        return this.worldPoint;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public int getRespawnTime() {
        return this.respawnTime;
    }

    public int getZOffset() {
        return this.zOffset;
    }
}

