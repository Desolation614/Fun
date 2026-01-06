/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.party.data;

import java.awt.Color;
import net.runelite.api.coords.WorldPoint;

public class PartyTilePingData {
    private final WorldPoint point;
    private final Color color;
    private int alpha = 255;
    private final long creationTime = System.nanoTime();

    public PartyTilePingData(WorldPoint point, Color color) {
        this.point = point;
        this.color = color;
    }

    public WorldPoint getPoint() {
        return this.point;
    }

    public Color getColor() {
        return this.color;
    }

    public int getAlpha() {
        return this.alpha;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }
}

