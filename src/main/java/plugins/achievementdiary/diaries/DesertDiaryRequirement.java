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

public class DesertDiaryRequirement
extends GenericDiaryRequirement {
    public DesertDiaryRequirement() {
        this.add("Catch a Golden Warbler.", new SkillRequirement(Skill.HUNTER, 5));
        this.add("Mine 5 clay in the north-eastern desert.", new SkillRequirement(Skill.MINING, 5));
        this.add("Open the Sarcophagus in the first room of Pyramid Plunder.", new SkillRequirement(Skill.THIEVING, 21), new QuestRequirement(Quest.ICTHLARINS_LITTLE_HELPER, true));
        this.add("Climb to the summit of the Agility Pyramid.", new SkillRequirement(Skill.AGILITY, 30));
        this.add("Slay a desert lizard.", new SkillRequirement(Skill.SLAYER, 22));
        this.add("Catch an Orange Salamander.", new SkillRequirement(Skill.HUNTER, 47));
        this.add("Steal a feather from the Desert Phoenix.", new SkillRequirement(Skill.THIEVING, 25));
        this.add("Travel to Uzer via Magic Carpet.", new QuestRequirement(Quest.THE_GOLEM));
        this.add("Travel to the Desert via Eagle.", new QuestRequirement(Quest.EAGLES_PEAK));
        this.add("Pray at the Elidinis statuette in Nardah.", new QuestRequirement(Quest.SPIRITS_OF_THE_ELID));
        this.add("Create a combat potion in the desert.", new SkillRequirement(Skill.HERBLORE, 36));
        this.add("Teleport to Enakhra's Temple with the Camulet.", new QuestRequirement(Quest.ENAKHRAS_LAMENT));
        this.add("Visit the Genie.", new QuestRequirement(Quest.SPIRITS_OF_THE_ELID));
        this.add("Teleport to Pollnivneach with a redirected teleport to house tablet.", new SkillRequirement(Skill.CONSTRUCTION, 20));
        this.add("Chop some Teak logs near Uzer.", new SkillRequirement(Skill.WOODCUTTING, 35));
        this.add("Knock out and pickpocket a Menaphite Thug.", new SkillRequirement(Skill.THIEVING, 65), new QuestRequirement(Quest.THE_FEUD));
        this.add("Mine some Granite.", new SkillRequirement(Skill.MINING, 45));
        this.add("Refill your waterskins in the Desert using Lunar magic.", new SkillRequirement(Skill.MAGIC, 68), new QuestRequirement(Quest.DREAM_MENTOR));
        this.add("Complete a lap of the Pollnivneach agility course.", new SkillRequirement(Skill.AGILITY, 70));
        this.add("Slay a Dust Devil in the desert cave with a Slayer helmet equipped.", new SkillRequirement(Skill.SLAYER, 65), new SkillRequirement(Skill.DEFENCE, 10), new SkillRequirement(Skill.CRAFTING, 55), new QuestRequirement(Quest.DESERT_TREASURE_I, true));
        this.add("Activate Ancient Magicks at the altar in the Jaldraocht Pyramid.", new QuestRequirement(Quest.DESERT_TREASURE_I));
        this.add("Defeat a Locust Rider with Keris.", new SkillRequirement(Skill.ATTACK, 50), new QuestRequirement(Quest.CONTACT));
        this.add("Burn some yew logs on the Nardah Mayor's balcony.", new SkillRequirement(Skill.FIREMAKING, 60));
        this.add("Create a Mithril Platebody in Nardah.", new SkillRequirement(Skill.SMITHING, 68));
        this.add("Bake a wild pie at the Nardah Clay Oven.", new SkillRequirement(Skill.COOKING, 85));
        this.add("Cast Ice Barrage against a foe in the Desert.", new SkillRequirement(Skill.MAGIC, 94), new QuestRequirement(Quest.DESERT_TREASURE_I));
        this.add("Fletch some Dragon darts at the Bedabin Camp.", new SkillRequirement(Skill.FLETCHING, 95), new QuestRequirement(Quest.THE_TOURIST_TRAP));
        this.add("Speak to the KQ head in your POH.", new SkillRequirement(Skill.CONSTRUCTION, 78), new QuestRequirement(Quest.PRIEST_IN_PERIL));
        this.add("Steal from the Grand Gold Chest in the final room of Pyramid Plunder.", new SkillRequirement(Skill.THIEVING, 91), new QuestRequirement(Quest.ICTHLARINS_LITTLE_HELPER, true));
        this.add("Restore at least 85 Prayer points when praying at the Altar in Sophanem.", new SkillRequirement(Skill.PRAYER, 85), new QuestRequirement(Quest.ICTHLARINS_LITTLE_HELPER, true));
    }
}

