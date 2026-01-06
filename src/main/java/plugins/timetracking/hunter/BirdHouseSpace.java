/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.hunter;

enum BirdHouseSpace {
    MEADOW_NORTH("Mushroom Meadow (North)", 1626),
    MEADOW_SOUTH("Mushroom Meadow (South)", 1627),
    VALLEY_NORTH("Verdant Valley (Northeast)", 1628),
    VALLEY_SOUTH("Verdant Valley (Southwest)", 1629);

    private final String name;
    private final int varp;

    private BirdHouseSpace(String name, int varp) {
        this.name = name;
        this.varp = varp;
    }

    public String getName() {
        return this.name;
    }

    public int getVarp() {
        return this.varp;
    }
}

