/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats.potions;

import com.google.common.annotations.VisibleForTesting;
import java.util.Comparator;
import java.util.stream.Stream;
import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.Builders;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.SimpleStatBoost;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;
import net.runelite.client.plugins.itemstats.potions.PrayerPotion;
import net.runelite.client.plugins.itemstats.stats.Stat;
import net.runelite.client.plugins.itemstats.stats.Stats;

public class SuperRestore
implements Effect {
    private static final Stat[] superRestoreStats = new Stat[]{Stats.ATTACK, Stats.DEFENCE, Stats.STRENGTH, Stats.RANGED, Stats.MAGIC, Stats.COOKING, Stats.WOODCUTTING, Stats.FLETCHING, Stats.FISHING, Stats.FIREMAKING, Stats.CRAFTING, Stats.SMITHING, Stats.MINING, Stats.HERBLORE, Stats.AGILITY, Stats.THIEVING, Stats.SLAYER, Stats.FARMING, Stats.RUNECRAFT, Stats.HUNTER, Stats.CONSTRUCTION};
    @VisibleForTesting
    public final double percR;
    private final int delta;

    @Override
    public StatsChanges calculate(Client client) {
        StatsChanges changes = new StatsChanges(0);
        SimpleStatBoost calc = new SimpleStatBoost(null, false, Builders.perc(this.percR, this.delta));
        PrayerPotion prayer = new PrayerPotion(this.delta, this.percR);
        changes.setStatChanges((StatChange[])Stream.concat(Stream.of(prayer.effect(client)), Stream.of(superRestoreStats).filter(stat -> stat.getValue(client) < stat.getMaximum(client)).map(stat -> {
            calc.setStat((Stat)stat);
            return calc.effect(client);
        })).toArray(StatChange[]::new));
        changes.setPositivity(Stream.of(changes.getStatChanges()).map(sc -> sc.getPositivity()).max(Comparator.naturalOrder()).get());
        return changes;
    }

    public SuperRestore(double percR, int delta) {
        this.percR = percR;
        this.delta = delta;
    }
}

