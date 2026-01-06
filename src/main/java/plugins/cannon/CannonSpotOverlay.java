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
package net.runelite.client.plugins.cannon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.cannon.CannonConfig;
import net.runelite.client.plugins.cannon.CannonPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class CannonSpotOverlay
extends Overlay {
    private final Client client;
    private final CannonPlugin plugin;
    private final CannonConfig config;
    @Inject
    private ItemManager itemManager;
    private boolean hidden;

    @Inject
    CannonSpotOverlay(Client client, CannonPlugin plugin, CannonConfig config) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<WorldPoint> spotPoints = this.plugin.getSpotPoints();
        if (this.hidden || spotPoints.isEmpty() || !this.config.showCannonSpots() || this.plugin.isCannonPlaced()) {
            return null;
        }
        for (WorldPoint spot : spotPoints) {
            if (spot.getPlane() != this.client.getPlane()) continue;
            LocalPoint spotPoint = LocalPoint.fromWorld((Client)this.client, (WorldPoint)spot);
            LocalPoint localLocation = this.client.getLocalPlayer().getLocalLocation();
            if (spotPoint == null || localLocation.distanceTo(spotPoint) > 4100) continue;
            this.renderCannonSpot(graphics, this.client, spotPoint, this.itemManager.getImage(2), Color.RED);
        }
        return null;
    }

    private void renderCannonSpot(Graphics2D graphics, Client client, LocalPoint point, BufferedImage image, Color color) {
        Point imageLoc;
        Polygon poly = Perspective.getCanvasTilePoly((Client)client, (LocalPoint)point);
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }
        if ((imageLoc = Perspective.getCanvasImageLocation((Client)client, (LocalPoint)point, (BufferedImage)image, (int)0)) != null) {
            OverlayUtil.renderImageLocation(graphics, imageLoc, image);
        }
    }

    void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}

