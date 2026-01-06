/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats.potions;

import java.util.Comparator;
import java.util.stream.Stream;
import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.BoostedStatBoost;
import net.runelite.client.plugins.itemstats.Builders;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.SimpleStatBoost;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;
import net.runelite.client.plugins.itemstats.stats.Stat;
import net.runelite.client.plugins.itemstats.stats.Stats;

public class SaradominBrew
implements Effect {
    private static final Stat[] saradominBrewStats = new Stat[]{Stats.ATTACK, Stats.STRENGTH, Stats.RANGED, Stats.MAGIC};
    private final double percH;
    private final double percD;
    private final double percSD;
    private final int deltaB;
    private final int deltaR;

    @Override
    public StatsChanges calculate(Client client) {
        StatsChanges changes = new StatsChanges(0);
        SimpleStatBoost hitpoints = new SimpleStatBoost(Stats.HITPOINTS, true, Builders.perc(this.percH, this.deltaB));
        SimpleStatBoost defence = new SimpleStatBoost(Stats.DEFENCE, true, Builders.perc(this.percD, this.deltaB));
        BoostedStatBoost calc = new BoostedStatBoost(null, false, Builders.perc(this.percSD, -this.deltaR));
        changes.setStatChanges((StatChange[])Stream.of(Stream.of(hitpoints.effect(client)), Stream.of(defence.effect(client)), Stream.of(saradominBrewStats).filter(stat -> 1 < stat.getValue(client)).map(stat -> {
            calc.setStat((Stat)stat);
            return calc.effect(client);
        })).reduce(Stream::concat).orElseGet(Stream::empty).toArray(StatChange[]::new));
        changes.setPositivity(Stream.of(changes.getStatChanges()).map(StatChange::getPositivity).max(Comparator.naturalOrder()).get());
        return changes;
    }

    public SaradominBrew(double percH, double percD, double percSD, int deltaB, int deltaR) {
        this.percH = percH;
        this.percD = percD;
        this.percSD = percSD;
        this.deltaB = deltaB;
        this.deltaR = deltaR;
    }
}

