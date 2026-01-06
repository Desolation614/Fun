/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats.special;

import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.StatBoost;
import net.runelite.client.plugins.itemstats.stats.Stats;

public class CaveNightshade
extends StatBoost {
    public CaveNightshade() {
        super(Stats.HITPOINTS, false);
    }

    @Override
    public int heals(Client client) {
        int currentHP = this.getStat().getValue(client);
        if (currentHP < 20) {
            return -currentHP / 2;
        }
        return -15;
    }
}

