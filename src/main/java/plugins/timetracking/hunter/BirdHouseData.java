/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.hunter;

import net.runelite.client.plugins.timetracking.hunter.BirdHouseSpace;

final class BirdHouseData {
    private final BirdHouseSpace space;
    private final int varp;
    private final long timestamp;

    public BirdHouseData(BirdHouseSpace space, int varp, long timestamp) {
        this.space = space;
        this.varp = varp;
        this.timestamp = timestamp;
    }

    public BirdHouseSpace getSpace() {
        return this.space;
    }

    public int getVarp() {
        return this.varp;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BirdHouseData)) {
            return false;
        }
        BirdHouseData other = (BirdHouseData)o;
        if (this.getVarp() != other.getVarp()) {
            return false;
        }
        if (this.getTimestamp() != other.getTimestamp()) {
            return false;
        }
        BirdHouseSpace this$space = this.getSpace();
        BirdHouseSpace other$space = other.getSpace();
        return !(this$space == null ? other$space != null : !((Object)((Object)this$space)).equals((Object)other$space));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getVarp();
        long $timestamp = this.getTimestamp();
        result = result * 59 + (int)($timestamp >>> 32 ^ $timestamp);
        BirdHouseSpace $space = this.getSpace();
        result = result * 59 + ($space == null ? 43 : ((Object)((Object)$space)).hashCode());
        return result;
    }

    public String toString() {
        return "BirdHouseData(space=" + String.valueOf((Object)this.getSpace()) + ", varp=" + this.getVarp() + ", timestamp=" + this.getTimestamp() + ")";
    }
}

