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
import net.runelite.client.plugins.achievementdiary.OrRequirement;
import net.runelite.client.plugins.achievementdiary.QuestRequirement;
import net.runelite.client.plugins.achievementdiary.SkillRequirement;

public class KaramjaDiaryRequirement
extends GenericDiaryRequirement {
    public KaramjaDiaryRequirement() {
        this.add("Use the rope swing to travel to the small island north-west of Karamja, where the moss giants are.", new SkillRequirement(Skill.AGILITY, 10));
        this.add("Mine some gold from the rocks on the north-west peninsula of Karamja.", new SkillRequirement(Skill.MINING, 40));
        this.add("Explore Cairn Island to the west of Karamja.", new SkillRequirement(Skill.AGILITY, 15));
        this.add("Claim a ticket from the Agility Arena in Brimhaven.", new SkillRequirement(Skill.AGILITY, 30));
        this.add("Discover hidden wall in the dungeon below the volcano.", new QuestRequirement(Quest.DRAGON_SLAYER_I, true));
        this.add("Visit the Isle of Crandor via the dungeon below the volcano.", new QuestRequirement(Quest.DRAGON_SLAYER_I, true));
        this.add("Use Vigroy and Hajedy's cart service.", new QuestRequirement(Quest.SHILO_VILLAGE));
        this.add("Earn 100% favour in the village of Tai Bwo Wannai.", new SkillRequirement(Skill.WOODCUTTING, 10), new QuestRequirement(Quest.JUNGLE_POTION));
        this.add("Cook a spider on a stick.", new SkillRequirement(Skill.COOKING, 16));
        this.add("Charter the Lady of the Waves from Cairn Isle to Port Khazard.", new QuestRequirement(Quest.SHILO_VILLAGE));
        this.add("Cut a log from a teak tree.", new SkillRequirement(Skill.WOODCUTTING, 35), new QuestRequirement(Quest.JUNGLE_POTION));
        this.add("Cut a log from a mahogany tree.", new SkillRequirement(Skill.WOODCUTTING, 50), new QuestRequirement(Quest.JUNGLE_POTION));
        this.add("Catch a karambwan.", new SkillRequirement(Skill.FISHING, 65), new QuestRequirement(Quest.TAI_BWO_WANNAI_TRIO, true));
        this.add("Exchange gems for a machete.", new QuestRequirement(Quest.JUNGLE_POTION));
        this.add("Use the gnome glider to travel to Karamja.", new QuestRequirement(Quest.THE_GRAND_TREE));
        this.add("Grow a healthy fruit tree in the patch near Brimhaven.", new SkillRequirement(Skill.FARMING, 27));
        this.add("Trap a horned graahk.", new SkillRequirement(Skill.HUNTER, 41));
        this.add("Chop the vines to gain deeper access to Brimhaven Dungeon.", new SkillRequirement(Skill.WOODCUTTING, 10));
        this.add("Cross the lava using the stepping stones within Brimhaven Dungeon.", new SkillRequirement(Skill.AGILITY, 12));
        this.add("Climb the stairs within Brimhaven Dungeon.", new SkillRequirement(Skill.WOODCUTTING, 10));
        this.add("Charter a ship from the shipyard in the far east of Karamja.", new QuestRequirement(Quest.THE_GRAND_TREE));
        this.add("Mine a red topaz from a gem rock.", new SkillRequirement(Skill.MINING, 40), new OrRequirement(new QuestRequirement(Quest.SHILO_VILLAGE), new QuestRequirement(Quest.JUNGLE_POTION)));
        this.add("Craft some nature runes from Essence.", new SkillRequirement(Skill.RUNECRAFT, 44));
        this.add("Cook a karambwan thoroughly.", new SkillRequirement(Skill.COOKING, 30), new QuestRequirement(Quest.TAI_BWO_WANNAI_TRIO));
        this.add("Kill a deathwing in the dungeon under the Kharazi Jungle.", new SkillRequirement(Skill.WOODCUTTING, 15), new SkillRequirement(Skill.STRENGTH, 50), new SkillRequirement(Skill.AGILITY, 50), new SkillRequirement(Skill.THIEVING, 50), new SkillRequirement(Skill.MINING, 52), new QuestRequirement(Quest.LEGENDS_QUEST));
        this.add("Use the crossbow shortcut south of the volcano.", new SkillRequirement(Skill.AGILITY, 53), new SkillRequirement(Skill.RANGED, 42), new SkillRequirement(Skill.STRENGTH, 21));
        this.add("Collect 5 palm leaves.", new SkillRequirement(Skill.WOODCUTTING, 15), new QuestRequirement(Quest.LEGENDS_QUEST));
        this.add("Be assigned a Slayer task by the Slayer Master in Shilo Village.", new CombatLevelRequirement(100), new SkillRequirement(Skill.SLAYER, 50), new QuestRequirement(Quest.SHILO_VILLAGE));
        this.add("Craft 56 Nature runes simultaneously from Essence without the use of Extracts.", new SkillRequirement(Skill.RUNECRAFT, 91));
        this.add("Check the health of a palm tree in Brimhaven.", new SkillRequirement(Skill.FARMING, 68));
        this.add("Create an antivenom potion whilst standing in the horse shoe mine.", new SkillRequirement(Skill.HERBLORE, 87));
        this.add("Check the health of your Calquat tree patch.", new SkillRequirement(Skill.FARMING, 72));
    }
}

