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
package net.runelite.client.plugins.implings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.implings.ImplingSpawn;
import net.runelite.client.plugins.implings.ImplingsConfig;
import net.runelite.client.plugins.implings.ImplingsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class ImplingsOverlay
extends Overlay {
    private final Client client;
    private final ImplingsConfig config;
    private final ImplingsPlugin plugin;

    @Inject
    private ImplingsOverlay(Client client, ImplingsConfig config, ImplingsPlugin plugin) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.config = config;
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.config.showSpawn()) {
            for (ImplingSpawn spawn : ImplingSpawn.values()) {
                if (this.plugin.showImplingType(spawn.getType()) == ImplingsConfig.ImplingMode.NONE) continue;
                String impName = spawn.getType().getName();
                this.drawSpawn(graphics, spawn.getSpawnLocation(), impName, this.config.getSpawnColor());
            }
        }
        return null;
    }

    private void drawSpawn(Graphics2D graphics, WorldPoint point, String text, Color color) {
        Point textPoint;
        if (point.distanceTo(this.client.getLocalPlayer().getWorldLocation()) >= 32) {
            return;
        }
        LocalPoint localPoint = LocalPoint.fromWorld((Client)this.client, (WorldPoint)point);
        if (localPoint == null) {
            return;
        }
        Polygon poly = Perspective.getCanvasTilePoly((Client)this.client, (LocalPoint)localPoint);
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }
        if ((textPoint = Perspective.getCanvasTextLocation((Client)this.client, (Graphics2D)graphics, (LocalPoint)localPoint, (String)text, (int)0)) != null) {
            OverlayUtil.renderTextLocation(graphics, textPoint, text, color);
        }
    }
}

