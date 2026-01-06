/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.itemstats;

import net.runelite.client.plugins.itemstats.Positivity;
import net.runelite.client.plugins.itemstats.stats.Stat;

public class StatChange {
    private Stat stat;
    private int relative;
    private int theoretical;
    private int absolute;
    private Positivity positivity;

    public String getFormattedRelative() {
        return StatChange.formatBoost(this.relative);
    }

    public String getFormattedTheoretical() {
        return StatChange.formatBoost(this.theoretical);
    }

    static String formatBoost(int boost) {
        return String.format("%+d", boost);
    }

    public Stat getStat() {
        return this.stat;
    }

    public int getRelative() {
        return this.relative;
    }

    public int getTheoretical() {
        return this.theoretical;
    }

    public int getAbsolute() {
        return this.absolute;
    }

    public Positivity getPositivity() {
        return this.positivity;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public void setRelative(int relative) {
        this.relative = relative;
    }

    public void setTheoretical(int theoretical) {
        this.theoretical = theoretical;
    }

    public void setAbsolute(int absolute) {
        this.absolute = absolute;
    }

    public void setPositivity(Positivity positivity) {
        this.positivity = positivity;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StatChange)) {
            return false;
        }
        StatChange other = (StatChange)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getRelative() != other.getRelative()) {
            return false;
        }
        if (this.getTheoretical() != other.getTheoretical()) {
            return false;
        }
        if (this.getAbsolute() != other.getAbsolute()) {
            return false;
        }
        Stat this$stat = this.getStat();
        Stat other$stat = other.getStat();
        if (this$stat == null ? other$stat != null : !this$stat.equals(other$stat)) {
            return false;
        }
        Positivity this$positivity = this.getPositivity();
        Positivity other$positivity = other.getPositivity();
        return !(this$positivity == null ? other$positivity != null : !((Object)((Object)this$positivity)).equals((Object)other$positivity));
    }

    protected boolean canEqual(Object other) {
        return other instanceof StatChange;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getRelative();
        result = result * 59 + this.getTheoretical();
        result = result * 59 + this.getAbsolute();
        Stat $stat = this.getStat();
        result = result * 59 + ($stat == null ? 43 : $stat.hashCode());
        Positivity $positivity = this.getPositivity();
        result = result * 59 + ($positivity == null ? 43 : ((Object)((Object)$positivity)).hashCode());
        return result;
    }

    public String toString() {
        return "StatChange(stat=" + String.valueOf(this.getStat()) + ", relative=" + this.getRelative() + ", theoretical=" + this.getTheoretical() + ", absolute=" + this.getAbsolute() + ", positivity=" + String.valueOf((Object)this.getPositivity()) + ")";
    }
}

