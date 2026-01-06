/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.itemstats;

import net.runelite.client.plugins.itemstats.Combo;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.Food;
import net.runelite.client.plugins.itemstats.RangeStatBoost;
import net.runelite.client.plugins.itemstats.SimpleStatBoost;
import net.runelite.client.plugins.itemstats.SingleEffect;
import net.runelite.client.plugins.itemstats.StatBoost;
import net.runelite.client.plugins.itemstats.delta.DeltaCalculator;
import net.runelite.client.plugins.itemstats.delta.DeltaPercentage;
import net.runelite.client.plugins.itemstats.stats.Stat;

public class Builders {
    public static Food food(int diff) {
        return Builders.food(max -> diff);
    }

    public static Food food(DeltaCalculator p) {
        return new Food(p);
    }

    public static Effect combo(SingleEffect ... effect) {
        return new Combo(effect);
    }

    public static SimpleStatBoost boost(Stat stat, int boost) {
        return Builders.boost(stat, max -> boost);
    }

    public static SimpleStatBoost boost(Stat stat, DeltaCalculator p) {
        return new SimpleStatBoost(stat, true, p);
    }

    public static SimpleStatBoost heal(Stat stat, int boost) {
        return Builders.heal(stat, max -> boost);
    }

    public static SimpleStatBoost heal(Stat stat, DeltaCalculator p) {
        return new SimpleStatBoost(stat, false, p);
    }

    public static SimpleStatBoost dec(Stat stat, int boost) {
        return Builders.heal(stat, -boost);
    }

    public static DeltaPercentage perc(double perc, int delta) {
        return new DeltaPercentage(perc, delta);
    }

    public static RangeStatBoost range(StatBoost a, StatBoost b) {
        return new RangeStatBoost(a, b);
    }
}

