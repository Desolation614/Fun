/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.xptracker;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Skill;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.plugins.xptracker.XpTrackerService;

@Singleton
class XpTrackerServiceImpl
implements XpTrackerService {
    private final XpTrackerPlugin plugin;

    @Inject
    XpTrackerServiceImpl(XpTrackerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getActions(Skill skill) {
        return this.plugin.getSkillSnapshot(skill).getActionsInSession();
    }

    @Override
    public int getActionsHr(Skill skill) {
        return this.plugin.getSkillSnapshot(skill).getActionsPerHour();
    }

    @Override
    public int getActionsLeft(Skill skill) {
        return this.plugin.getSkillSnapshot(skill).getActionsRemainingToGoal();
    }

    @Override
    public int getXpHr(Skill skill) {
        return this.plugin.getSkillSnapshot(skill).getXpPerHour();
    }

    @Override
    public int getStartGoalXp(Skill skill) {
        return this.plugin.getSkillSnapshot(skill).getStartGoalXp();
    }

    @Override
    public int getEndGoalXp(Skill skill) {
        return this.plugin.getSkillSnapshot(skill).getEndGoalXp();
    }

    @Override
    public String getTimeTilGoal(Skill skill) {
        return this.plugin.getSkillSnapshot(skill).getTimeTillGoalShort();
    }
}

