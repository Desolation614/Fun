/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.skillcalculator;

import javax.annotation.Nullable;
import net.runelite.api.Skill;
import net.runelite.client.plugins.skillcalculator.skills.AgilityAction;
import net.runelite.client.plugins.skillcalculator.skills.AgilityBonus;
import net.runelite.client.plugins.skillcalculator.skills.ConstructionAction;
import net.runelite.client.plugins.skillcalculator.skills.ConstructionBonus;
import net.runelite.client.plugins.skillcalculator.skills.CookingAction;
import net.runelite.client.plugins.skillcalculator.skills.CraftingAction;
import net.runelite.client.plugins.skillcalculator.skills.FarmingAction;
import net.runelite.client.plugins.skillcalculator.skills.FarmingBonus;
import net.runelite.client.plugins.skillcalculator.skills.FiremakingAction;
import net.runelite.client.plugins.skillcalculator.skills.FiremakingBonus;
import net.runelite.client.plugins.skillcalculator.skills.FishingAction;
import net.runelite.client.plugins.skillcalculator.skills.FishingBonus;
import net.runelite.client.plugins.skillcalculator.skills.FletchingAction;
import net.runelite.client.plugins.skillcalculator.skills.HerbloreAction;
import net.runelite.client.plugins.skillcalculator.skills.HunterAction;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import net.runelite.client.plugins.skillcalculator.skills.MiningAction;
import net.runelite.client.plugins.skillcalculator.skills.MiningBonus;
import net.runelite.client.plugins.skillcalculator.skills.PrayerAction;
import net.runelite.client.plugins.skillcalculator.skills.PrayerBonus;
import net.runelite.client.plugins.skillcalculator.skills.RunecraftAction;
import net.runelite.client.plugins.skillcalculator.skills.RunecraftBonus;
import net.runelite.client.plugins.skillcalculator.skills.SkillAction;
import net.runelite.client.plugins.skillcalculator.skills.SkillBonus;
import net.runelite.client.plugins.skillcalculator.skills.SmithingAction;
import net.runelite.client.plugins.skillcalculator.skills.SmithingBonus;
import net.runelite.client.plugins.skillcalculator.skills.ThievingAction;
import net.runelite.client.plugins.skillcalculator.skills.WoodcuttingAction;
import net.runelite.client.plugins.skillcalculator.skills.WoodcuttingBonus;

enum CalculatorType {
    MINING(Skill.MINING, MiningBonus.values(), MiningAction.values()),
    AGILITY(Skill.AGILITY, AgilityBonus.values(), AgilityAction.values()),
    SMITHING(Skill.SMITHING, SmithingBonus.values(), SmithingAction.values()),
    HERBLORE(Skill.HERBLORE, null, HerbloreAction.values()),
    FISHING(Skill.FISHING, FishingBonus.values(), FishingAction.values()),
    THIEVING(Skill.THIEVING, null, ThievingAction.values()),
    COOKING(Skill.COOKING, null, CookingAction.values()),
    PRAYER(Skill.PRAYER, PrayerBonus.values(), PrayerAction.values()),
    CRAFTING(Skill.CRAFTING, null, CraftingAction.values()),
    FIREMAKING(Skill.FIREMAKING, FiremakingBonus.values(), FiremakingAction.values()),
    MAGIC(Skill.MAGIC, null, MagicAction.values()),
    FLETCHING(Skill.FLETCHING, null, FletchingAction.values()),
    WOODCUTTING(Skill.WOODCUTTING, WoodcuttingBonus.values(), WoodcuttingAction.values()),
    RUNECRAFT(Skill.RUNECRAFT, RunecraftBonus.values(), RunecraftAction.values()),
    FARMING(Skill.FARMING, FarmingBonus.values(), FarmingAction.values()),
    CONSTRUCTION(Skill.CONSTRUCTION, ConstructionBonus.values(), ConstructionAction.values()),
    HUNTER(Skill.HUNTER, null, HunterAction.values());

    private final Skill skill;
    @Nullable
    private final SkillBonus[] skillBonuses;
    private final SkillAction[] skillActions;

    private CalculatorType(Skill skill, SkillBonus[] skillBonuses, SkillAction[] skillActions) {
        this.skill = skill;
        this.skillBonuses = skillBonuses;
        this.skillActions = skillActions;
    }

    public Skill getSkill() {
        return this.skill;
    }

    @Nullable
    public SkillBonus[] getSkillBonuses() {
        return this.skillBonuses;
    }

    public SkillAction[] getSkillActions() {
        return this.skillActions;
    }
}

