/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.GameObject
 */
package net.runelite.client.plugins.blastmine;

import java.time.Duration;
import java.time.Instant;
import net.runelite.api.GameObject;
import net.runelite.client.plugins.blastmine.BlastMineRockType;

class BlastMineRock {
    private static final Duration PLANT_TIME = Duration.ofSeconds(30L);
    private static final Duration FUSE_TIME = Duration.ofMillis(4200L);
    private final GameObject gameObject;
    private final BlastMineRockType type;
    private final Instant creationTime = Instant.now();

    BlastMineRock(GameObject gameObject, BlastMineRockType blastMineRockType) {
        this.gameObject = gameObject;
        this.type = blastMineRockType;
    }

    double getRemainingFuseTimeRelative() {
        Duration duration = Duration.between(this.creationTime, Instant.now());
        return duration.compareTo(FUSE_TIME) < 0 ? (double)duration.toMillis() / (double)FUSE_TIME.toMillis() : 1.0;
    }

    double getRemainingTimeRelative() {
        Duration duration = Duration.between(this.creationTime, Instant.now());
        return duration.compareTo(PLANT_TIME) < 0 ? (double)duration.toMillis() / (double)PLANT_TIME.toMillis() : 1.0;
    }

    public GameObject getGameObject() {
        return this.gameObject;
    }

    public BlastMineRockType getType() {
        return this.type;
    }
}

