/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Point
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.cluescrolls;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

class ClueScrollWorldMapPoint
extends WorldMapPoint {
    private final ClueScrollPlugin plugin;
    private final BufferedImage clueScrollWorldImage;
    private final Point clueScrollWorldImagePoint;

    ClueScrollWorldMapPoint(WorldPoint worldPoint, ClueScrollPlugin plugin) {
        super(worldPoint, null);
        this.clueScrollWorldImage = new BufferedImage(plugin.getMapArrow().getWidth(), plugin.getMapArrow().getHeight(), 2);
        Graphics graphics = this.clueScrollWorldImage.getGraphics();
        graphics.drawImage(plugin.getMapArrow(), 0, 0, null);
        graphics.drawImage(plugin.getClueScrollImage(), 0, 0, null);
        this.clueScrollWorldImagePoint = new Point(this.clueScrollWorldImage.getWidth() / 2, this.clueScrollWorldImage.getHeight());
        this.plugin = plugin;
        this.setSnapToEdge(true);
        this.setJumpOnClick(true);
        this.setName("Clue Scroll");
        this.setImage(this.clueScrollWorldImage);
        this.setImagePoint(this.clueScrollWorldImagePoint);
    }

    @Override
    public void onEdgeSnap() {
        this.setImage(this.plugin.getClueScrollImage());
        this.setImagePoint(null);
    }

    @Override
    public void onEdgeUnsnap() {
        this.setImage(this.clueScrollWorldImage);
        this.setImagePoint(this.clueScrollWorldImagePoint);
    }
}

