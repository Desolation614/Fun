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

public class KandarinDiaryRequirement
extends GenericDiaryRequirement {
    public KandarinDiaryRequirement() {
        this.add("Catch a Mackerel at Catherby.", new SkillRequirement(Skill.FISHING, 16));
        this.add("Plant some Jute seeds in the patch north of McGrubor's Wood.", new SkillRequirement(Skill.FARMING, 13));
        this.add("Defeat one of each elemental in the workshop.", new QuestRequirement(Quest.ELEMENTAL_WORKSHOP_I, true));
        this.add("Cross the Coal truck log shortcut.", new SkillRequirement(Skill.AGILITY, 20));
        this.add("Complete a lap of the Barbarian agility course.", new SkillRequirement(Skill.AGILITY, 35), new QuestRequirement(Quest.ALFRED_GRIMHANDS_BARCRAWL));
        this.add("Create a Super Antipoison potion from scratch in the Seers/Catherby Area.", new SkillRequirement(Skill.HERBLORE, 48));
        this.add("Enter the Ranging guild.", new SkillRequirement(Skill.RANGED, 40));
        this.add("Use the grapple shortcut to get from the water obelisk to Catherby shore.", new SkillRequirement(Skill.AGILITY, 36), new SkillRequirement(Skill.STRENGTH, 22), new SkillRequirement(Skill.RANGED, 39));
        this.add("Catch and cook a Bass in Catherby.", new SkillRequirement(Skill.FISHING, 46), new SkillRequirement(Skill.COOKING, 43));
        this.add("Teleport to Camelot.", new SkillRequirement(Skill.MAGIC, 45));
        this.add("String a Maple shortbow in Seers' Village bank.", new SkillRequirement(Skill.FLETCHING, 50));
        this.add("Pick some Limpwurt root from the farming patch in Catherby.", new SkillRequirement(Skill.FARMING, 26));
        this.add("Create a Mind helmet.", new QuestRequirement(Quest.ELEMENTAL_WORKSHOP_II));
        this.add("Kill a Fire Giant inside Baxtorian Waterfall.", new QuestRequirement(Quest.WATERFALL_QUEST, true));
        this.add("Steal from the chest in Hemenster.", new SkillRequirement(Skill.THIEVING, 47));
        this.add("Travel to McGrubor's Wood by Fairy Ring.", new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, true));
        this.add("Mine some coal near the coal trucks.", new SkillRequirement(Skill.MINING, 30));
        this.add("Catch a Leaping Sturgeon.", new SkillRequirement(Skill.FISHING, 70), new SkillRequirement(Skill.AGILITY, 45), new SkillRequirement(Skill.STRENGTH, 45));
        this.add("Complete a lap of the Seers' Village agility course.", new SkillRequirement(Skill.AGILITY, 60));
        this.add("Create a Yew Longbow from scratch around Seers' Village.", new SkillRequirement(Skill.WOODCUTTING, 60), new SkillRequirement(Skill.FLETCHING, 70), new SkillRequirement(Skill.CRAFTING, 10));
        this.add("Enter the Seers' Village courthouse with piety turned on.", new SkillRequirement(Skill.PRAYER, 70), new SkillRequirement(Skill.DEFENCE, 70), new QuestRequirement(Quest.KINGS_RANSOM));
        this.add("Charge a Water Orb.", new SkillRequirement(Skill.MAGIC, 56));
        this.add("Burn some Maple logs with a bow in Seers' Village.", new SkillRequirement(Skill.FIREMAKING, 65));
        this.add("Kill a Shadow Hound in the Shadow dungeon.", new SkillRequirement(Skill.THIEVING, 53), new QuestRequirement(Quest.DESERT_TREASURE_I, true));
        this.add("Purchase and equip a granite body from Barbarian Assault.", new SkillRequirement(Skill.STRENGTH, 50), new SkillRequirement(Skill.DEFENCE, 50));
        this.add("Have the Seers' estate agent decorate your house with Fancy Stone.", new SkillRequirement(Skill.CONSTRUCTION, 50));
        this.add("Smith an Adamant spear at Otto's Grotto.", new SkillRequirement(Skill.SMITHING, 75), new QuestRequirement(Quest.TAI_BWO_WANNAI_TRIO));
        this.add("Pick some Dwarf weed from the herb patch at Catherby.", new SkillRequirement(Skill.FARMING, 79));
        this.add("Fish and Cook 5 Sharks in Catherby using the Cooking gauntlets.", new SkillRequirement(Skill.FISHING, 76), new SkillRequirement(Skill.COOKING, 80), new QuestRequirement(Quest.FAMILY_CREST));
        this.add("Mix a Stamina Mix on top of the Seers' Village bank.", new SkillRequirement(Skill.HERBLORE, 86), new SkillRequirement(Skill.AGILITY, 60));
        this.add("Smith a Rune Hasta at Otto's Grotto.", new SkillRequirement(Skill.SMITHING, 90));
        this.add("Construct a Pyre ship from Magic Logs.(Requires Chewed Bones.)", new SkillRequirement(Skill.FIREMAKING, 85), new SkillRequirement(Skill.CRAFTING, 85));
        this.add("Teleport to Catherby.", new SkillRequirement(Skill.MAGIC, 87), new QuestRequirement(Quest.LUNAR_DIPLOMACY));
    }
}

