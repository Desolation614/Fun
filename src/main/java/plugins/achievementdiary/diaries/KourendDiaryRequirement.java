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

public class KourendDiaryRequirement
extends GenericDiaryRequirement {
    public KourendDiaryRequirement() {
        this.add("Mine some Iron at the Mount Karuulm mine.", new SkillRequirement(Skill.MINING, 15));
        this.add("Steal from a Hosidius Food Stall.", new SkillRequirement(Skill.THIEVING, 25));
        this.add("Enter your Player Owned House from Hosidius.", new SkillRequirement(Skill.CONSTRUCTION, 25));
        this.add("Create a Strength potion in the Lovakengj Pub.", new SkillRequirement(Skill.HERBLORE, 12));
        this.add("Fish a Trout from the River Molch.", new SkillRequirement(Skill.FISHING, 20));
        this.add("Travel to the Fairy Ring south of Mount Karuulm.", new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, true));
        this.add("Use Kharedst's memoirs to teleport to all five cities in Great Kourend.", new QuestRequirement(Quest.THE_DEPTHS_OF_DESPAIR), new QuestRequirement(Quest.THE_QUEEN_OF_THIEVES), new QuestRequirement(Quest.TALE_OF_THE_RIGHTEOUS), new QuestRequirement(Quest.THE_FORSAKEN_TOWER), new QuestRequirement(Quest.THE_ASCENT_OF_ARCEUUS));
        this.add("Mine some Volcanic sulphur.", new SkillRequirement(Skill.MINING, 42));
        this.add("Enter the Farming Guild.", new SkillRequirement(Skill.FARMING, 45));
        this.add("Repair a Piscarilius crane.", new SkillRequirement(Skill.CRAFTING, 30), new SkillRequirement(Skill.CONSTRUCTION, 30));
        this.add("Catch a Bluegill on Molch Island.", new SkillRequirement(Skill.FISHING, 43), new SkillRequirement(Skill.HUNTER, 35));
        this.add("Use the boulder leap in the Arceuus essence mine.", new SkillRequirement(Skill.AGILITY, 49));
        this.add("Subdue the Wintertodt.", new SkillRequirement(Skill.FIREMAKING, 50));
        this.add("Catch a Chinchompa in the Kourend Woodland.", new SkillRequirement(Skill.HUNTER, 53), new QuestRequirement(Quest.EAGLES_PEAK));
        this.add("Chop some Mahogany logs north of the Farming Guild.", new SkillRequirement(Skill.WOODCUTTING, 50));
        this.add("Enter the Woodcutting Guild.", new SkillRequirement(Skill.WOODCUTTING, 60));
        this.add("Smelt an Adamantite bar in The Forsaken Tower.", new SkillRequirement(Skill.SMITHING, 70), new QuestRequirement(Quest.THE_FORSAKEN_TOWER, true));
        this.add("Mine some Lovakite.", new SkillRequirement(Skill.MINING, 65));
        this.add("Plant some Logavano seeds at the Tithe Farm.", new SkillRequirement(Skill.FARMING, 74));
        this.add("Deliver an artefact to Captain Khaled.", new SkillRequirement(Skill.THIEVING, 49));
        this.add("Kill a Wyrm in the Karuulm Slayer Dungeon.", new SkillRequirement(Skill.SLAYER, 62));
        this.add("Cast Monster Examine on a Troll south of Mount Quidamortem.", new SkillRequirement(Skill.MAGIC, 66), new QuestRequirement(Quest.DREAM_MENTOR));
        this.add("Craft one or more Blood runes from Dark essence fragments.", new SkillRequirement(Skill.RUNECRAFT, 77), new SkillRequirement(Skill.MINING, 38), new SkillRequirement(Skill.CRAFTING, 38));
        this.add("Chop some Redwood logs.", new SkillRequirement(Skill.WOODCUTTING, 90));
        this.add("Catch an Anglerfish and cook it whilst in Great Kourend.", new SkillRequirement(Skill.FISHING, 82), new SkillRequirement(Skill.COOKING, 84));
        this.add("Kill a Hydra in the Karuulm Slayer Dungeon.", new SkillRequirement(Skill.SLAYER, 95));
        this.add("Create an Ape Atoll teleport tablet.", new SkillRequirement(Skill.MAGIC, 90), new SkillRequirement(Skill.MINING, 38), new SkillRequirement(Skill.CRAFTING, 38));
        this.add("Create your own Battlestaff from scratch within the Farming Guild.", new SkillRequirement(Skill.FARMING, 85), new SkillRequirement(Skill.FLETCHING, 40));
    }
}

