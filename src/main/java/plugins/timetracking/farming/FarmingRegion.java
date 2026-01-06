/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.timetracking.farming;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.timetracking.farming.FarmingPatch;

public class FarmingRegion {
    private final String name;
    private final int regionID;
    private final boolean definite;
    private final FarmingPatch[] patches;

    FarmingRegion(String name, int regionID, boolean definite, FarmingPatch ... patches) {
        this.name = name;
        this.regionID = regionID;
        this.definite = definite;
        this.patches = patches;
        for (FarmingPatch p : patches) {
            p.setRegion(this);
        }
    }

    public boolean isInBounds(WorldPoint loc) {
        return true;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public int getRegionID() {
        return this.regionID;
    }

    public boolean isDefinite() {
        return this.definite;
    }

    public FarmingPatch[] getPatches() {
        return this.patches;
    }
}

