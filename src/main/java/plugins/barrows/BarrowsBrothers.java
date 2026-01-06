/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.barrows;

import net.runelite.api.coords.WorldPoint;

enum BarrowsBrothers {
    AHRIM("Ahrim", new WorldPoint(3566, 3289, 0), 457),
    DHAROK("Dharok", new WorldPoint(3575, 3298, 0), 458),
    GUTHAN("Guthan", new WorldPoint(3577, 3283, 0), 459),
    KARIL("Karil", new WorldPoint(3566, 3275, 0), 460),
    TORAG("Torag", new WorldPoint(3553, 3283, 0), 461),
    VERAC("Verac", new WorldPoint(3557, 3298, 0), 462);

    private final String name;
    private final WorldPoint location;
    private final int killedVarbit;

    private BarrowsBrothers(String name, WorldPoint location, int killedVarbit) {
        this.name = name;
        this.location = location;
        this.killedVarbit = killedVarbit;
    }

    public String getName() {
        return this.name;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public int getKilledVarbit() {
        return this.killedVarbit;
    }
}

