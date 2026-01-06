/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.ItemSkillAction;

public enum MiningAction implements ItemSkillAction
{
    CLAY(434, 1, 5.0f),
    RUNE_ESSENCE(1436, 1, 5.0f),
    COPPER_ORE(436, 1, 17.5f),
    TIN_ORE(438, 1, 17.5f),
    LIMESTONE(3211, 10, 26.5f),
    BARRONITE_SHARDS(25683, 14, 16.0f){

        @Override
        public String getName(ItemManager itemManager) {
            return "Barronite shards";
        }
    }
    ,
    BARRONITE_DEPOSIT(25684, 14, 32.0f),
    IRON_ORE(440, 15, 35.0f),
    SILVER_ORE(442, 20, 40.0f),
    PURE_ESSENCE(7936, 30, 5.0f){

        @Override
        public boolean isMembers(ItemManager itemManager) {
            return true;
        }
    }
    ,
    COAL(453, 30, 50.0f),
    SANDSTONE_1KG(6971, 35, 30.0f),
    SANDSTONE_2KG(6973, 35, 40.0f),
    SANDSTONE_5KG(6975, 35, 50.0f),
    SANDSTONE_10KG(6977, 35, 60.0f),
    DENSE_ESSENCE_BLOCK(13445, 38, 12.0f),
    GEM_ROCKS(1629, 40, 65.0f){

        @Override
        public String getName(ItemManager itemManager) {
            return "Gem rocks";
        }
    }
    ,
    GOLD_ORE(444, 40, 65.0f),
    CALCIFIED_ROCKS(29381, 41, 33.0f){

        @Override
        public String getName(ItemManager itemManager) {
            return "Calcified Rocks";
        }
    }
    ,
    GRANITE_500G(6979, 45, 50.0f),
    GRANITE_2KG(6981, 45, 60.0f),
    GRANITE_5KG(6983, 45, 75.0f),
    MITHRIL_ORE(447, 55, 80.0f),
    SOFT_CLAY(1761, 70, 5.0f){

        @Override
        public boolean isMembers(ItemManager itemManager) {
            return true;
        }
    }
    ,
    ADAMANTITE_ORE(449, 70, 95.0f),
    RUNITE_ORE(451, 85, 125.0f),
    AMETHYST(21347, 92, 240.0f);

    private final int itemId;
    private final int level;
    private final float xp;

    private MiningAction(int itemId, int level, float xp) {
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

