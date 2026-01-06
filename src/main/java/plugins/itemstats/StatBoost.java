/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.itemstats;

import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.client.plugins.itemstats.Positivity;
import net.runelite.client.plugins.itemstats.SingleEffect;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.stats.Stat;

public abstract class StatBoost
extends SingleEffect {
    private Stat stat;
    private boolean boost;

    public StatBoost(Stat stat, boolean boost) {
        this.stat = stat;
        this.boost = boost;
    }

    public abstract int heals(Client var1);

    @Override
    public StatChange effect(Client client) {
        int newValue;
        int value = this.stat.getValue(client);
        int max = this.stat.getMaximum(client);
        boolean hitCap = false;
        int calcedDelta = this.heals(client);
        if (calcedDelta > 0) {
            String statName = this.stat.getName();
            int meleeMastery = client.getVarbitValue(11580);
            int rangedMastery = client.getVarbitValue(11581);
            int magicMastery = client.getVarbitValue(11582);
            if (statName.equals(Skill.HITPOINTS.getName())) {
                ItemContainer equipment;
                float multiplier = 1.0f;
                if (meleeMastery >= 2 || rangedMastery >= 2 || magicMastery >= 2) {
                    multiplier = (float)((double)multiplier + 0.2);
                }
                if ((equipment = client.getItemContainer(94)) != null && equipment.contains(30386)) {
                    multiplier += 1.0f;
                }
                calcedDelta = (int)((float)calcedDelta * multiplier);
            } else if ((meleeMastery >= 5 || rangedMastery >= 5 || magicMastery >= 5) && statName.equals(Skill.PRAYER.getName())) {
                calcedDelta = (int)((double)calcedDelta * 1.25);
            }
        }
        if (this.boost && calcedDelta > 0) {
            max += calcedDelta;
        }
        if (value > max) {
            max = value;
        }
        if ((newValue = value + calcedDelta) > max) {
            newValue = max;
            hitCap = true;
        }
        if (newValue < 0) {
            newValue = 0;
        }
        int delta = newValue - value;
        StatChange out = new StatChange();
        out.setStat(this.stat);
        if (delta > 0) {
            out.setPositivity(hitCap ? Positivity.BETTER_CAPPED : Positivity.BETTER_UNCAPPED);
        } else if (delta == 0) {
            out.setPositivity(Positivity.NO_CHANGE);
        } else {
            out.setPositivity(Positivity.WORSE);
        }
        out.setAbsolute(newValue);
        out.setRelative(delta);
        out.setTheoretical(calcedDelta);
        return out;
    }

    public Stat getStat() {
        return this.stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public boolean isBoost() {
        return this.boost;
    }

    public void setBoost(boolean boost) {
        this.boost = boost;
    }
}

