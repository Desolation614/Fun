/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.itemstats.stats;

import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.plugins.itemstats.stats.Stat;

public class SkillStat
extends Stat {
    private final Skill skill;

    SkillStat(Skill skill) {
        super(skill.getName());
        this.skill = skill;
    }

    @Override
    public int getValue(Client client) {
        return client.getBoostedSkillLevel(this.skill);
    }

    @Override
    public int getMaximum(Client client) {
        return client.getRealSkillLevel(this.skill);
    }
}

