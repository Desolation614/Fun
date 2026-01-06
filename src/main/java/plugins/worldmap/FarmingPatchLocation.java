/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.worldmap;

import net.runelite.api.coords.WorldPoint;

enum FarmingPatchLocation {
    ALLOTMENT("Allotment", new WorldPoint(3793, 2836, 0), new WorldPoint(1269, 3730, 0)),
    ALLOTMENT_FLOWER("Allotment/Flower", new WorldPoint(3289, 6100, 0)),
    ALLOTMENT_HERB_FLOWER("Allotment/Herb/Flower", new WorldPoint(1729, 3558, 0), new WorldPoint(3598, 3524, 0), new WorldPoint(3052, 3309, 0), new WorldPoint(2810, 3462, 0), new WorldPoint(2663, 3375, 0), new WorldPoint(1587, 3101, 0)),
    ANIMA_HERB("Anima/Herb", new WorldPoint(1235, 3724, 0)),
    BELLADONNA("Belladonna", new WorldPoint(3084, 3356, 0), new WorldPoint(1449, 3354, 0)),
    BUSH("Bush", new WorldPoint(2938, 3223, 0), new WorldPoint(2589, 3862, 0), new WorldPoint(3182, 3356, 0), new WorldPoint(2615, 3224, 0)),
    BUSH_FLOWER("Bush/Flower", new WorldPoint(1259, 3729, 0)),
    CACTUS("Cactus", new WorldPoint(3313, 3201, 0), new WorldPoint(1264, 3745, 0)),
    CALQUAT("Calquat", new WorldPoint(2793, 3099, 0), new WorldPoint(1365, 3035, 0)),
    CELASTRUS_FRUIT_TREE("Celastrus/Fruit Tree", new WorldPoint(1242, 3755, 0)),
    CRYSTAL_TREE("Crystal Tree", new WorldPoint(3292, 6120, 0)),
    FLOWER("Flower", new WorldPoint(1352, 3025, 0)),
    FRUIT_TREE("Fruit Tree", new WorldPoint(2487, 3181, 0), new WorldPoint(2343, 3160, 0), new WorldPoint(2472, 3445, 0), new WorldPoint(2858, 3432, 0), new WorldPoint(2765, 3211, 0), new WorldPoint(1347, 3058, 0)),
    GRAPES("Grapes", new WorldPoint(1807, 3555, 0)),
    HARDWOOD("Hardwood", new WorldPoint(3707, 3838, 0), new WorldPoint(1683, 2971, 0)),
    HERB("Herb", new WorldPoint(3789, 2840, 0), new WorldPoint(2847, 3933, 0), new WorldPoint(2828, 3696, 0)),
    HESPORI("Hespori", new WorldPoint(1182, 10068, 0)),
    HOPS("Hops", new WorldPoint(2572, 3102, 0), new WorldPoint(2661, 3523, 0), new WorldPoint(3224, 3313, 0), new WorldPoint(2812, 3334, 0), new WorldPoint(1367, 2938, 0)),
    MUSHROOM("Mushroom", new WorldPoint(3449, 3471, 0)),
    REDWOOD("Redwood", new WorldPoint(1233, 3754, 0)),
    SEAWEED("Seaweed", new WorldPoint(3730, 10271, 0)),
    SPIRIT_TREE("Spirit Tree", new WorldPoint(3056, 3259, 0), new WorldPoint(1690, 3540, 0), new WorldPoint(3614, 3856, 0), new WorldPoint(2799, 3205, 0), new WorldPoint(1254, 3753, 0)),
    TREE("Tree", new WorldPoint(3226, 3457, 0), new WorldPoint(2933, 3436, 0), new WorldPoint(3189, 3233, 0), new WorldPoint(2434, 3418, 0), new WorldPoint(3005, 3375, 0), new WorldPoint(1234, 3736, 0), new WorldPoint(1366, 3318, 0));

    private final String tooltip;
    private final WorldPoint[] locations;

    private FarmingPatchLocation(String description, WorldPoint ... locations) {
        this.tooltip = "Farming patch - " + description;
        this.locations = locations;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public WorldPoint[] getLocations() {
        return this.locations;
    }
}

