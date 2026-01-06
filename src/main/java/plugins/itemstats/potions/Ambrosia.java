/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.itemstats.potions;

import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.plugins.itemstats.Builders;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;
import net.runelite.client.plugins.itemstats.stats.Stats;

public class Ambrosia
implements Effect {
    @Override
    public StatsChanges calculate(Client client) {
        int currentHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
        int maxHp = client.getRealSkillLevel(Skill.HITPOINTS);
        int hpToMax = Math.max(0, maxHp - currentHp);
        int currentPrayer = client.getBoostedSkillLevel(Skill.PRAYER);
        int maxPrayer = client.getRealSkillLevel(Skill.PRAYER);
        int prayerToMax = Math.max(0, maxPrayer - currentPrayer);
        StatChange hpChange = Builders.boost(Stats.HITPOINTS, Builders.perc(0.25, 2 + hpToMax)).effect(client);
        StatChange prayerChange = Builders.boost(Stats.PRAYER, Builders.perc(0.2, 5 + prayerToMax)).effect(client);
        StatChange runChange = Builders.heal(Stats.RUN_ENERGY, 100).effect(client);
        StatsChanges changes = new StatsChanges(3);
        changes.setStatChanges(new StatChange[]{hpChange, prayerChange, runChange});
        return changes;
    }
}

