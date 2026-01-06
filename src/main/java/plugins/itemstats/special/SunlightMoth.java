/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats.special;

import java.util.Comparator;
import java.util.stream.Stream;
import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.Builders;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.SimpleStatBoost;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;
import net.runelite.client.plugins.itemstats.stats.Stat;
import net.runelite.client.plugins.itemstats.stats.Stats;

public class SunlightMoth
implements Effect {
    private static final Stat[] RESTORED_STATS = new Stat[]{Stats.ATTACK, Stats.DEFENCE, Stats.STRENGTH, Stats.RANGED, Stats.MAGIC, Stats.COOKING, Stats.WOODCUTTING, Stats.FLETCHING, Stats.FISHING, Stats.FIREMAKING, Stats.CRAFTING, Stats.SMITHING, Stats.MINING, Stats.HERBLORE, Stats.AGILITY, Stats.THIEVING, Stats.SLAYER, Stats.FARMING, Stats.RUNECRAFT, Stats.HUNTER, Stats.CONSTRUCTION};
    public final double percentRestored;
    private final int delta;

    public SunlightMoth(double percentRestored, int delta) {
        this.percentRestored = percentRestored;
        this.delta = delta;
    }

    @Override
    public StatsChanges calculate(Client client) {
        StatsChanges changes = new StatsChanges(0);
        SimpleStatBoost calcRestore = new SimpleStatBoost(null, false, Builders.perc(this.percentRestored, this.delta));
        SimpleStatBoost calcHeal = new SimpleStatBoost(Stats.HITPOINTS, false, Builders.perc(0.0, 8));
        changes.setStatChanges((StatChange[])Stream.concat(Stream.of(new Stat[]{Stats.HITPOINTS}).map(stat -> calcHeal.effect(client)), Stream.of(RESTORED_STATS).filter(stat -> stat.getValue(client) < stat.getMaximum(client)).map(stat -> {
            calcRestore.setStat((Stat)stat);
            return calcRestore.effect(client);
        })).toArray(StatChange[]::new));
        changes.setPositivity(Stream.of(changes.getStatChanges()).map(StatChange::getPositivity).max(Comparator.naturalOrder()).orElse(null));
        return changes;
    }
}

