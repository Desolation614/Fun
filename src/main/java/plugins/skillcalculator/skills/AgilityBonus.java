/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import java.util.EnumSet;
import java.util.Set;
import net.runelite.client.plugins.skillcalculator.skills.SkillBonus;

public enum AgilityBonus implements SkillBonus
{
    WILDERNESS_AGILITY_TICKET_11_TO_50("11-50 Wilderness Tickets", 1.05f),
    WILDERNESS_AGILITY_TICKET_51_TO_100("51-100 Wilderness Tickets", 1.1f),
    WILDERNESS_AGILITY_TICKET_101_PLUS("101+ Wilderness Tickets", 1.15f),
    DESERT_HARD_DIARY("Desert Hard Diary", 1.1415731f),
    FREMENNIK_HARD_DIARY("Fremennik Hard Diary", 1.1794872f);

    private final String name;
    private final float value;

    public Set<AgilityBonus> getCanBeStackedWith() {
        switch (this) {
            case WILDERNESS_AGILITY_TICKET_11_TO_50: 
            case WILDERNESS_AGILITY_TICKET_51_TO_100: 
            case WILDERNESS_AGILITY_TICKET_101_PLUS: {
                return EnumSet.complementOf(EnumSet.of(WILDERNESS_AGILITY_TICKET_11_TO_50, WILDERNESS_AGILITY_TICKET_51_TO_100, WILDERNESS_AGILITY_TICKET_101_PLUS));
            }
        }
        return EnumSet.complementOf(EnumSet.of(this));
    }

    private AgilityBonus(String name, float value) {
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

