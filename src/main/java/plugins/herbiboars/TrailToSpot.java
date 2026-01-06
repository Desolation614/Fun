/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.runelite.client.plugins.herbiboars;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

final class TrailToSpot {
    private final int varbitId;
    private final int value;
    private final int footprint;

    Set<Integer> getFootprintIds() {
        return ImmutableSet.of((Object)this.footprint, (Object)(this.footprint + 1));
    }

    public TrailToSpot(int varbitId, int value, int footprint) {
        this.varbitId = varbitId;
        this.value = value;
        this.footprint = footprint;
    }

    public int getValue() {
        return this.value;
    }

    public int getFootprint() {
        return this.footprint;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TrailToSpot)) {
            return false;
        }
        TrailToSpot other = (TrailToSpot)o;
        if (this.getVarbitId() != other.getVarbitId()) {
            return false;
        }
        if (this.getValue() != other.getValue()) {
            return false;
        }
        return this.getFootprint() == other.getFootprint();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getVarbitId();
        result = result * 59 + this.getValue();
        result = result * 59 + this.getFootprint();
        return result;
    }

    public String toString() {
        return "TrailToSpot(varbitId=" + this.getVarbitId() + ", value=" + this.getValue() + ", footprint=" + this.getFootprint() + ")";
    }

    public int getVarbitId() {
        return this.varbitId;
    }
}

