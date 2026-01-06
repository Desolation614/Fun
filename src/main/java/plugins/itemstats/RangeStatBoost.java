/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats;

import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.Positivity;
import net.runelite.client.plugins.itemstats.RangeStatChange;
import net.runelite.client.plugins.itemstats.SingleEffect;
import net.runelite.client.plugins.itemstats.StatBoost;
import net.runelite.client.plugins.itemstats.StatChange;

public class RangeStatBoost
extends SingleEffect {
    private final StatBoost a;
    private final StatBoost b;

    RangeStatBoost(StatBoost a, StatBoost b) {
        assert (a.getStat() == b.getStat());
        this.a = a;
        this.b = b;
    }

    @Override
    public StatChange effect(Client client) {
        StatChange changeA = this.a.effect(client);
        StatChange changeB = this.b.effect(client);
        RangeStatChange r = new RangeStatChange();
        r.setMinAbsolute(Math.min(changeA.getAbsolute(), changeB.getAbsolute()));
        r.setAbsolute(Math.max(changeA.getAbsolute(), changeB.getAbsolute()));
        r.setMinRelative(Math.min(changeA.getRelative(), changeB.getRelative()));
        r.setRelative(Math.max(changeA.getRelative(), changeB.getRelative()));
        r.setMinTheoretical(Math.min(changeA.getTheoretical(), changeB.getTheoretical()));
        r.setTheoretical(Math.max(changeA.getTheoretical(), changeB.getTheoretical()));
        r.setStat(changeA.getStat());
        int avg = (changeA.getPositivity().ordinal() + changeB.getPositivity().ordinal()) / 2;
        r.setPositivity(Positivity.values()[avg]);
        return r;
    }
}

