/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats;

import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;

public abstract class SingleEffect
implements Effect {
    @Override
    public final StatsChanges calculate(Client client) {
        StatsChanges c = new StatsChanges(1);
        StatChange[] statChanges = c.getStatChanges();
        statChanges[0] = this.effect(client);
        c.setPositivity(statChanges[0].getPositivity());
        return c;
    }

    public abstract StatChange effect(Client var1);
}

