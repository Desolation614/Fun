/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.GameObject
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.tithefarm;

import java.time.Duration;
import java.time.Instant;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.tithefarm.TitheFarmPlantState;
import net.runelite.client.plugins.tithefarm.TitheFarmPlantType;

class TitheFarmPlant {
    private static final Duration PLANT_TIME = Duration.ofMinutes(1L);
    private Instant planted = Instant.now();
    private final TitheFarmPlantState state;
    private final TitheFarmPlantType type;
    private final GameObject gameObject;
    private final WorldPoint worldLocation;

    TitheFarmPlant(TitheFarmPlantState state, TitheFarmPlantType type, GameObject gameObject) {
        this.state = state;
        this.type = type;
        this.gameObject = gameObject;
        this.worldLocation = gameObject.getWorldLocation();
    }

    public double getPlantTimeRelative() {
        Duration duration = Duration.between(this.planted, Instant.now());
        return duration.compareTo(PLANT_TIME) < 0 ? (double)duration.toMillis() / (double)PLANT_TIME.toMillis() : 1.0;
    }

    public Instant getPlanted() {
        return this.planted;
    }

    public void setPlanted(Instant planted) {
        this.planted = planted;
    }

    public TitheFarmPlantState getState() {
        return this.state;
    }

    public TitheFarmPlantType getType() {
        return this.type;
    }

    public GameObject getGameObject() {
        return this.gameObject;
    }

    public WorldPoint getWorldLocation() {
        return this.worldLocation;
    }
}

