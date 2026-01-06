/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GraphicsObject
 *  net.runelite.api.Perspective
 *  net.runelite.api.Player
 *  net.runelite.api.coords.LocalPoint
 */
package net.runelite.client.plugins.zalcano;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.zalcano.ZalcanoPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class ZalcanoOverlay
extends Overlay {
    private final Client client;
    private final ZalcanoPlugin zalcanoPlugin;

    @Inject
    private ZalcanoOverlay(Client client, ZalcanoPlugin zalcanoPlugin) {
        this.client = client;
        this.zalcanoPlugin = zalcanoPlugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.setPriority(0.5f);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Polygon polygon;
        LocalPoint targetedGlowingRock;
        List<GraphicsObject> rocks = this.zalcanoPlugin.getRocks();
        if (!rocks.isEmpty()) {
            rocks.removeIf(GraphicsObject::finished);
            for (GraphicsObject graphicsObject : rocks) {
                Player localPlayer = this.client.getLocalPlayer();
                LocalPoint graphicsObjectLocation = graphicsObject.getLocation();
                Polygon polygon2 = Perspective.getCanvasTilePoly((Client)this.client, (LocalPoint)graphicsObjectLocation);
                if (polygon2 == null) continue;
                OverlayUtil.renderPolygon(graphics, polygon2, localPlayer.getLocalLocation().equals((Object)graphicsObjectLocation) ? Color.RED : Color.ORANGE);
            }
        }
        if ((targetedGlowingRock = this.zalcanoPlugin.getTargetedGlowingRock()) != null && this.client.getGameCycle() < this.zalcanoPlugin.getTargetedGlowingRockEndCycle() && (polygon = Perspective.getCanvasTileAreaPoly((Client)this.client, (LocalPoint)targetedGlowingRock, (int)3)) != null) {
            OverlayUtil.renderPolygon(graphics, polygon, Color.RED);
        }
        return null;
    }
}

