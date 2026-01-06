/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.itemstats;

import net.runelite.client.plugins.itemstats.StatBoost;
import net.runelite.client.plugins.itemstats.stats.Stats;

public abstract class FoodBase
extends StatBoost {
    public FoodBase() {
        super(Stats.HITPOINTS, false);
    }
}

