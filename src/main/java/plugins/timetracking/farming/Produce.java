/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.timetracking.farming;

import javax.annotation.Nullable;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;

public enum Produce {
    WEEDS("Weeds", 6055, 5, 4),
    SCARECROW("Scarecrow", 6059, 5, 4),
    POTATO("Potato", "Potatoes", PatchImplementation.ALLOTMENT, 1942, 10, 5, 0, 3),
    ONION("Onion", "Onions", PatchImplementation.ALLOTMENT, 1957, 10, 5, 0, 3),
    CABBAGE("Cabbage", "Cabbages", PatchImplementation.ALLOTMENT, 1965, 10, 5, 0, 3),
    TOMATO("Tomato", "Tomatoes", PatchImplementation.ALLOTMENT, 1982, 10, 5, 0, 3),
    SWEETCORN("Sweetcorn", PatchImplementation.ALLOTMENT, 5986, 10, 7, 0, 3),
    STRAWBERRY("Strawberry", "Strawberries", PatchImplementation.ALLOTMENT, 5504, 10, 7, 0, 3),
    WATERMELON("Watermelon", "Watermelons", PatchImplementation.ALLOTMENT, 5982, 10, 9, 0, 3),
    SNAPE_GRASS("Snape grass", PatchImplementation.ALLOTMENT, 231, 10, 8, 0, 3),
    MARIGOLD("Marigold", "Marigolds", PatchImplementation.FLOWER, 6010, 5, 5),
    ROSEMARY("Rosemary", PatchImplementation.FLOWER, 6014, 5, 5),
    NASTURTIUM("Nasturtium", "Nasturtiums", PatchImplementation.FLOWER, 6012, 5, 5),
    WOAD("Woad", PatchImplementation.FLOWER, 1793, 5, 5),
    LIMPWURT("Limpwurt", "Limpwurt roots", PatchImplementation.FLOWER, 225, 5, 5),
    WHITE_LILY("White lily", "White lillies", PatchImplementation.FLOWER, 22932, 5, 5),
    REDBERRIES("Redberry", "Redberries", PatchImplementation.BUSH, 1951, 20, 6, 20, 5),
    CADAVABERRIES("Cadavaberry", "Cadava berries", PatchImplementation.BUSH, 753, 20, 7, 20, 5),
    DWELLBERRIES("Dwellberry", "Dwellberries", PatchImplementation.BUSH, 2126, 20, 8, 20, 5),
    JANGERBERRIES("Jangerberry", "Jangerberries", PatchImplementation.BUSH, 247, 20, 9, 20, 5),
    WHITEBERRIES("Whiteberry", "White berries", PatchImplementation.BUSH, 239, 20, 9, 20, 5),
    POISON_IVY("Poison ivy", "Poison ivy berries", PatchImplementation.BUSH, 6018, 20, 9, 20, 5),
    BARLEY("Barley", PatchImplementation.HOPS, 6006, 10, 5, 0, 3),
    HAMMERSTONE("Hammerstone", PatchImplementation.HOPS, 5994, 10, 5, 0, 3),
    ASGARNIAN("Asgarnian", PatchImplementation.HOPS, 5996, 10, 6, 0, 3),
    JUTE("Jute", PatchImplementation.HOPS, 5931, 10, 6, 0, 3),
    YANILLIAN("Yanillian", PatchImplementation.HOPS, 5998, 10, 7, 0, 3),
    KRANDORIAN("Krandorian", PatchImplementation.HOPS, 6000, 10, 8, 0, 3),
    WILDBLOOD("Wildblood", PatchImplementation.HOPS, 6002, 10, 9, 0, 3),
    GUAM("Guam", PatchImplementation.HERB, 249, 20, 5, 0, 3),
    MARRENTILL("Marrentill", PatchImplementation.HERB, 251, 20, 5, 0, 3),
    TARROMIN("Tarromin", PatchImplementation.HERB, 253, 20, 5, 0, 3),
    HARRALANDER("Harralander", PatchImplementation.HERB, 255, 20, 5, 0, 3),
    RANARR("Ranarr", PatchImplementation.HERB, 257, 20, 5, 0, 3),
    TOADFLAX("Toadflax", PatchImplementation.HERB, 2998, 20, 5, 0, 3),
    IRIT("Irit", PatchImplementation.HERB, 259, 20, 5, 0, 3),
    AVANTOE("Avantoe", PatchImplementation.HERB, 261, 20, 5, 0, 3),
    KWUARM("Kwuarm", PatchImplementation.HERB, 263, 20, 5, 0, 3),
    HUASCA("Huasca", PatchImplementation.HERB, 30097, 20, 5, 0, 3),
    SNAPDRAGON("Snapdragon", PatchImplementation.HERB, 3000, 20, 5, 0, 3),
    CADANTINE("Cadantine", PatchImplementation.HERB, 265, 20, 5, 0, 3),
    LANTADYME("Lantadyme", PatchImplementation.HERB, 2481, 20, 5, 0, 3),
    DWARF_WEED("Dwarf weed", PatchImplementation.HERB, 267, 20, 5, 0, 3),
    TORSTOL("Torstol", PatchImplementation.HERB, 269, 20, 5, 0, 3),
    GOUTWEED("Goutweed", PatchImplementation.HERB, 3261, 20, 5, 0, 2),
    ANYHERB("Any herb", PatchImplementation.HERB, 249, 20, 5, 0, 3),
    OAK("Oak", "Oak tree", PatchImplementation.TREE, 1521, 40, 5),
    WILLOW("Willow", "Willow tree", PatchImplementation.TREE, 1519, 40, 7),
    MAPLE("Maple", "Maple tree", PatchImplementation.TREE, 1517, 40, 9),
    YEW("Yew", "Yew tree", PatchImplementation.TREE, 1515, 40, 11),
    MAGIC("Magic", "Magic tree", PatchImplementation.TREE, 1513, 40, 13),
    APPLE("Apple", "Apple tree", PatchImplementation.FRUIT_TREE, 1955, 160, 7, 45, 7),
    BANANA("Banana", "Banana tree", PatchImplementation.FRUIT_TREE, 1963, 160, 7, 45, 7),
    ORANGE("Orange", "Orange tree", PatchImplementation.FRUIT_TREE, 2108, 160, 7, 45, 7),
    CURRY("Curry", "Curry tree", PatchImplementation.FRUIT_TREE, 5970, 160, 7, 45, 7),
    PINEAPPLE("Pineapple", "Pineapple plant", PatchImplementation.FRUIT_TREE, 2114, 160, 7, 45, 7),
    PAPAYA("Papaya", "Papaya tree", PatchImplementation.FRUIT_TREE, 5972, 160, 7, 45, 7),
    PALM("Palm", "Palm tree", PatchImplementation.FRUIT_TREE, 5974, 160, 7, 45, 7),
    DRAGONFRUIT("Dragonfruit", "Dragonfruit tree", PatchImplementation.FRUIT_TREE, 22929, 160, 7, 45, 7),
    CACTUS("Cactus", PatchImplementation.CACTUS, 6016, 80, 8, 20, 4),
    POTATO_CACTUS("Potato cactus", "Potato cacti", PatchImplementation.CACTUS, 3138, 10, 8, 5, 7),
    TEAK("Teak", PatchImplementation.HARDWOOD_TREE, 6333, 640, 8),
    MAHOGANY("Mahogany", PatchImplementation.HARDWOOD_TREE, 6332, 640, 9),
    ATTAS("Attas", PatchImplementation.ANIMA, 22940, 640, 9),
    IASOR("Iasor", PatchImplementation.ANIMA, 22939, 640, 9),
    KRONOS("Kronos", PatchImplementation.ANIMA, 22938, 640, 9),
    SEAWEED("Seaweed", PatchImplementation.SEAWEED, 21504, 10, 5, 0, 4),
    GRAPE("Grape", PatchImplementation.GRAPES, 1987, 5, 8, 0, 5),
    MUSHROOM("Mushroom", PatchImplementation.MUSHROOM, 6004, 40, 7, 0, 7),
    BELLADONNA("Belladonna", PatchImplementation.BELLADONNA, 2398, 80, 5),
    CALQUAT("Calquat", PatchImplementation.CALQUAT, 5980, 160, 9, 0, 7),
    SPIRIT_TREE("Spirit tree", PatchImplementation.SPIRIT_TREE, 6063, 320, 13),
    CELASTRUS("Celastrus", "Celastrus tree", PatchImplementation.CELASTRUS, 1391, 160, 6, 0, 4),
    REDWOOD("Redwood", "Redwood tree", PatchImplementation.REDWOOD, 19669, 640, 11),
    HESPORI("Hespori", PatchImplementation.HESPORI, 23044, 640, 4, 0, 2),
    CRYSTAL_TREE("Crystal tree", PatchImplementation.CRYSTAL_TREE, 23866, 80, 7),
    EMPTY_COMPOST_BIN("Compost Bin", PatchImplementation.COMPOST, 3271, 0, 1, 0, 0),
    COMPOST("Compost", PatchImplementation.COMPOST, 6032, 40, 3, 0, 15),
    SUPERCOMPOST("Supercompost", PatchImplementation.COMPOST, 6034, 40, 3, 0, 15),
    ULTRACOMPOST("Ultracompost", PatchImplementation.COMPOST, 21483, 0, 3, 0, 15),
    ROTTEN_TOMATO("Rotten Tomato", PatchImplementation.COMPOST, 2518, 40, 3, 0, 15),
    EMPTY_BIG_COMPOST_BIN("Big Compost Bin", PatchImplementation.COMPOST, 3271, 0, 1, 0, 0),
    BIG_COMPOST("Compost", PatchImplementation.BIG_COMPOST, 6032, 40, 3, 0, 30),
    BIG_SUPERCOMPOST("Supercompost", PatchImplementation.BIG_COMPOST, 6034, 40, 3, 0, 30),
    BIG_ULTRACOMPOST("Ultracompost", PatchImplementation.BIG_COMPOST, 21483, 0, 3, 0, 30),
    BIG_ROTTEN_TOMATO("Rotten Tomato", PatchImplementation.BIG_COMPOST, 2518, 40, 3, 0, 30);

    private final String name;
    private final String contractName;
    private final PatchImplementation patchImplementation;
    private final int itemID;
    private final int tickrate;
    private final int stages;
    private final int regrowTickrate;
    private final int harvestStages;

    private Produce(String name, int itemID, int tickrate, int stages, int regrowTickrate, int harvestStages) {
        this(name, name, null, itemID, tickrate, stages, regrowTickrate, harvestStages);
    }

    private Produce(String name, PatchImplementation patchImplementation, int itemID, int tickrate, int stages, int regrowTickrate, int harvestStages) {
        this(name, name, patchImplementation, itemID, tickrate, stages, regrowTickrate, harvestStages);
    }

    private Produce(String name, String contractName, PatchImplementation patchImplementation, int itemID, int tickrate, int stages) {
        this(name, contractName, patchImplementation, itemID, tickrate, stages, 0, 1);
    }

    private Produce(String name, PatchImplementation patchImplementation, int itemID, int tickrate, int stages) {
        this(name, name, patchImplementation, itemID, tickrate, stages, 0, 1);
    }

    private Produce(String name, int itemID, int tickrate, int stages) {
        this(name, name, null, itemID, tickrate, stages, 0, 1);
    }

    public int getTickrate() {
        return (int)Math.ceil((double)this.tickrate / 2.0);
    }

    public int getRegrowTickrate() {
        return (int)Math.ceil((double)this.regrowTickrate / 2.0);
    }

    @Nullable
    static Produce getByItemID(int itemId) {
        for (Produce produce : Produce.values()) {
            if (produce.getItemID() != itemId) continue;
            return produce;
        }
        return null;
    }

    @Nullable
    static Produce getByContractName(String contractName) {
        for (Produce produce : Produce.values()) {
            if (!produce.getContractName().equalsIgnoreCase(contractName)) continue;
            return produce;
        }
        return null;
    }

    private Produce(String name, String contractName, PatchImplementation patchImplementation, int itemID, int tickrate, int stages, int regrowTickrate, int harvestStages) {
        this.name = name;
        this.contractName = contractName;
        this.patchImplementation = patchImplementation;
        this.itemID = itemID;
        this.tickrate = tickrate;
        this.stages = stages;
        this.regrowTickrate = regrowTickrate;
        this.harvestStages = harvestStages;
    }

    public String getName() {
        return this.name;
    }

    public String getContractName() {
        return this.contractName;
    }

    public PatchImplementation getPatchImplementation() {
        return this.patchImplementation;
    }

    public int getItemID() {
        return this.itemID;
    }

    public int getStages() {
        return this.stages;
    }

    public int getHarvestStages() {
        return this.harvestStages;
    }
}

