/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats;

import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.StatBoost;
import net.runelite.client.plugins.itemstats.delta.DeltaCalculator;
import net.runelite.client.plugins.itemstats.stats.Stat;

public class CappedStatBoost
extends StatBoost {
    private final DeltaCalculator deltaCalculator;
    private final DeltaCalculator capCalculator;

    public CappedStatBoost(Stat stat, DeltaCalculator deltaCalculator, DeltaCalculator capCalculator) {
        super(stat, true);
        this.deltaCalculator = deltaCalculator;
        this.capCalculator = capCalculator;
    }

    @Override
    public int heals(Client client) {
        int cap;
        int current = this.getStat().getValue(client);
        int max = this.getStat().getMaximum(client);
        int delta = this.deltaCalculator.calculateDelta(max);
        if (delta + current <= max + (cap = this.capCalculator.calculateDelta(max))) {
            return delta;
        }
        if (current > max + cap) {
            return 0;
        }
        return max + cap - current;
    }
}

