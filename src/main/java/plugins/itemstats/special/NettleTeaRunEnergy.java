/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.itemstats.special;

import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.plugins.itemstats.StatBoost;
import net.runelite.client.plugins.itemstats.stats.Stats;

public class NettleTeaRunEnergy
extends StatBoost {
    public NettleTeaRunEnergy() {
        super(Stats.RUN_ENERGY, false);
    }

    @Override
    public int heals(Client client) {
        if (client.getBoostedSkillLevel(Skill.HITPOINTS) < client.getRealSkillLevel(Skill.HITPOINTS)) {
            return 5;
        }
        return 0;
    }
}

