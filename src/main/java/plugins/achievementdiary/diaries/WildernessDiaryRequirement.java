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
import net.runelite.client.plugins.achievementdiary.OrRequirement;
import net.runelite.client.plugins.achievementdiary.QuestRequirement;
import net.runelite.client.plugins.achievementdiary.SkillRequirement;

public class WildernessDiaryRequirement
extends GenericDiaryRequirement {
    public WildernessDiaryRequirement() {
        this.add("Cast Low Alchemy at the Fountain of Rune.", new SkillRequirement(Skill.MAGIC, 21));
        this.add("Kill an Earth Warrior in the Wilderness beneath Edgeville.", new SkillRequirement(Skill.AGILITY, 15));
        this.add("Mine some Iron ore in the Wilderness.", new SkillRequirement(Skill.MINING, 15));
        this.add("Have the Mage of Zamorak teleport you to the Abyss.", new QuestRequirement(Quest.ENTER_THE_ABYSS));
        this.add("Mine some Mithril ore in the wilderness.", new SkillRequirement(Skill.MINING, 55));
        this.add("Chop some yew logs from a fallen Ent.", new SkillRequirement(Skill.WOODCUTTING, 61));
        this.add("Enter the Wilderness Godwars Dungeon.", new OrRequirement(new SkillRequirement(Skill.AGILITY, 60), new SkillRequirement(Skill.STRENGTH, 60)));
        this.add("Complete a lap of the Wilderness Agility course.", new SkillRequirement(Skill.AGILITY, 52));
        this.add("Charge an Earth Orb.", new SkillRequirement(Skill.MAGIC, 60));
        this.add("Kill a Bloodveld in the Wilderness Godwars Dungeon.", new SkillRequirement(Skill.SLAYER, 50));
        this.add("Smith a Golden helmet in the Resource Area.", new SkillRequirement(Skill.SMITHING, 50), new QuestRequirement(Quest.BETWEEN_A_ROCK, true));
        this.add("Cast one of the 3 God spells against another player in the Wilderness.", new SkillRequirement(Skill.MAGIC, 60), new QuestRequirement(Quest.MAGE_ARENA_I));
        this.add("Charge an Air Orb.", new SkillRequirement(Skill.MAGIC, 66));
        this.add("Catch a Black Salamander in the Wilderness.", new SkillRequirement(Skill.HUNTER, 67));
        this.add("Smith an Adamant scimitar in the Resource Area.", new SkillRequirement(Skill.SMITHING, 75));
        this.add("Take the agility shortcut from Trollheim into the Wilderness.", new SkillRequirement(Skill.AGILITY, 64), new QuestRequirement(Quest.DEATH_PLATEAU));
        this.add("Kill a Spiritual warrior in the Wilderness Godwars Dungeon.", new SkillRequirement(Skill.SLAYER, 68));
        this.add("Fish some Raw Lava Eel in the Wilderness.", new SkillRequirement(Skill.FISHING, 53));
        this.add("Teleport to Ghorrock.", new SkillRequirement(Skill.MAGIC, 96), new QuestRequirement(Quest.DESERT_TREASURE_I));
        this.add("Fish and Cook a Dark Crab in the Resource Area.", new SkillRequirement(Skill.FISHING, 85), new SkillRequirement(Skill.COOKING, 90));
        this.add("Smith a rune scimitar from scratch in the Resource Area.", new SkillRequirement(Skill.MINING, 85), new SkillRequirement(Skill.SMITHING, 90));
        this.add("Steal from the Rogues' chest.", new SkillRequirement(Skill.THIEVING, 84));
        this.add("Slay a spiritual mage inside the wilderness Godwars Dungeon.", new SkillRequirement(Skill.SLAYER, 83), new OrRequirement(new SkillRequirement(Skill.AGILITY, 60), new SkillRequirement(Skill.STRENGTH, 60)));
        this.add("Cut and burn some magic logs in the Resource Area.", new SkillRequirement(Skill.WOODCUTTING, 75), new SkillRequirement(Skill.FIREMAKING, 75));
    }
}

