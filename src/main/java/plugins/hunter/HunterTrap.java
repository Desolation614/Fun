/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.GameObject
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.hunter;

import java.time.Duration;
import java.time.Instant;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

class HunterTrap {
    static final Duration TRAP_TIME = Duration.ofMinutes(1L);
    private Instant placedOn;
    private State state = State.OPEN;
    private final int objectId;
    private final WorldPoint worldLocation;

    HunterTrap(GameObject gameObject) {
        this.placedOn = Instant.now();
        this.objectId = gameObject.getId();
        this.worldLocation = gameObject.getWorldLocation();
    }

    public double getTrapTimeRelative() {
        Duration duration = Duration.between(this.placedOn, Instant.now());
        return duration.compareTo(TRAP_TIME) < 0 ? (double)duration.toMillis() / (double)TRAP_TIME.toMillis() : 1.0;
    }

    public void resetTimer() {
        this.placedOn = Instant.now();
    }

    public Instant getPlacedOn() {
        return this.placedOn;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getObjectId() {
        return this.objectId;
    }

    public WorldPoint getWorldLocation() {
        return this.worldLocation;
    }

    static enum State {
        OPEN,
        EMPTY,
        FULL,
        TRANSITION;

    }
}

