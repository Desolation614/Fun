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

public class LumbridgeDiaryRequirement
extends GenericDiaryRequirement {
    public LumbridgeDiaryRequirement() {
        this.add("Slay a Cave bug beneath Lumbridge Swamp.", new SkillRequirement(Skill.SLAYER, 7));
        this.add("Have Sedridor teleport you to the Essence Mine.", new QuestRequirement(Quest.RUNE_MYSTERIES));
        this.add("Craft some water runes from Essence.", new SkillRequirement(Skill.RUNECRAFT, 5));
        this.add("Chop and burn some oak logs in Lumbridge.", new SkillRequirement(Skill.WOODCUTTING, 15), new SkillRequirement(Skill.FIREMAKING, 15));
        this.add("Catch some Anchovies in Al Kharid.", new SkillRequirement(Skill.FISHING, 15));
        this.add("Bake some Bread on the Lumbridge kitchen range.", new QuestRequirement(Quest.COOKS_ASSISTANT));
        this.add("Mine some Iron ore at the Al Kharid mine.", new SkillRequirement(Skill.MINING, 15));
        this.add("Complete a lap of the Al Kharid agility course.", new SkillRequirement(Skill.AGILITY, 20));
        this.add("Grapple across the River Lum.", new SkillRequirement(Skill.AGILITY, 8), new SkillRequirement(Skill.STRENGTH, 19), new SkillRequirement(Skill.RANGED, 37));
        this.add("Purchase an upgraded device from Ava.", new SkillRequirement(Skill.RANGED, 50), new QuestRequirement(Quest.ANIMAL_MAGNETISM));
        this.add("Travel to the Wizards' Tower by Fairy ring.", new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, true));
        this.add("Cast the teleport to Lumbridge spell.", new SkillRequirement(Skill.MAGIC, 31));
        this.add("Catch some Salmon in Lumbridge.", new SkillRequirement(Skill.FISHING, 30));
        this.add("Craft a coif in the Lumbridge cow pen.", new SkillRequirement(Skill.CRAFTING, 38));
        this.add("Chop some willow logs in Draynor Village.", new SkillRequirement(Skill.WOODCUTTING, 30));
        this.add("Pickpocket Martin the Master Gardener.", new SkillRequirement(Skill.THIEVING, 38));
        this.add("Get a slayer task from Chaeldar.", new CombatLevelRequirement(70), new QuestRequirement(Quest.LOST_CITY));
        this.add("Catch an Essence or Eclectic impling in Puro-Puro.", new SkillRequirement(Skill.HUNTER, 42), new QuestRequirement(Quest.LOST_CITY));
        this.add("Craft some Lava runes at the fire altar in Al Kharid.", new SkillRequirement(Skill.RUNECRAFT, 23));
        this.add("Cast Bones to Peaches in Al Kharid palace.", new SkillRequirement(Skill.MAGIC, 60));
        this.add("Squeeze past the jutting wall on your way to the cosmic altar.", new SkillRequirement(Skill.AGILITY, 46), new QuestRequirement(Quest.LOST_CITY));
        this.add("Craft 56 Cosmic runes simultaneously from Essence without the use of Extracts.", new SkillRequirement(Skill.RUNECRAFT, 59), new QuestRequirement(Quest.LOST_CITY));
        this.add("Travel from Lumbridge to Edgeville on a Waka Canoe.", new SkillRequirement(Skill.WOODCUTTING, 57));
        this.add("Collect at least 100 Tears of Guthix in one visit.", new QuestRequirement(Quest.TEARS_OF_GUTHIX));
        this.add("Take the train from Dorgesh-Kaan to Keldagrim.", new QuestRequirement(Quest.ANOTHER_SLICE_OF_HAM));
        this.add("Purchase some Barrows gloves from the Lumbridge bank chest.", new QuestRequirement(Quest.RECIPE_FOR_DISASTER));
        this.add("Pick some Belladonna from the farming patch at Draynor Manor.", new SkillRequirement(Skill.FARMING, 63));
        this.add("Light your mining helmet in the Lumbridge castle basement.", new SkillRequirement(Skill.FIREMAKING, 65));
        this.add("Recharge your prayer at the Emir's Arena with Smite activated.", new SkillRequirement(Skill.PRAYER, 52));
        this.add("Craft, string and enchant an Amulet of Power in Lumbridge.", new SkillRequirement(Skill.CRAFTING, 70), new SkillRequirement(Skill.MAGIC, 57));
        this.add("Steal from a Dorgesh-Kaan rich chest.", new SkillRequirement(Skill.THIEVING, 78), new QuestRequirement(Quest.DEATH_TO_THE_DORGESHUUN));
        this.add("Grapple across a pylon on the Dorgesh-Kaan Agility Course.", new SkillRequirement(Skill.AGILITY, 70), new SkillRequirement(Skill.RANGED, 70), new SkillRequirement(Skill.STRENGTH, 70), new QuestRequirement(Quest.DEATH_TO_THE_DORGESHUUN));
        this.add("Chop some magic logs at the Mage Training Arena.", new SkillRequirement(Skill.WOODCUTTING, 75));
        this.add("Smith an Adamant platebody down Draynor sewer.", new SkillRequirement(Skill.SMITHING, 88));
        this.add("Craft 140 or more Water runes simultaneously from Essence without the use of Extracts.", new SkillRequirement(Skill.RUNECRAFT, 76));
    }
}

