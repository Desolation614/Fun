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
import net.runelite.client.plugins.achievementdiary.CombatLevelRequirement;
import net.runelite.client.plugins.achievementdiary.GenericDiaryRequirement;
import net.runelite.client.plugins.achievementdiary.QuestRequirement;
import net.runelite.client.plugins.achievementdiary.SkillRequirement;

public class WesternDiaryRequirement
extends GenericDiaryRequirement {
    public WesternDiaryRequirement() {
        this.add("Catch a Copper Longtail.", new SkillRequirement(Skill.HUNTER, 9));
        this.add("Complete a novice game of Pest Control.", new CombatLevelRequirement(40));
        this.add("Mine some Iron Ore near Piscatoris.", new SkillRequirement(Skill.MINING, 15));
        this.add("Claim any Chompy bird hat from Rantz.", new QuestRequirement(Quest.BIG_CHOMPY_BIRD_HUNTING));
        this.add("Have Brimstail teleport you to the Essence Mine.", new QuestRequirement(Quest.RUNE_MYSTERIES));
        this.add("Fletch an Oak Shortbow in the Gnome Stronghold.", new SkillRequirement(Skill.FLETCHING, 20));
        this.add("Take the agility shortcut from the Grand Tree to Otto's Grotto.", new SkillRequirement(Skill.AGILITY, 37), new QuestRequirement(Quest.TREE_GNOME_VILLAGE), new QuestRequirement(Quest.THE_GRAND_TREE));
        this.add("Travel to the Gnome Stronghold by Spirit Tree.", new QuestRequirement(Quest.TREE_GNOME_VILLAGE));
        this.add("Trap a Spined Larupia.", new SkillRequirement(Skill.HUNTER, 31));
        this.add("Fish some Bass on Ape Atoll.", new SkillRequirement(Skill.FISHING, 46), new QuestRequirement(Quest.MONKEY_MADNESS_I, true));
        this.add("Chop and burn some teak logs on Ape Atoll.", new SkillRequirement(Skill.WOODCUTTING, 35), new SkillRequirement(Skill.FIREMAKING, 35), new QuestRequirement(Quest.MONKEY_MADNESS_I, true));
        this.add("Complete an intermediate game of Pest Control.", new CombatLevelRequirement(70));
        this.add("Travel to the Feldip Hills by Gnome Glider.", new QuestRequirement(Quest.ONE_SMALL_FAVOUR), new QuestRequirement(Quest.THE_GRAND_TREE));
        this.add("Claim a Chompy bird hat from Rantz after registering at least 125 kills.", new QuestRequirement(Quest.BIG_CHOMPY_BIRD_HUNTING));
        this.add("Travel from Eagles' Peak to the Feldip Hills by Eagle.", new QuestRequirement(Quest.EAGLES_PEAK));
        this.add("Make a Chocolate Bomb at the Grand Tree.", new SkillRequirement(Skill.COOKING, 42));
        this.add("Complete a delivery for the Gnome Restaurant.", new SkillRequirement(Skill.COOKING, 29));
        this.add("Turn your small crystal seed into a Crystal saw.", new QuestRequirement(Quest.THE_EYES_OF_GLOUPHRIE));
        this.add("Mine some Gold ore underneath the Grand Tree.", new SkillRequirement(Skill.MINING, 40), new QuestRequirement(Quest.THE_GRAND_TREE));
        this.add("Kill an Elf with a Crystal bow.", new SkillRequirement(Skill.RANGED, 70), new SkillRequirement(Skill.AGILITY, 56), new QuestRequirement(Quest.ROVING_ELVES));
        this.add("Catch and cook a Monkfish in Piscatoris.", new SkillRequirement(Skill.FISHING, 62), new SkillRequirement(Skill.COOKING, 62), new QuestRequirement(Quest.SWAN_SONG));
        this.add("Complete a Veteran game of Pest Control.", new CombatLevelRequirement(100));
        this.add("Catch a Dashing Kebbit.", new SkillRequirement(Skill.HUNTER, 69));
        this.add("Complete a lap of the Ape Atoll agility course.", new SkillRequirement(Skill.AGILITY, 48), new QuestRequirement(Quest.MONKEY_MADNESS_I));
        this.add("Chop and burn some Mahogany logs on Ape Atoll.", new SkillRequirement(Skill.WOODCUTTING, 50), new SkillRequirement(Skill.FIREMAKING, 50), new QuestRequirement(Quest.MONKEY_MADNESS_I));
        this.add("Mine some Adamantite ore in Tirannwn.", new SkillRequirement(Skill.MINING, 70), new QuestRequirement(Quest.REGICIDE));
        this.add("Check the health of your Palm tree in Lletya.", new SkillRequirement(Skill.FARMING, 68), new QuestRequirement(Quest.MOURNINGS_END_PART_I, true));
        this.add("Claim a Chompy bird hat from Rantz after registering at least 300 kills.", new QuestRequirement(Quest.BIG_CHOMPY_BIRD_HUNTING));
        this.add("Build an Isafdar painting in your POH Quest hall.", new SkillRequirement(Skill.CONSTRUCTION, 65), new QuestRequirement(Quest.ROVING_ELVES));
        this.add("Kill Zulrah.", new QuestRequirement(Quest.REGICIDE, true));
        this.add("Teleport to Ape Atoll.", new SkillRequirement(Skill.MAGIC, 64), new QuestRequirement(Quest.RECIPE_FOR_DISASTER, true));
        this.add("Pickpocket a Gnome.", new SkillRequirement(Skill.THIEVING, 75));
        this.add("Fletch a Magic Longbow in Tirannwn.", new SkillRequirement(Skill.FLETCHING, 85), new QuestRequirement(Quest.MOURNINGS_END_PART_I));
        this.add("Kill the Thermonuclear Smoke devil (Does not require task).", new SkillRequirement(Skill.SLAYER, 93));
        this.add("Have Prissy Scilla protect your Magic tree.", new SkillRequirement(Skill.FARMING, 75));
        this.add("Use the Elven overpass advanced cliffside shortcut.", new SkillRequirement(Skill.AGILITY, 85), new QuestRequirement(Quest.UNDERGROUND_PASS));
        this.add("Claim a Chompy bird hat from Rantz after registering at least 1000 kills.", new QuestRequirement(Quest.BIG_CHOMPY_BIRD_HUNTING));
        this.add("Pickpocket an Elf.", new SkillRequirement(Skill.THIEVING, 85), new QuestRequirement(Quest.MOURNINGS_END_PART_I, true));
    }
}

