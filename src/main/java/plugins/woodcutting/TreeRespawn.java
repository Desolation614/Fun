/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.woodcutting;

import java.time.Instant;
import net.runelite.api.coords.WorldPoint;

class TreeRespawn {
    private final WorldPoint worldLocation;
    private final int lenX;
    private final int lenY;
    private final Instant startTime;
    private final int respawnTime;

    boolean isExpired() {
        return Instant.now().isAfter(this.startTime.plusMillis(this.respawnTime));
    }

    public TreeRespawn(WorldPoint worldLocation, int lenX, int lenY, Instant startTime, int respawnTime) {
        this.worldLocation = worldLocation;
        this.lenX = lenX;
        this.lenY = lenY;
        this.startTime = startTime;
        this.respawnTime = respawnTime;
    }

    public WorldPoint getWorldLocation() {
        return this.worldLocation;
    }

    public int getLenX() {
        return this.lenX;
    }

    public int getLenY() {
        return this.lenY;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public int getRespawnTime() {
        return this.respawnTime;
    }
}

