/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.itemstats;

import net.runelite.client.plugins.itemstats.StatChange;

public class RangeStatChange
extends StatChange {
    private int minRelative;
    private int minTheoretical;
    private int minAbsolute;

    @Override
    public String getFormattedRelative() {
        return RangeStatChange.concat(this.minRelative, this.getRelative());
    }

    @Override
    public String getFormattedTheoretical() {
        return RangeStatChange.concat(this.minTheoretical, this.getTheoretical());
    }

    private static String concat(int changeA, int changeB) {
        if (changeA == changeB) {
            return RangeStatChange.formatBoost(changeA);
        }
        if (changeA * -1 == changeB) {
            return "\u00b1" + Math.abs(changeA);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%+d", changeA));
        sb.append('~');
        if (changeA < 0 && changeB < 0 || changeA >= 0 && changeB >= 0) {
            sb.append(Math.abs(changeB));
        } else {
            sb.append(String.format("%+d", changeB));
        }
        return sb.toString();
    }

    public int getMinRelative() {
        return this.minRelative;
    }

    public int getMinTheoretical() {
        return this.minTheoretical;
    }

    public int getMinAbsolute() {
        return this.minAbsolute;
    }

    public void setMinRelative(int minRelative) {
        this.minRelative = minRelative;
    }

    public void setMinTheoretical(int minTheoretical) {
        this.minTheoretical = minTheoretical;
    }

    public void setMinAbsolute(int minAbsolute) {
        this.minAbsolute = minAbsolute;
    }

    @Override
    public String toString() {
        return "RangeStatChange(minRelative=" + this.getMinRelative() + ", minTheoretical=" + this.getMinTheoretical() + ", minAbsolute=" + this.getMinAbsolute() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RangeStatChange)) {
            return false;
        }
        RangeStatChange other = (RangeStatChange)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (this.getMinRelative() != other.getMinRelative()) {
            return false;
        }
        if (this.getMinTheoretical() != other.getMinTheoretical()) {
            return false;
        }
        return this.getMinAbsolute() == other.getMinAbsolute();
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof RangeStatChange;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + this.getMinRelative();
        result = result * 59 + this.getMinTheoretical();
        result = result * 59 + this.getMinAbsolute();
        return result;
    }
}

