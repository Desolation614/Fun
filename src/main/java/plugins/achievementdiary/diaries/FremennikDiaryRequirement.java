/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Quest
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.achievementdiary.diaries;

import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.client.plugins.achievementdiary.GenericDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.QuestRequirement;
import net.runelite.client.plugins.achievementdiary.SkillRequirement;

public class FremennikDiaryRequirement
extends GenericDiaryRequirement {
    public FremennikDiaryRequirement() {
        this.add("Catch a Cerulean twitch.", new SkillRequirement(Skill.HUNTER, 11));
        this.add("Change your boots at Yrsa's Shoe Store.", new QuestRequirement(Quest.THE_FREMENNIK_TRIALS));
        this.add("Craft a tiara from scratch in Rellekka.", new SkillRequirement(Skill.CRAFTING, 23), new SkillRequirement(Skill.MINING, 20), new SkillRequirement(Skill.SMITHING, 20), new QuestRequirement(Quest.THE_FREMENNIK_TRIALS));
        this.add("Browse the Stonemasons shop.", new QuestRequirement(Quest.THE_GIANT_DWARF, true));
        this.add("Steal from the Keldagrim crafting or baker's stall.", new SkillRequirement(Skill.THIEVING, 5), new QuestRequirement(Quest.THE_GIANT_DWARF, true));
        this.add("Enter the Troll Stronghold.", new QuestRequirement(Quest.DEATH_PLATEAU), new QuestRequirement(Quest.TROLL_STRONGHOLD, true));
        this.add("Chop and burn some oak logs in the Fremennik Province.", new SkillRequirement(Skill.WOODCUTTING, 15), new SkillRequirement(Skill.FIREMAKING, 15));
        this.add("Slay a Brine rat.", new SkillRequirement(Skill.SLAYER, 47), new QuestRequirement(Quest.OLAFS_QUEST, true));
        this.add("Travel to the Snowy Hunter Area via Eagle.", new QuestRequirement(Quest.EAGLES_PEAK));
        this.add("Mine some coal in Rellekka.", new SkillRequirement(Skill.MINING, 30), new QuestRequirement(Quest.THE_FREMENNIK_TRIALS));
        this.add("Steal from the Rellekka Fish stalls.", new SkillRequirement(Skill.THIEVING, 42), new QuestRequirement(Quest.THE_FREMENNIK_TRIALS));
        this.add("Travel to Miscellania by Fairy ring.", new QuestRequirement(Quest.THE_FREMENNIK_TRIALS), new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, true));
        this.add("Catch a Snowy knight.", new SkillRequirement(Skill.HUNTER, 35));
        this.add("Pick up your Pet Rock from your POH Menagerie.", new SkillRequirement(Skill.CONSTRUCTION, 37), new QuestRequirement(Quest.THE_FREMENNIK_TRIALS));
        this.add("Visit the Lighthouse from Waterbirth island.", new QuestRequirement(Quest.HORROR_FROM_THE_DEEP), new QuestRequirement(Quest.THE_FREMENNIK_TRIALS, true));
        this.add("Mine some gold at the Arzinian mine.", new SkillRequirement(Skill.MINING, 40), new QuestRequirement(Quest.BETWEEN_A_ROCK, true));
        this.add("Teleport to Trollheim.", new SkillRequirement(Skill.MAGIC, 61), new QuestRequirement(Quest.EADGARS_RUSE));
        this.add("Catch a Sabre-toothed Kyatt.", new SkillRequirement(Skill.HUNTER, 55));
        this.add("Mix a super defence potion in the Fremennik province.", new SkillRequirement(Skill.HERBLORE, 66));
        this.add("Steal from the Keldagrim Gem Stall.", new SkillRequirement(Skill.THIEVING, 75), new QuestRequirement(Quest.THE_GIANT_DWARF, true));
        this.add("Craft a Fremennik shield on Neitiznot.", new SkillRequirement(Skill.WOODCUTTING, 56), new QuestRequirement(Quest.THE_FREMENNIK_ISLES));
        this.add("Mine 5 Adamantite ores on Jatizso.", new SkillRequirement(Skill.MINING, 70), new QuestRequirement(Quest.THE_FREMENNIK_ISLES));
        this.add("Obtain 100% support from your kingdom subjects.", new QuestRequirement(Quest.THRONE_OF_MISCELLANIA));
        this.add("Teleport to Waterbirth Island.", new SkillRequirement(Skill.MAGIC, 72), new QuestRequirement(Quest.LUNAR_DIPLOMACY));
        this.add("Obtain the Blast Furnace Foreman's permission to use the Blast Furnace for free.", new SkillRequirement(Skill.SMITHING, 60), new QuestRequirement(Quest.THE_GIANT_DWARF, true));
        this.add("Craft 56 astral runes simultaneously from Essence without the use of Extracts.", new SkillRequirement(Skill.RUNECRAFT, 82), new QuestRequirement(Quest.LUNAR_DIPLOMACY));
        this.add("Create a dragonstone amulet in the Neitiznot furnace.", new SkillRequirement(Skill.CRAFTING, 80), new QuestRequirement(Quest.THE_FREMENNIK_ISLES, true));
        this.add("Complete a lap of the Rellekka agility course.", new SkillRequirement(Skill.AGILITY, 80));
        this.add("Kill the generals of Armadyl, Bandos, Saradomin and Zamorak in the God Wars Dungeon.", new SkillRequirement(Skill.AGILITY, 70), new SkillRequirement(Skill.STRENGTH, 70), new SkillRequirement(Skill.HITPOINTS, 70), new SkillRequirement(Skill.RANGED, 70), new QuestRequirement(Quest.TROLL_STRONGHOLD));
        this.add("Slay a Spiritual mage within the Godwars Dungeon.", new SkillRequirement(Skill.SLAYER, 83), new QuestRequirement(Quest.TROLL_STRONGHOLD));
    }
}

