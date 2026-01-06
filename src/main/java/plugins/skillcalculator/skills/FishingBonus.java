/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.plugins.skillcalculator.skills.SkillBonus;

public enum FishingBonus implements SkillBonus
{
    ANGLERS_OUTFIT("Angler's Outfit", 1.025f);

    private final String name;
    private final float value;

    private FishingBonus(String name, float value) {
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

