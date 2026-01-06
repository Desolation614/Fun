/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats.special;

import java.util.ArrayList;
import net.runelite.api.Client;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.Positivity;
import net.runelite.client.plugins.itemstats.RangeStatChange;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;
import net.runelite.client.plugins.itemstats.stats.Stat;
import net.runelite.client.plugins.itemstats.stats.Stats;

public class SpicyStew
implements Effect {
    @Override
    public StatsChanges calculate(Client client) {
        int redBoost = SpicyStew.spiceBoostOf(client.getVarbitValue(1879));
        int yellowBoost = SpicyStew.spiceBoostOf(client.getVarbitValue(1880));
        int orangeBoost = SpicyStew.spiceBoostOf(client.getVarbitValue(1882));
        int brownBoost = SpicyStew.spiceBoostOf(client.getVarbitValue(1881));
        ArrayList<StatChange> changes = new ArrayList<StatChange>();
        if (redBoost > 0) {
            changes.add(SpicyStew.statChangeOf(Stats.ATTACK, redBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.STRENGTH, redBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.DEFENCE, redBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.RANGED, redBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.MAGIC, redBoost, client));
        }
        if (yellowBoost > 0) {
            changes.add(SpicyStew.statChangeOf(Stats.PRAYER, yellowBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.AGILITY, yellowBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.THIEVING, yellowBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.SLAYER, yellowBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.HUNTER, yellowBoost, client));
        }
        if (orangeBoost > 0) {
            changes.add(SpicyStew.statChangeOf(Stats.SMITHING, orangeBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.COOKING, orangeBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.CRAFTING, orangeBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.FIREMAKING, orangeBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.FLETCHING, orangeBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.RUNECRAFT, orangeBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.CONSTRUCTION, orangeBoost, client));
        }
        if (brownBoost > 0) {
            changes.add(SpicyStew.statChangeOf(Stats.MINING, brownBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.HERBLORE, brownBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.FISHING, brownBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.WOODCUTTING, brownBoost, client));
            changes.add(SpicyStew.statChangeOf(Stats.FARMING, brownBoost, client));
        }
        StatsChanges changesReturn = new StatsChanges(4);
        changesReturn.setStatChanges(changes.toArray(new StatChange[changes.size()]));
        return changesReturn;
    }

    private static int spiceBoostOf(int spiceDoses) {
        return Math.max(0, spiceDoses * 2 - 1);
    }

    private static StatChange statChangeOf(Stat stat, int spiceBoost, Client client) {
        int currentBase;
        int currentValue = stat.getValue(client);
        int currentBoost = currentValue - (currentBase = stat.getMaximum(client));
        int spiceBoostCapped = currentBoost <= 0 ? spiceBoost : Math.max(0, spiceBoost - currentBoost);
        RangeStatChange change = new RangeStatChange();
        change.setStat(stat);
        change.setMinRelative(-spiceBoost);
        change.setRelative(spiceBoostCapped);
        change.setMinTheoretical(-spiceBoost);
        change.setTheoretical(spiceBoost);
        change.setMinAbsolute(Math.max(-spiceBoost, -currentValue));
        change.setAbsolute(stat.getValue(client) + spiceBoostCapped);
        Positivity positivity = spiceBoostCapped == 0 ? Positivity.NO_CHANGE : (spiceBoost > spiceBoostCapped ? Positivity.BETTER_CAPPED : Positivity.BETTER_UNCAPPED);
        change.setPositivity(positivity);
        return change;
    }
}

