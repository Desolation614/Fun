/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking;

public enum Tab {
    OVERVIEW("Overview", 22051),
    CLOCK("Timers & Stopwatches", 2575),
    BIRD_HOUSE("Bird Houses", 21515),
    ALLOTMENT("Allotment Patches", 1965),
    FLOWER("Flower Patches", 2462),
    HERB("Herb Patches", 207),
    TREE("Tree Patches", 1515),
    FRUIT_TREE("Fruit Tree Patches", 2114),
    HOPS("Hops Patches", 6006),
    BUSH("Bush Patches", 6018),
    GRAPE("Grape Patches", 1987),
    SPECIAL("Special Patches", 6004),
    TIME_OFFSET("Farming Tick Offset", 5331);

    public static final Tab[] FARMING_TABS;
    private final String name;
    private final int itemID;

    private Tab(String name, int itemID) {
        this.name = name;
        this.itemID = itemID;
    }

    public String getName() {
        return this.name;
    }

    public int getItemID() {
        return this.itemID;
    }

    static {
        FARMING_TABS = new Tab[]{HERB, TREE, FRUIT_TREE, SPECIAL, FLOWER, ALLOTMENT, BUSH, GRAPE, HOPS};
    }
}

