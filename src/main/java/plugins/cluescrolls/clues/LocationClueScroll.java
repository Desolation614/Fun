/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls.clues;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;

public interface LocationClueScroll {
    public WorldPoint getLocation(ClueScrollPlugin var1);

    default public WorldPoint[] getLocations(ClueScrollPlugin plugin) {
        WorldPoint[] worldPointArray;
        WorldPoint location = this.getLocation(plugin);
        if (location == null) {
            worldPointArray = new WorldPoint[]{};
        } else {
            WorldPoint[] worldPointArray2 = new WorldPoint[1];
            worldPointArray = worldPointArray2;
            worldPointArray2[0] = location;
        }
        return worldPointArray;
    }
}

