/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats.food;

import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.FoodBase;

public class Anglerfish
extends FoodBase {
    public Anglerfish() {
        this.setBoost(true);
    }

    @Override
    public int heals(Client client) {
        int maxHP = this.getStat().getMaximum(client);
        int C = maxHP <= 24 ? 2 : (maxHP <= 49 ? 4 : (maxHP <= 74 ? 6 : (maxHP <= 92 ? 8 : 13)));
        return maxHP / 10 + C;
    }
}

