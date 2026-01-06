/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.party.messages;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.party.messages.PartyMemberMessage;

public class LocationUpdate
extends PartyMemberMessage {
    private final int c;

    public LocationUpdate(WorldPoint worldPoint) {
        this.c = worldPoint.getPlane() << 28 | worldPoint.getX() << 14 | worldPoint.getY();
    }

    public WorldPoint getWorldPoint() {
        return new WorldPoint(this.c >> 14 & 0x3FFF, this.c & 0x3FFF, this.c >> 28 & 3);
    }

    public String toString() {
        return "LocationUpdate(getWorldPoint=" + String.valueOf(this.getWorldPoint()) + ")";
    }
}

