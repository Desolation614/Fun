/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.groundmarkers;

import com.google.common.base.Strings;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.List;
import javax.annotation.Nullable;
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
import net.runelite.client.ui.overlay.OverlayUtil;

public class GroundMarkerOverlay
extends Overlay {
    private static final int MAX_DRAW_DISTANCE = 32;
    private final Client client;
    private final GroundMarkerConfig config;
    private final GroundMarkerPlugin plugin;

    @Inject
    private GroundMarkerOverlay(Client client, GroundMarkerConfig config, GroundMarkerPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(0.0f);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<ColorTileMarker> points = this.plugin.getPoints();
        if (points.isEmpty()) {
            return null;
        }
        BasicStroke stroke = new BasicStroke((float)this.config.borderWidth());
        for (ColorTileMarker point : points) {
            WorldPoint worldPoint = point.getWorldPoint();
            if (worldPoint.getPlane() != this.client.getPlane()) continue;
            Color tileColor = point.getColor();
            if (tileColor == null) {
                tileColor = this.config.markerColor();
            }
            this.drawTile(graphics, worldPoint, tileColor, point.getLabel(), stroke);
        }
        return null;
    }

    private void drawTile(Graphics2D graphics, WorldPoint point, Color color, @Nullable String label, Stroke borderStroke) {
        Point canvasTextLocation;
        WorldPoint playerLocation = this.client.getLocalPlayer().getWorldLocation();
        if (point.distanceTo(playerLocation) >= 32) {
            return;
        }
        LocalPoint lp = LocalPoint.fromWorld((Client)this.client, (WorldPoint)point);
        if (lp == null) {
            return;
        }
        Polygon poly = Perspective.getCanvasTilePoly((Client)this.client, (LocalPoint)lp);
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color, new Color(0, 0, 0, this.config.fillOpacity()), borderStroke);
        }
        if (!Strings.isNullOrEmpty((String)label) && (canvasTextLocation = Perspective.getCanvasTextLocation((Client)this.client, (Graphics2D)graphics, (LocalPoint)lp, (String)label, (int)0)) != null) {
            OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
        }
    }
}

