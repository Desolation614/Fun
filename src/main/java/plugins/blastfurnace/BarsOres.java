/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.blastfurnace;

enum BarsOres {
    COPPER_ORE(959, 436),
    TIN_ORE(950, 438),
    IRON_ORE(951, 440),
    COAL(949, 453),
    MITHRIL_ORE(952, 447),
    ADAMANTITE_ORE(953, 449),
    RUNITE_ORE(954, 451),
    SILVER_ORE(956, 442),
    GOLD_ORE(955, 444),
    BRONZE_BAR(941, 2349),
    IRON_BAR(942, 2351),
    STEEL_BAR(943, 2353),
    MITHRIL_BAR(944, 2359),
    ADAMANTITE_BAR(945, 2361),
    RUNITE_BAR(946, 2363),
    SILVER_BAR(948, 2355),
    GOLD_BAR(947, 2357);

    private final int varbit;
    private final int itemID;

    private BarsOres(int varbit, int itemID) {
        this.varbit = varbit;
        this.itemID = itemID;
    }

    public int getItemID() {
        return this.itemID;
    }

    public int getVarbit() {
        return this.varbit;
    }
}

