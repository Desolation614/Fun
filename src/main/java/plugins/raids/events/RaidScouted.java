/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.raids.events;

import net.runelite.client.plugins.raids.Raid;

public final class RaidScouted {
    private final Raid raid;
    private final boolean firstScout;

    public RaidScouted(Raid raid, boolean firstScout) {
        this.raid = raid;
        this.firstScout = firstScout;
    }

    public Raid getRaid() {
        return this.raid;
    }

    public boolean isFirstScout() {
        return this.firstScout;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RaidScouted)) {
            return false;
        }
        RaidScouted other = (RaidScouted)o;
        if (this.isFirstScout() != other.isFirstScout()) {
            return false;
        }
        Raid this$raid = this.getRaid();
        Raid other$raid = other.getRaid();
        return !(this$raid == null ? other$raid != null : !this$raid.equals(other$raid));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isFirstScout() ? 79 : 97);
        Raid $raid = this.getRaid();
        result = result * 59 + ($raid == null ? 43 : $raid.hashCode());
        return result;
    }

    public String toString() {
        return "RaidScouted(raid=" + String.valueOf(this.getRaid()) + ", firstScout=" + this.isFirstScout() + ")";
    }
}

