/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats;

import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.Positivity;
import net.runelite.client.plugins.itemstats.SingleEffect;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;

public class Combo
implements Effect {
    private final SingleEffect[] calcs;

    @Override
    public StatsChanges calculate(Client client) {
        StatsChanges out = new StatsChanges(this.calcs.length);
        StatChange[] statChanges = out.getStatChanges();
        Positivity positivity = Positivity.NO_CHANGE;
        for (int i = 0; i < this.calcs.length; ++i) {
            statChanges[i] = this.calcs[i].effect(client);
            if (positivity.ordinal() >= statChanges[i].getPositivity().ordinal()) continue;
            positivity = statChanges[i].getPositivity();
        }
        out.setPositivity(positivity);
        return out;
    }

    public Combo(SingleEffect[] calcs) {
        this.calcs = calcs;
    }
}

