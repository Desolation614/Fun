/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.runelite.api.Client
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.runelite.client.plugins.itemstats.potions;

import java.util.Comparator;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.Builders;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.Positivity;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;
import net.runelite.client.plugins.itemstats.stats.Stats;
import org.apache.commons.lang3.ArrayUtils;

public class MixedPotion
implements Effect {
    private final int heal;
    @Nonnull
    private final Effect potion;

    @Override
    public StatsChanges calculate(Client client) {
        StatsChanges changes = new StatsChanges(0);
        StatChange mixedPotionHpBoost = Builders.food(this.heal).effect(client);
        StatsChanges potionChanges = this.potion.calculate(client);
        int mixedPotionHitpointsHealing = mixedPotionHpBoost.getRelative();
        if (Stream.of(potionChanges.getStatChanges()).anyMatch(statChange -> statChange.getStat() == Stats.HITPOINTS)) {
            changes.setStatChanges((StatChange[])Stream.of(potionChanges.getStatChanges()).map(change -> {
                if (change.getStat() != Stats.HITPOINTS || mixedPotionHitpointsHealing == 0 || change.getTheoretical() >= 0) {
                    return change;
                }
                int max = Stats.HITPOINTS.getMaximum(client);
                int absolute = change.getAbsolute();
                int relative = change.getRelative();
                if (absolute + mixedPotionHitpointsHealing > max) {
                    change.setPositivity(Positivity.BETTER_CAPPED);
                } else if (relative + mixedPotionHitpointsHealing > 0) {
                    change.setPositivity(Positivity.BETTER_UNCAPPED);
                } else if (relative + mixedPotionHitpointsHealing == 0) {
                    change.setPositivity(Positivity.NO_CHANGE);
                } else {
                    change.setPositivity(Positivity.WORSE);
                }
                change.setAbsolute(Math.min(max, absolute + mixedPotionHitpointsHealing));
                change.setRelative(change.getRelative() + mixedPotionHitpointsHealing);
                change.setTheoretical(change.getTheoretical() + mixedPotionHitpointsHealing);
                return change;
            }).toArray(StatChange[]::new));
        } else {
            changes.setStatChanges((StatChange[])ArrayUtils.addAll((Object[])new StatChange[]{mixedPotionHpBoost}, (Object[])potionChanges.getStatChanges()));
        }
        changes.setPositivity(Stream.of(changes.getStatChanges()).map(StatChange::getPositivity).max(Comparator.naturalOrder()).get());
        return changes;
    }

    public MixedPotion(int heal, @Nonnull Effect potion) {
        if (potion == null) {
            throw new NullPointerException("potion is marked non-null but is null");
        }
        this.heal = heal;
        this.potion = potion;
    }
}

