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

public class FaladorDiaryRequirement
extends GenericDiaryRequirement {
    public FaladorDiaryRequirement() {
        this.add("Find out what your family crest is from Sir Renitee.", new SkillRequirement(Skill.CONSTRUCTION, 16));
        this.add("Climb over the western Falador wall.", new SkillRequirement(Skill.AGILITY, 5));
        this.add("Smith some Blurite Limbs on Doric's Anvil.", new SkillRequirement(Skill.MINING, 10), new SkillRequirement(Skill.SMITHING, 13), new QuestRequirement(Quest.THE_KNIGHTS_SWORD), new QuestRequirement(Quest.DORICS_QUEST));
        this.add("Light a Bullseye lantern at the Chemist's in Rimmington.", new SkillRequirement(Skill.FIREMAKING, 49));
        this.add("Telegrab some Wine of Zamorak at the Chaos Temple by the Wilderness.", new SkillRequirement(Skill.MAGIC, 33));
        this.add("Place a Scarecrow in the Falador farming patch.", new SkillRequirement(Skill.FARMING, 23));
        this.add("Kill a Mogre at Mudskipper Point.", new SkillRequirement(Skill.SLAYER, 32), new QuestRequirement(Quest.SKIPPY_AND_THE_MOGRES));
        this.add("Visit the Port Sarim Rat Pits.", new QuestRequirement(Quest.RATCATCHERS, true));
        this.add("Grapple up and then jump off the north Falador wall.", new SkillRequirement(Skill.AGILITY, 11), new SkillRequirement(Skill.STRENGTH, 37), new SkillRequirement(Skill.RANGED, 19));
        this.add("Pickpocket a Falador guard.", new SkillRequirement(Skill.THIEVING, 40));
        this.add("Pray at the Altar of Guthix in Taverley whilst wearing full Initiate.", new SkillRequirement(Skill.PRAYER, 10), new SkillRequirement(Skill.DEFENCE, 20), new QuestRequirement(Quest.RECRUITMENT_DRIVE));
        this.add("Mine some Gold ore at the Crafting Guild.", new SkillRequirement(Skill.CRAFTING, 40), new SkillRequirement(Skill.MINING, 40));
        this.add("Squeeze through the crevice in the Dwarven mines.", new SkillRequirement(Skill.AGILITY, 42));
        this.add("Chop and burn some Willow logs in Taverley", new SkillRequirement(Skill.WOODCUTTING, 30), new SkillRequirement(Skill.FIREMAKING, 30));
        this.add("Craft a fruit basket on the Falador Farm loom.", new SkillRequirement(Skill.CRAFTING, 36));
        this.add("Teleport to Falador.", new SkillRequirement(Skill.MAGIC, 37));
        this.add("Craft 140 Mind runes simultaneously from Essence without the use of Extracts.", new SkillRequirement(Skill.RUNECRAFT, 56));
        this.add("Change your family crest to the Saradomin symbol.", new SkillRequirement(Skill.PRAYER, 70));
        this.add("Kill a Skeletal Wyvern in the Asgarnia Ice Dungeon.", new SkillRequirement(Skill.SLAYER, 72));
        this.add("Complete a lap of the Falador rooftop agility course.", new SkillRequirement(Skill.AGILITY, 50));
        this.add("Enter the mining guild wearing a Prospector helmet.", new SkillRequirement(Skill.MINING, 60));
        this.add("Kill the Blue Dragon under the Heroes' Guild.", new QuestRequirement(Quest.HEROES_QUEST));
        this.add("Crack a wall safe within Rogues Den.", new SkillRequirement(Skill.THIEVING, 50));
        this.add("Recharge your prayer in the Port Sarim church while wearing full Proselyte.", new SkillRequirement(Skill.DEFENCE, 30), new QuestRequirement(Quest.THE_SLUG_MENACE));
        this.add("Equip a dwarven helmet within the dwarven mines.", new SkillRequirement(Skill.DEFENCE, 50), new QuestRequirement(Quest.GRIM_TALES));
        this.add("Craft 252 Air Runes simultaneously from Essence without the use of Extracts.", new SkillRequirement(Skill.RUNECRAFT, 88));
        this.add("Purchase a White 2h Sword from Sir Vyvin.", new QuestRequirement(Quest.WANTED));
        this.add("Find at least 3 magic roots at once when digging up your magic tree in Falador.", new SkillRequirement(Skill.FARMING, 91), new SkillRequirement(Skill.WOODCUTTING, 75));
        this.add("Jump over the strange floor in Taverley dungeon.", new SkillRequirement(Skill.AGILITY, 80));
        this.add("Mix a Saradomin brew in Falador east bank.", new SkillRequirement(Skill.HERBLORE, 81));
    }
}

