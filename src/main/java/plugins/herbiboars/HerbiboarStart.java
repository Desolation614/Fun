/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.herbiboars;

import net.runelite.api.coords.WorldPoint;

enum HerbiboarStart {
    MIDDLE(new WorldPoint(3686, 3870, 0)),
    LEPRECHAUN(new WorldPoint(3705, 3830, 0)),
    CAMP_ENTRANCE(new WorldPoint(3704, 3810, 0)),
    GHOST_MUSHROOM(new WorldPoint(3695, 3800, 0)),
    DRIFTWOOD(new WorldPoint(3751, 3850, 0));

    private final WorldPoint location;

    static HerbiboarStart from(WorldPoint location) {
        for (HerbiboarStart start : HerbiboarStart.values()) {
            if (!start.getLocation().equals((Object)location)) continue;
            return start;
        }
        return null;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    private HerbiboarStart(WorldPoint location) {
        this.location = location;
    }
}

