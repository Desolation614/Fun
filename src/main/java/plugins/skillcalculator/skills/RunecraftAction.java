/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.ItemSkillAction;
import net.runelite.client.plugins.skillcalculator.skills.RunecraftBonus;

public enum RunecraftAction implements ItemSkillAction
{
    AIR_RUNE(556, 1, 5.0f, false),
    AIR_TIARA(5527, 1, 25.0f, true),
    MIND_TIARA(5529, 1, 27.5f, true),
    WATER_TIARA(5531, 1, 30.0f, true),
    EARTH_TIARA(5535, 1, 32.5f, true),
    FIRE_TIARA(5537, 1, 35.0f, true),
    BODY_TIARA(5533, 1, 37.5f, true),
    COSMIC_TIARA(5539, 1, 40.0f, true),
    CHAOS_TIARA(5543, 1, 42.5f, true),
    NATURE_TIARA(5541, 1, 45.0f, true),
    LAW_TIARA(5545, 1, 47.5f, true),
    DEATH_TIARA(5547, 1, 50.0f, true),
    WRATH_TIARA(22121, 1, 52.5f, true),
    MIND_RUNE(558, 2, 5.5f, false),
    MIND_CORE(25696, 2, 55.0f, true),
    WATER_RUNE(555, 5, 6.0f, false),
    MIST_RUNE(4695, 6, 8.5f, false),
    EARTH_RUNE(557, 9, 6.5f, false),
    DUST_RUNE(4696, 10, 9.0f, false),
    MUD_RUNE(4698, 13, 9.5f, false),
    FIRE_RUNE(554, 14, 7.0f, false),
    SMOKE_RUNE(4697, 15, 9.5f, false),
    STEAM_RUNE(4694, 19, 10.0f, false),
    BODY_RUNE(559, 20, 7.5f, false),
    BODY_CORE(25698, 20, 75.0f, true),
    LAVA_RUNE(4699, 23, 10.5f, false),
    COSMIC_RUNE(564, 27, 8.0f, false, true),
    SUNFIRE_RUNE(28929, 33, 9.0f, false),
    CHAOS_RUNE(562, 35, 8.5f, false, true),
    CHAOS_CORE(25700, 35, 85.0f, true),
    ASTRAL_RUNE(9075, 40, 8.7f, false),
    NATURE_RUNE(561, 44, 9.0f, false, true),
    LAW_RUNE(563, 54, 9.5f, false, true),
    DEATH_RUNE(560, 65, 10.0f, false, true),
    TRUE_BLOOD_RUNE(565, 77, 10.5f, false){

        @Override
        public String getName(ItemManager itemManager) {
            return "Blood rune (True Altar)";
        }
    }
    ,
    ZEAH_BLOOD_RUNE(565, 77, 24.425f, true){

        @Override
        public String getName(ItemManager itemManager) {
            return "Blood rune (Zeah)";
        }
    }
    ,
    AETHER_RUNE(30843, 90, 20.0f, false),
    SOUL_RUNE(566, 90, 30.325f, true),
    WRATH_RUNE(21880, 95, 8.0f, false);

    private static final Set<RunecraftBonus> RUNECRAFT_BONUSES;
    private final int itemId;
    private final int level;
    private final float xp;
    private final boolean ignoreBonus;
    private final boolean isMembersOverride;

    private RunecraftAction(int itemId, int level, float xp, boolean ignoreBonus) {
        this(itemId, level, xp, ignoreBonus, false);
    }

    public Set<RunecraftBonus> getExcludedSkillBonuses() {
        if (this.ignoreBonus) {
            return RUNECRAFT_BONUSES;
        }
        return Collections.emptySet();
    }

    private RunecraftAction(int itemId, int level, float xp, boolean ignoreBonus, boolean isMembersOverride) {
        this.itemId = itemId;
        this.level = level;
        this.xp = xp;
        this.ignoreBonus = ignoreBonus;
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

    public boolean isIgnoreBonus() {
        return this.ignoreBonus;
    }

    public boolean isMembersOverride() {
        return this.isMembersOverride;
    }

    static {
        RUNECRAFT_BONUSES = EnumSet.allOf(RunecraftBonus.class);
    }
}

