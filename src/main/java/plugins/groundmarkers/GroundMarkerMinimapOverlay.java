/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.groundmarkers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.groundmarkers.ColorTileMarker;
import net.runelite.client.plugins.groundmarkers.GroundMarkerConfig;
import net.runelite.client.plugins.groundmarkers.GroundMarkerPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class GroundMarkerMinimapOverlay
extends Overlay {
    private final Client client;
    private final GroundMarkerConfig config;
    private final GroundMarkerPlugin plugin;

    @Inject
    private GroundMarkerMinimapOverlay(Client client, GroundMarkerConfig config, GroundMarkerPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(0.0f);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.config.drawTileOnMinimmap()) {
            return null;
        }
        List<ColorTileMarker> points = this.plugin.getPoints();
        for (ColorTileMarker point : points) {
            WorldPoint worldPoint = point.getWorldPoint();
            if (worldPoint.getPlane() != this.client.getPlane()) continue;
            Color tileColor = point.getColor();
            if (tileColor == null) {
                tileColor = this.config.markerColor();
            }
            this.drawOnMinimap(graphics, worldPoint, tileColor);
        }
        return null;
    }

    private void drawOnMinimap(Graphics2D graphics, WorldPoint point, Color color) {
        if (!point.isInScene(this.client)) {
            return;
        }
        int x = point.getX() - this.client.getBaseX();
        int y = point.getY() - this.client.getBaseY();
        Point mp1 = Perspective.localToMinimap((Client)this.client, (LocalPoint)new LocalPoint(x <<= 7, y <<= 7));
        Point mp2 = Perspective.localToMinimap((Client)this.client, (LocalPoint)new LocalPoint(x, y + 128));
        Point mp3 = Perspective.localToMinimap((Client)this.client, (LocalPoint)new LocalPoint(x + 128, y + 128));
        Point mp4 = Perspective.localToMinimap((Client)this.client, (LocalPoint)new LocalPoint(x + 128, y));
        if (mp1 == null || mp2 == null || mp3 == null || mp4 == null) {
            return;
        }
        Polygon poly = new Polygon();
        poly.addPoint(mp1.getX(), mp1.getY());
        poly.addPoint(mp2.getX(), mp2.getY());
        poly.addPoint(mp3.getX(), mp3.getY());
        poly.addPoint(mp4.getX(), mp4.getY());
        BasicStroke stroke = new BasicStroke(1.0f);
        graphics.setStroke(stroke);
        graphics.setColor(color);
        graphics.drawPolygon(poly);
    }
}

