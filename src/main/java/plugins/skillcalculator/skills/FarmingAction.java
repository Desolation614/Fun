/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.NamedSkillAction;

public enum FarmingAction implements NamedSkillAction
{
    POTATOES("Potatoes", 1, 8.0f, 1942),
    WINTER_SQIRK("Winter Sq'irk", 1, 30.0f, 10847),
    DOCK_LEAF("Dock Leaf", 1, 31.0f, 8183),
    FERN_BIG_PLANT("Fern (big plant)", 1, 31.0f, 8186),
    PLANT("Plant", 1, 31.0f, 8180),
    SHORT_PLANT("Short Plant", 1, 31.0f, 8189),
    SPRING_SQIRK("Spring Sq'irk", 1, 40.0f, 10844),
    AUTUMN_SQIRK("Autumn Sq'irk", 1, 50.0f, 10846),
    SUMMER_SQIRK("Summer Sq'irk", 1, 60.0f, 10845),
    BUSH("Bush", 1, 70.0f, 8187),
    LARGE_LEAF_BUSH("Large Leaf Bush", 1, 70.0f, 8190),
    SMALL_FERN("Small Fern", 1, 70.0f, 8181),
    THISTLE("Thistle", 1, 70.0f, 8184),
    FERN_SMALL_PLANT("Fern (small plant)", 1, 100.0f, 8182),
    HUGE_PLANT("Huge Plant", 1, 100.0f, 8191),
    REEDS("Reeds", 1, 100.0f, 8185),
    TALL_PLANT("Tall Plant", 1, 100.0f, 8188),
    ONIONS("Onions", 5, 10.0f, 1957),
    CABBAGES("Cabbages", 7, 10.0f, 1965),
    GUAM_LEAF("Guam Leaf", 9, 11.0f, 249),
    TOMATOES("Tomatoes", 12, 12.5f, 1982),
    MARRENTILL("Marrentill", 14, 13.5f, 251),
    OAK_TREE("Oak Tree", 15, 481.3f, 1521),
    TARROMIN("Tarromin", 19, 16.0f, 253),
    SWEETCORN("Sweetcorn", 20, 17.0f, 5986),
    GIANT_SEAWEED("Giant seaweed", 23, 21.0f, 21504),
    HARRALANDER("Harralander", 26, 21.5f, 255),
    LIMPWURT_PLANT("Limpwurt Plant", 26, 40.0f, 225),
    APPLE_TREE("Apple Tree", 27, 1221.5f, 1955),
    GOUTWEED("Goutweed", 29, 105.0f, 3261),
    WILLOW_TREE("Willow Tree", 30, 1481.5f, 1519),
    STRAWBERRIES("Strawberries", 31, 26.0f, 5504),
    RANARR_WEED("Ranarr Weed", 32, 27.0f, 257),
    BANANA_TREE("Banana Tree", 33, 1778.5f, 1963),
    TEAK_TREE("Teak Tree", 35, 7315.0f, 6333),
    TOADFLAX("Toadflax", 38, 34.0f, 2998),
    ORANGE_TREE("Orange Tree", 39, 2505.7f, 2108),
    CURRY_TREE("Curry Tree", 42, 2946.9f, 5970),
    IRIT_LEAF("Irit Leaf", 44, 43.0f, 259),
    MAPLE_TREE("Maple Tree", 45, 3448.4f, 1517),
    WATERMELONS("Watermelons", 47, 49.0f, 5982),
    AVANTOE("Avantoe", 50, 54.5f, 261),
    PINEAPPLE_PLANT("Pineapple Plant", 51, 4662.7f, 2114),
    MAHOGANY_TREE("Mahogany Tree", 55, 15783.0f, 6332),
    KWUARM("Kwuarm", 56, 69.0f, 263),
    PAPAYA_TREE("Papaya Tree", 57, 6218.4f, 5972),
    WHITE_LILY("White lily", 58, 292.0f, 22932),
    YEW_TREE("Yew Tree", 60, 7150.9f, 1515),
    SNAPE_GRASS("Snape grass", 61, 82.0f, 231),
    SNAPDRAGON("Snapdragon", 62, 87.5f, 3000),
    HUASCA("Huasca", 65, 86.5f, 30097),
    HESPORI("Hespori", 65, 12662.0f, 23044),
    CADANTINE("Cadantine", 67, 106.5f, 265),
    PALM_TREE("Palm Tree", 68, 10260.6f, 5974),
    CALQUAT_TREE("Calquat Tree", 72, 12225.5f, 5980),
    LANTADYME("Lantadyme", 73, 134.5f, 2481),
    CRYSTAL_TREE("Crystal Tree", 74, 13366.0f, 23962),
    MAGIC_TREE("Magic Tree", 75, 13913.8f, 1513),
    DWARF_WEED("Dwarf Weed", 79, 170.5f, 267),
    DRAGONFRUIT_TREE("Dragonfruit Tree", 81, 17895.0f, 22929),
    SPIRIT_TREE("Spirit Tree", 83, 19501.3f, 6063),
    TORSTOL("Torstol", 85, 199.5f, 269),
    CELASTRUS_TREE("Celastrus Tree", 85, 14334.0f, 22935),
    REDWOOD_TREE("Redwood Tree", 90, 22680.0f, 19669);

    private final String name;
    private final int level;
    private final float xp;
    private final int icon;

    @Override
    public boolean isMembers(ItemManager itemManager) {
        return true;
    }

    private FarmingAction(String name, int level, float xp, int icon) {
        this.name = name;
        this.level = level;
        this.xp = xp;
        this.icon = icon;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public float getXp() {
        return this.xp;
    }

    @Override
    public int getIcon() {
        return this.icon;
    }
}

