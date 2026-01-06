/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.itemstats;

import net.runelite.client.plugins.itemstats.Positivity;
import net.runelite.client.plugins.itemstats.StatChange;

public class StatsChanges {
    private Positivity positivity;
    private StatChange[] statChanges;

    public StatsChanges(int len) {
        this.statChanges = new StatChange[len];
        this.positivity = Positivity.NO_CHANGE;
    }

    public Positivity getPositivity() {
        return this.positivity;
    }

    public void setPositivity(Positivity positivity) {
        this.positivity = positivity;
    }

    public StatChange[] getStatChanges() {
        return this.statChanges;
    }

    public void setStatChanges(StatChange[] statChanges) {
        this.statChanges = statChanges;
    }
}

