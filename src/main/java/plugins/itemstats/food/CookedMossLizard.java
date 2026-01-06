/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats.food;

import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.FoodBase;
import net.runelite.client.plugins.itemstats.stats.Stats;

public class CookedMossLizard
extends FoodBase {
    @Override
    public int heals(Client client) {
        int cooking = Stats.COOKING.getValue(client) / 3;
        int hunter = Stats.HUNTER.getValue(client) / 2;
        return Math.min(cooking, hunter);
    }
}

