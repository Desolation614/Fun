/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.skillcalculator.skills;

import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.FiremakingBonus;
import net.runelite.client.plugins.skillcalculator.skills.ItemSkillAction;

public enum FiremakingAction implements ItemSkillAction
{
    SACRED_OIL_2(3434, "Applying Sacred oil(2)", 1, 10.0f, FiremakingMethod.SACRED_OIL),
    SACRED_OIL_3(3432, "Applying Sacred oil(3)", 1, 15.0f, FiremakingMethod.SACRED_OIL),
    SACRED_OIL_4(3430, "Applying Sacred oil(4)", 1, 20.0f, FiremakingMethod.SACRED_OIL),
    ACHEY_TREE_LOGS(2862, 1, 40.0f, FiremakingMethod.NORMAL_LOGS),
    LOGS(1511, 1, 40.0f, FiremakingMethod.NORMAL_LOGS),
    PYRE_LOGS(3438, 5, 50.0f, FiremakingMethod.PYRE_LOGS),
    OAK_LOGS(1521, 15, 60.0f, FiremakingMethod.NORMAL_LOGS),
    OAK_PYRE_LOGS(3440, 20, 70.0f, FiremakingMethod.PYRE_LOGS),
    WILLOW_LOGS(1519, 30, 90.0f, FiremakingMethod.NORMAL_LOGS),
    WILLOW_PYRE_LOGS(3442, 35, 100.0f, FiremakingMethod.PYRE_LOGS),
    TEAK_LOGS(6333, 35, 105.0f, FiremakingMethod.NORMAL_LOGS),
    TEAK_PYRE_LOGS(6211, 40, 120.0f, FiremakingMethod.PYRE_LOGS),
    ARCTIC_PINE_LOGS(10810, 42, 125.0f, FiremakingMethod.NORMAL_LOGS),
    MAPLE_LOGS(1517, 45, 135.0f, FiremakingMethod.NORMAL_LOGS),
    ARCTIC_PYRE_LOGS(10808, 47, 158.0f, FiremakingMethod.PYRE_LOGS),
    MAHOGANY_LOGS(6332, 50, 157.5f, FiremakingMethod.NORMAL_LOGS),
    MAPLE_PYRE_LOGS(3444, 50, 175.0f, FiremakingMethod.PYRE_LOGS),
    MAHOGANY_PYRE_LOGS(6213, 55, 210.0f, FiremakingMethod.PYRE_LOGS),
    YEW_LOGS(1515, 60, 202.5f, FiremakingMethod.NORMAL_LOGS),
    BLISTERWOOD_LOGS(24691, 62, 96.0f, FiremakingMethod.NORMAL_LOGS),
    YEW_PYRE_LOGS(3446, 65, 255.0f, FiremakingMethod.PYRE_LOGS),
    MAGIC_LOGS(1513, 75, 303.8f, FiremakingMethod.NORMAL_LOGS),
    MAGIC_PYRE_LOGS(3448, 80, 404.5f, FiremakingMethod.PYRE_LOGS),
    REDWOOD_LOGS(19669, 90, 350.0f, FiremakingMethod.NORMAL_LOGS),
    REDWOOD_PYRE_LOGS(19672, 95, 500.0f, FiremakingMethod.PYRE_LOGS);

    private final int itemId;
    @Nullable
    private final String nameOverride;
    private final int level;
    private final float xp;
    private final FiremakingMethod firemakingMethod;

    private FiremakingAction(int itemId, int level, float xp, FiremakingMethod firemakingMethod) {
        this(itemId, null, level, xp, firemakingMethod);
    }

    @Override
    public String getName(ItemManager itemManager) {
        if (this.nameOverride != null) {
            return this.nameOverride;
        }
        return ItemSkillAction.super.getName(itemManager);
    }

    public Set<FiremakingBonus> getExcludedSkillBonuses() {
        switch (this.getFiremakingMethod()) {
            case NORMAL_LOGS: {
                return EnumSet.of(FiremakingBonus.MORYTANIA_ELITE_DIARY);
            }
            case PYRE_LOGS: {
                return EnumSet.complementOf(EnumSet.of(FiremakingBonus.MORYTANIA_ELITE_DIARY));
            }
        }
        return EnumSet.complementOf(EnumSet.of(FiremakingBonus.PYROMANCER_OUTFIT));
    }

    private FiremakingAction(int itemId, String nameOverride, int level, float xp, FiremakingMethod firemakingMethod) {
        this.itemId = itemId;
        this.nameOverride = nameOverride;
        this.level = level;
        this.xp = xp;
        this.firemakingMethod = firemakingMethod;
    }

    @Override
    public int getItemId() {
        return this.itemId;
    }

    @Nullable
    public String getNameOverride() {
        return this.nameOverride;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public float getXp() {
        return this.xp;
    }

    public FiremakingMethod getFiremakingMethod() {
        return this.firemakingMethod;
    }

    private static enum FiremakingMethod {
        NORMAL_LOGS,
        PYRE_LOGS,
        SACRED_OIL;

    }
}

