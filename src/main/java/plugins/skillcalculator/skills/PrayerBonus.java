/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import java.util.EnumSet;
import java.util.Set;
import net.runelite.client.plugins.skillcalculator.skills.SkillBonus;

public enum PrayerBonus implements SkillBonus
{
    BONECRUSHER("Bonecrusher", 0.5f),
    SACRED_BONE_BURNER("Sacred Bone Burner", 3.0f),
    SINISTER_OFFERING("Sinister Offering", 3.0f),
    LIT_GILDED_ALTAR("Lit Gilded Altar", 3.5f),
    ECTOFUNTUS("Ectofuntus", 4.0f),
    CHAOS_ALTAR("Chaos Altar", 7.0f),
    BLESSED_SUNFIRE_WINE("Blessed Sunfire Wine", 1.2f),
    DEMONIC_OFFERING("Demonic Offering", 3.0f),
    MORYTANIA_DIARY_3_SHADES("Morytania Diary 3 Shades", 1.5f),
    ZEALOT_ROBES("Zealot Robes", 1.05f);

    private final String name;
    private final float value;

    public Set<PrayerBonus> getCanBeStackedWith() {
        switch (this) {
            case ECTOFUNTUS: 
            case LIT_GILDED_ALTAR: 
            case CHAOS_ALTAR: 
            case SINISTER_OFFERING: {
                return EnumSet.complementOf(EnumSet.of(ECTOFUNTUS, new PrayerBonus[]{LIT_GILDED_ALTAR, CHAOS_ALTAR, SINISTER_OFFERING, SACRED_BONE_BURNER, BONECRUSHER}));
            }
            case BONECRUSHER: 
            case SACRED_BONE_BURNER: {
                return EnumSet.complementOf(EnumSet.of(ECTOFUNTUS, new PrayerBonus[]{LIT_GILDED_ALTAR, CHAOS_ALTAR, SINISTER_OFFERING, SACRED_BONE_BURNER, BONECRUSHER, ZEALOT_ROBES}));
            }
            case ZEALOT_ROBES: {
                return EnumSet.complementOf(EnumSet.of(BONECRUSHER, SACRED_BONE_BURNER, ZEALOT_ROBES));
            }
        }
        return EnumSet.complementOf(EnumSet.of(this));
    }

    private PrayerBonus(String name, float value) {
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

