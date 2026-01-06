/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.party.messages;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.party.messages.PartyMemberMessage;

public final class TilePing
extends PartyMemberMessage {
    private final WorldPoint point;

    public TilePing(WorldPoint point) {
        this.point = point;
    }

    public WorldPoint getPoint() {
        return this.point;
    }

    public String toString() {
        return "TilePing(point=" + String.valueOf(this.getPoint()) + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TilePing)) {
            return false;
        }
        TilePing other = (TilePing)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        WorldPoint this$point = this.getPoint();
        WorldPoint other$point = other.getPoint();
        return !(this$point == null ? other$point != null : !this$point.equals(other$point));
    }

    protected boolean canEqual(Object other) {
        return other instanceof TilePing;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        WorldPoint $point = this.getPoint();
        result = result * 59 + ($point == null ? 43 : $point.hashCode());
        return result;
    }
}

