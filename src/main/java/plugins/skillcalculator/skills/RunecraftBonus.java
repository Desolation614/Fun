/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.plugins.skillcalculator.skills.SkillBonus;

public enum RunecraftBonus implements SkillBonus
{
    DAEYALT_ESSENCE("Daeyalt essence", 1.5f);

    private final String name;
    private final float value;

    private RunecraftBonus(String name, float value) {
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

