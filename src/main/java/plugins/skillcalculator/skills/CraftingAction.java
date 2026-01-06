/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.ItemSkillAction;

public enum CraftingAction implements ItemSkillAction
{
    BALL_OF_WOOL(1759, 1, 2.5f),
    POT(1931, 1, 6.3f),
    UNFIRED_POT(1787, 1, 6.3f),
    OPAL_RING(21081, 1, 10.0f),
    LEATHER_GLOVES(1059, 1, 13.8f),
    OPAL(1609, 1, 15.0f),
    BEER_GLASS(1919, 1, 17.5f, true),
    MOLTEN_GLASS(1775, 1, 20.0f),
    EMPTY_CANDLE_LANTERN(4527, 4, 19.0f),
    BIRD_HOUSE(21512, 5, 15.0f),
    GOLD_RING(1635, 5, 15.0f),
    GOLD_NECKLACE(1654, 6, 20.0f),
    PIE_DISH(2313, 7, 10.0f),
    UNFIRED_PIE_DISH(1789, 7, 15.0f),
    LEATHER_BOOTS(1061, 7, 16.3f),
    GOLD_BRACELET(11069, 7, 25.0f),
    BOWL(1923, 8, 15.0f),
    UNFIRED_BOWL(1791, 8, 18.0f),
    GOLD_AMULET_U(1673, 8, 30.0f),
    COWL(1167, 9, 18.5f),
    BOW_STRING(1777, 10, 15.0f),
    CROSSBOW_STRING(9438, 10, 15.0f),
    LEATHER_VAMBRACES(1063, 11, 22.0f),
    EMPTY_OIL_LAMP(4525, 12, 25.0f),
    JADE(1611, 13, 20.0f),
    JADE_RING(21084, 13, 32.0f),
    LEATHER_BODY(1129, 14, 25.0f),
    OAK_BIRD_HOUSE(21515, 15, 20.0f),
    RED_TOPAZ(1613, 16, 25.0f),
    OPAL_NECKLACE(21090, 16, 35.0f),
    TOPAZ_RING(21087, 16, 35.0f),
    HOLY_SYMBOL(1718, 16, 50.0f),
    UNHOLY_SYMBOL(1724, 17, 50.0f),
    LEATHER_CHAPS(1095, 18, 27.0f),
    EMPTY_PLANT_POT(5350, 19, 17.5f),
    UNFIRED_PLANT_POT(5352, 19, 20.0f),
    MAGIC_STRING(6038, 19, 30.0f),
    SAPPHIRE_RING(1637, 20, 40.0f),
    SAPPHIRE(1607, 20, 50.0f),
    EMPTY_SACK(5418, 21, 38.0f),
    OPAL_BRACELET(21117, 22, 45.0f),
    SAPPHIRE_NECKLACE(1656, 22, 55.0f),
    TIARA(5525, 23, 52.5f),
    SAPPHIRE_BRACELET(11072, 23, 60.0f),
    SAPPHIRE_AMULET_U(1675, 24, 65.0f),
    POT_LID(4440, 25, 20.0f),
    UNFIRED_POT_LID(4438, 25, 20.0f),
    WILLOW_BIRD_HOUSE(21518, 25, 25.0f),
    JADE_NECKLACE(21093, 25, 54.0f),
    DRIFT_NET(21652, 26, 55.0f),
    EMERALD_RING(1639, 27, 55.0f),
    OPAL_AMULET_U(21099, 27, 55.0f),
    EMERALD(1605, 27, 67.5f),
    HARDLEATHER_BODY(1131, 28, 35.0f),
    EMERALD_NECKLACE(1658, 29, 60.0f),
    JADE_BRACELET(21120, 29, 60.0f),
    ROPE(954, 30, 25.0f, true),
    EMERALD_BRACELET(11076, 30, 65.0f),
    EMERALD_AMULET_U(1677, 31, 70.0f),
    SPIKY_VAMBRACES(10077, 32, 6.0f),
    TOPAZ_NECKLACE(21096, 32, 70.0f),
    VIAL(229, 33, 35.0f, true),
    JADE_AMULET_U(21102, 34, 70.0f),
    RUBY_RING(1641, 34, 70.0f),
    RUBY(1603, 34, 85.0f),
    TEAK_BIRD_HOUSE(21521, 35, 30.0f),
    BROODOO_SHIELD(6235, 35, 100.0f),
    BASKET(5376, 36, 56.0f),
    COIF(1169, 38, 37.0f, true),
    TOPAZ_BRACELET(21123, 38, 75.0f),
    RUBY_NECKLACE(1660, 40, 75.0f),
    HARD_LEATHER_SHIELD(22269, 41, 70.0f),
    GOLD_TIARA(26788, 42, 35.0f),
    FISHBOWL(6668, 42, 42.5f),
    RUBY_BRACELET(11085, 42, 80.0f),
    DIAMOND_RING(1643, 43, 85.0f),
    DIAMOND(1601, 43, 107.5f),
    SNAKESKIN_BOOTS(6328, 45, 30.0f),
    MAPLE_BIRD_HOUSE(22192, 45, 35.0f),
    TOPAZ_AMULET_U(21105, 45, 80.0f),
    UNPOWERED_ORB(567, 46, 52.5f),
    SNAKESKIN_VAMBRACES(6330, 47, 35.0f),
    SNAKESKIN_BANDANA(6326, 48, 45.0f),
    LANTERN_LENS(4542, 49, 55.0f),
    MAHOGANY_BIRD_HOUSE(22195, 50, 40.0f),
    RUBY_AMULET_U(1679, 50, 85.0f),
    SNAKESKIN_CHAPS(6324, 51, 50.0f),
    SNAKESKIN_BODY(6322, 53, 55.0f),
    WATER_BATTLESTAFF(1395, 54, 100.0f),
    DRAGONSTONE_RING(1645, 55, 100.0f),
    DRAGONSTONE(1615, 55, 137.5f),
    DIAMOND_NECKLACE(1662, 56, 90.0f),
    SNAKESKIN_SHIELD(22272, 56, 100.0f),
    GREEN_DHIDE_VAMB(1065, 57, 62.0f, true),
    DIAMOND_BRACELET(11092, 58, 95.0f),
    EARTH_BATTLESTAFF(1399, 58, 112.5f),
    YEW_BIRD_HOUSE(22198, 60, 45.0f),
    GREEN_DHIDE_CHAPS(1099, 60, 124.0f, true),
    GREEN_DHIDE_SHIELD(22275, 62, 124.0f),
    FIRE_BATTLESTAFF(1393, 62, 125.0f),
    GREEN_DHIDE_BODY(1135, 63, 186.0f, true),
    BLUE_DHIDE_VAMB(2487, 66, 70.0f),
    AIR_BATTLESTAFF(1397, 66, 137.5f),
    ONYX_RING(6575, 67, 115.0f),
    ONYX(6573, 67, 167.5f),
    BLUE_DHIDE_CHAPS(2493, 68, 140.0f),
    BLUE_DHIDE_SHIELD(22278, 69, 140.0f),
    DIAMOND_AMULET_U(1681, 70, 100.0f),
    BLUE_DHIDE_BODY(2499, 71, 210.0f),
    DRAGONSTONE_NECKLACE(1664, 72, 105.0f),
    RED_DHIDE_VAMB(2489, 73, 78.0f),
    DRAGONSTONE_BRACELET(11115, 74, 110.0f),
    MAGIC_BIRD_HOUSE(22201, 75, 50.0f),
    RED_DHIDE_CHAPS(2495, 75, 156.0f),
    RED_DHIDE_SHIELD(22281, 76, 156.0f),
    RED_DHIDE_BODY(2501, 77, 234.0f),
    BLACK_DHIDE_VAMB(2491, 79, 86.0f),
    DRAGONSTONE_AMULET_U(1683, 80, 150.0f),
    ONYX_NECKLACE(6577, 82, 120.0f),
    BLACK_DHIDE_CHAPS(2497, 82, 172.0f),
    AMETHYST_BOLT_TIPS(21338, 83, 4.0f),
    BLACK_DHIDE_SHIELD(22284, 83, 172.0f),
    ONYX_BRACELET(11130, 84, 125.0f),
    BLACK_DHIDE_BODY(2503, 84, 258.0f),
    AMETHYST_ARROWTIPS(21350, 85, 4.0f),
    AMETHYST_JAVELIN_HEADS(21352, 87, 12.0f),
    LIGHT_ORB(10973, 87, 70.0f),
    AMETHYST_DART_TIP(25853, 89, 7.5f),
    ZENYTE_RING(19538, 89, 150.0f),
    ZENYTE(19493, 89, 200.0f),
    REDWOOD_BIRD_HOUSE(22204, 90, 55.0f),
    ONYX_AMULET_U(6579, 90, 165.0f),
    ZENYTE_NECKLACE(19535, 92, 165.0f),
    ZENYTE_BRACELET(19532, 95, 180.0f),
    ZENYTE_AMULET_U(19501, 98, 200.0f);

    private final int itemId;
    private final int level;
    private final float xp;
    private final boolean isMembersOverride;

    private CraftingAction(int itemId, int level, float xp) {
        this(itemId, level, xp, false);
    }

    @Override
    public boolean isMembers(ItemManager itemManager) {
        return this.isMembersOverride() || ItemSkillAction.super.isMembers(itemManager);
    }

    private CraftingAction(int itemId, int level, float xp, boolean isMembersOverride) {
        this.itemId = itemId;
        this.level = level;
        this.xp = xp;
        this.isMembersOverride = isMembersOverride;
    }

    @Override
    public int getItemId() {
        return this.itemId;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public float getXp() {
        return this.xp;
    }

    public boolean isMembersOverride() {
        return this.isMembersOverride;
    }
}

