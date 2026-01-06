/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.fishing;

import java.time.Instant;
import net.runelite.api.coords.WorldPoint;

final class MinnowSpot {
    private final WorldPoint loc;
    private final Instant time;

    public WorldPoint getLoc() {
        return this.loc;
    }

    public Instant getTime() {
        return this.time;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MinnowSpot)) {
            return false;
        }
        MinnowSpot other = (MinnowSpot)o;
        WorldPoint this$loc = this.getLoc();
        WorldPoint other$loc = other.getLoc();
        if (this$loc == null ? other$loc != null : !this$loc.equals(other$loc)) {
            return false;
        }
        Instant this$time = this.getTime();
        Instant other$time = other.getTime();
        return !(this$time == null ? other$time != null : !((Object)this$time).equals(other$time));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        WorldPoint $loc = this.getLoc();
        result = result * 59 + ($loc == null ? 43 : $loc.hashCode());
        Instant $time = this.getTime();
        result = result * 59 + ($time == null ? 43 : ((Object)$time).hashCode());
        return result;
    }

    public String toString() {
        return "MinnowSpot(loc=" + String.valueOf(this.getLoc()) + ", time=" + String.valueOf(this.getTime()) + ")";
    }

    public MinnowSpot(WorldPoint loc, Instant time) {
        this.loc = loc;
        this.time = time;
    }
}

