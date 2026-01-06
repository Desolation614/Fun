/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import java.util.EnumSet;
import java.util.Set;
import net.runelite.client.plugins.skillcalculator.skills.SkillBonus;

public enum WoodcuttingBonus implements SkillBonus
{
    LUMBERJACK_OUTFIT("Lumberjack Outfit", 1.025f),
    FELLING_AXE_RATIONS("Felling Axe + Rations", 1.1f);

    private final String name;
    private final float value;

    public Set<WoodcuttingBonus> getCanBeStackedWith() {
        EnumSet<WoodcuttingBonus> others = EnumSet.allOf(WoodcuttingBonus.class);
        others.remove(this);
        return others;
    }

    private WoodcuttingBonus(String name, float value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getValue() {
        return this.value;
    }
}

