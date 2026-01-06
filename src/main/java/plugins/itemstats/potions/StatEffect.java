/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats.potions;

import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.ItemStatChanges;
import net.runelite.client.plugins.itemstats.SimpleStatBoost;
import net.runelite.client.plugins.itemstats.SingleEffect;
import net.runelite.client.plugins.itemstats.StatBoost;
import net.runelite.client.plugins.itemstats.delta.DeltaPercentage;
import net.runelite.client.plugins.itemstats.stats.Stat;
import net.runelite.client.plugins.itemstats.stats.Stats;

enum StatEffect {
    ATTACK_POTION(Stats.ATTACK, 3, ItemStatChanges.ATTACK_POT),
    DEFENCE_POTION(Stats.DEFENCE, 30, ItemStatChanges.DEFENCE_POT),
    DIVINE_SUPER_DEFENCE_POTION(Stats.DEFENCE, 70, new SimpleStatBoost(Stats.DEFENCE, true, new DeltaPercentage(0.2, 7))),
    STRENGTH_POTION(Stats.STRENGTH, 12, ItemStatChanges.STRENGTH_POT),
    SUPER_ATTACK_POTION(Stats.ATTACK, 45, ItemStatChanges.SUPER_ATTACK_POT),
    SUPER_DEFENCE_POTION(Stats.DEFENCE, 66, ItemStatChanges.SUPER_DEFENCE_POT),
    SUPER_STRENGTH_POTION(Stats.STRENGTH, 55, ItemStatChanges.SUPER_STRENGTH_POT),
    PRAYER_POTION(Stats.PRAYER, 38, new StatBoost(Stats.PRAYER, false){

        @Override
        public int heals(Client client) {
            int pray = Stats.PRAYER.getMaximum(client);
            int herb = Stats.HERBLORE.getValue(client);
            return herb < 38 ? 0 : (int)Math.max((double)pray * 0.25, (double)herb * 0.3) + 7;
        }
    });

    private final Stat stat;
    private final int levelRequirement;
    private final SingleEffect effect;

    private StatEffect(Stat stat, int levelRequirement, SingleEffect effect) {
        this.stat = stat;
        this.levelRequirement = levelRequirement;
        this.effect = effect;
    }

    public Stat getStat() {
        return this.stat;
    }

    public int getLevelRequirement() {
        return this.levelRequirement;
    }

    public SingleEffect getEffect() {
        return this.effect;
    }
}

