/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.ItemSkillAction;

public enum WoodcuttingAction implements ItemSkillAction
{
    ACHEY_TREE_LOGS(2862, 1, 25.0f),
    LOGS(1511, 1, 25.0f),
    OAK_LOGS(1521, 15, 37.5f),
    WILLOW_LOGS(1519, 30, 67.5f),
    TEAK_LOGS(6333, 35, 85.0f),
    JUNIPER_LOGS(13355, 42, 35.0f),
    BARK(3239, 45, 82.5f),
    MAPLE_LOGS(1517, 45, 100.0f),
    MAHOGANY_LOGS(6332, 50, 125.0f),
    ARCTIC_PINE_LOGS(10810, 54, 40.0f),
    YEW_LOGS(1515, 60, 175.0f),
    BLISTERWOOD_LOGS(24691, 62, 76.0f),
    SULLIUSCEPS(21626, 65, 127.0f){

        @Override
        public String getName(ItemManager itemManager) {
            return "Sulliusceps";
        }
    }
    ,
    MAGIC_LOGS(1513, 75, 250.0f),
    REDWOOD_LOGS(19669, 90, 380.0f);

    private final int itemId;
    private final int level;
    private final float xp;

    private WoodcuttingAction(int itemId, int level, float xp) {
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

