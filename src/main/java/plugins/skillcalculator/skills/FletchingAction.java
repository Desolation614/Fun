/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.ItemSkillAction;

public enum FletchingAction implements ItemSkillAction
{
    ARROW_SHAFT(52, 1, 0.33f),
    HEADLESS_ARROW(53, 1, 1.0f),
    BRONZE_ARROW(882, 1, 1.3f),
    BRONZE_JAVELIN(825, 3, 1.0f),
    OGRE_ARROW(2866, 5, 1.0f),
    SHORTBOW(841, 5, 5.0f),
    SHORTBOW_U(50, 5, 5.0f),
    BRONZE_BOLTS(877, 9, 0.5f),
    BRONZE_CROSSBOW(9174, 9, 6.0f),
    WOODEN_STOCK(9440, 9, 6.0f),
    BRONZE_CROSSBOW_U(9454, 9, 12.0f),
    BRONZE_DART(806, 10, 1.8f),
    LONGBOW(839, 10, 10.0f),
    LONGBOW_U(48, 10, 10.0f),
    OPAL_BOLTS(879, 11, 1.6f),
    IRON_ARROW(884, 15, 2.5f),
    IRON_JAVELIN(826, 17, 2.0f),
    OAK_SHORTBOW(843, 20, 16.5f),
    OAK_SHORTBOW_U(54, 20, 16.5f),
    IRON_DART(807, 22, 3.8f),
    BLURITE_CROSSBOW(9176, 24, 16.0f),
    OAK_STOCK(9442, 24, 16.0f),
    BLURITE_CROSSBOW_U(9456, 24, 32.0f),
    OAK_LONGBOW(845, 25, 25.0f),
    OAK_LONGBOW_U(56, 25, 25.0f),
    OAK_SHIELD(22251, 27, 50.0f),
    STEEL_ARROW(886, 30, 5.0f),
    KEBBIT_BOLTS(10158, 32, 1.0f),
    STEEL_JAVELIN(827, 32, 5.0f),
    WILLOW_SHORTBOW(849, 35, 33.3f),
    WILLOW_SHORTBOW_U(60, 35, 33.3f),
    STEEL_DART(808, 37, 7.5f),
    IRON_BOLTS(9140, 39, 1.5f),
    IRON_CROSSBOW(9177, 39, 22.0f),
    WILLOW_STOCK(9444, 39, 22.0f),
    IRON_CROSSBOW_U(9457, 39, 44.0f),
    WILLOW_LONGBOW(847, 40, 41.5f),
    WILLOW_LONGBOW_U(58, 40, 41.5f),
    BATTLESTAFF(1391, 40, 80.0f),
    PEARL_BOLTS(880, 41, 3.2f),
    LONG_KEBBIT_BOLTS(10159, 42, 1.3f),
    WILLOW_SHIELD(22254, 42, 83.0f),
    SILVER_BOLTS(9145, 43, 2.5f),
    MITHRIL_ARROW(888, 45, 7.5f),
    STEEL_BOLTS(9141, 46, 3.5f),
    STEEL_CROSSBOW(9179, 46, 27.0f),
    TEAK_STOCK(9446, 46, 27.0f),
    STEEL_CROSSBOW_U(9459, 46, 54.0f),
    MITHRIL_JAVELIN(828, 47, 8.0f),
    MAPLE_SHORTBOW(853, 50, 50.0f),
    MAPLE_SHORTBOW_U(64, 50, 50.0f),
    BARBED_BOLTS(881, 51, 9.5f),
    BROAD_ARROWS(4150, 52, 10.0f),
    MITHRIL_DART(809, 52, 11.2f),
    MITHRIL_BOLTS(9142, 54, 5.0f),
    MAPLE_STOCK(9448, 54, 32.0f),
    MITHRIL_CROSSBOW(9181, 54, 32.0f),
    MITHRIL_CROSSBOW_U(9461, 54, 64.0f),
    BROAD_BOLTS(11875, 55, 3.0f),
    MAPLE_LONGBOW(851, 55, 58.2f),
    MAPLE_LONGBOW_U(62, 55, 58.3f),
    SAPPHIRE_BOLTS(9337, 56, 4.7f),
    MAPLE_SHIELD(22257, 57, 116.5f),
    EMERALD_BOLTS(9338, 58, 5.5f),
    HUNTERS_SPEAR(29305, 60, 9.5f),
    ADAMANT_ARROW(890, 60, 10.0f),
    ADAMANT_BOLTS(9143, 61, 7.0f),
    ADAMANT_CROSSBOW(9183, 61, 41.0f),
    MAHOGANY_STOCK(9450, 61, 41.0f),
    ADAMANT_CROSSBOW_U(9463, 61, 82.0f),
    ADAMANT_JAVELIN(829, 62, 10.0f),
    RUBY_BOLTS(9339, 63, 6.3f),
    DIAMOND_BOLTS(9340, 65, 7.0f),
    YEW_SHORTBOW(857, 65, 67.5f),
    YEW_SHORTBOW_U(68, 65, 67.5f),
    ADAMANT_DART(810, 67, 15.0f),
    RUNITE_BOLTS(9144, 69, 10.0f),
    RUNE_CROSSBOW(9185, 69, 50.0f),
    YEW_STOCK(9452, 69, 50.0f),
    RUNITE_CROSSBOW_U(9465, 69, 100.0f),
    YEW_LONGBOW(855, 70, 75.0f),
    YEW_LONGBOW_U(66, 70, 75.0f),
    DRAGONSTONE_BOLTS(9341, 71, 8.2f),
    YEW_SHIELD(22260, 72, 150.0f),
    ONYX_BOLTS(9342, 73, 9.4f),
    RUNE_ARROW(892, 75, 12.5f),
    AMETHYST_BROAD_BOLTS(21316, 76, 10.6f),
    RUNE_JAVELIN(830, 77, 12.4f),
    DRAGON_CROSSBOW(21902, 78, 70.0f),
    MAGIC_STOCK(21952, 78, 70.0f),
    TOXIC_BLOWPIPE(12926, 78, 120.0f),
    DRAGON_CROSSBOW_U(21921, 78, 135.0f),
    MAGIC_SHORTBOW(861, 80, 83.3f),
    MAGIC_SHORTBOW_U(72, 80, 83.3f),
    RUNE_DART(811, 81, 18.8f),
    AMETHYST_ARROW(21326, 82, 13.5f),
    DRAGON_BOLTS(21930, 84, 12.0f),
    AMETHYST_JAVELIN(21318, 84, 13.5f),
    MAGIC_LONGBOW(859, 85, 91.5f),
    MAGIC_LONGBOW_U(70, 85, 91.5f),
    MAGIC_SHIELD(22263, 87, 183.0f),
    DRAGON_ARROW(11212, 90, 15.0f),
    AMETHYST_DART(25849, 90, 21.0f),
    DRAGON_JAVELIN(19484, 92, 15.0f),
    REDWOOD_SHIELD(22266, 92, 216.0f),
    DRAGON_DART(11230, 95, 25.0f);

    private final int itemId;
    private final int level;
    private final float xp;

    @Override
    public boolean isMembers(ItemManager itemManager) {
        return true;
    }

    private FletchingAction(int itemId, int level, float xp) {
        this.itemId = itemId;
        this.level = level;
        this.xp = xp;
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
}

