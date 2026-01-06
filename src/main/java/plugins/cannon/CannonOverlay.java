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
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cannon.CannonConfig;
import net.runelite.client.plugins.cannon.CannonPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.TextComponent;

class CannonOverlay
extends Overlay {
    private final Client client;
    private final CannonConfig config;
    private final CannonPlugin plugin;
    private final TextComponent textComponent = new TextComponent();

    @Inject
    CannonOverlay(Client client, CannonConfig config, CannonPlugin plugin) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(0.5f);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.plugin.isCannonPlaced() || this.plugin.getCannonPosition() == null || this.plugin.getCannonWorld() != this.client.getWorld()) {
            return null;
        }
        WorldPoint cannonLocation = this.plugin.getCannonPosition().toWorldPoint().dx(1).dy(1);
        LocalPoint cannonPoint = LocalPoint.fromWorld((Client)this.client, (WorldPoint)cannonLocation);
        if (cannonPoint == null) {
            return null;
        }
        LocalPoint localLocation = this.client.getLocalPlayer().getLocalLocation();
        if (localLocation.distanceTo(cannonPoint) <= 4100) {
            Point cannonLoc = Perspective.getCanvasTextLocation((Client)this.client, (Graphics2D)graphics, (LocalPoint)cannonPoint, (String)String.valueOf(this.plugin.getCballsLeft()), (int)150);
            if (cannonLoc != null) {
                this.textComponent.setText(String.valueOf(this.plugin.getCballsLeft()));
                this.textComponent.setPosition(new java.awt.Point(cannonLoc.getX(), cannonLoc.getY()));
                this.textComponent.setColor(this.plugin.getStateColor());
                this.textComponent.render(graphics);
            }
            if (this.config.showDoubleHitSpot()) {
                Color color = this.config.highlightDoubleHitColor();
                this.drawDoubleHitSpots(graphics, cannonPoint, color);
            }
        }
        return null;
    }

    private void drawDoubleHitSpots(Graphics2D graphics, LocalPoint startTile, Color color) {
        for (int x = -3; x <= 3; ++x) {
            for (int y = -3; y <= 3; ++y) {
                int yPos;
                int xPos;
                LocalPoint marker;
                Polygon poly;
                if (y != 1 && x != 1 && y != -1 && x != -1 || y >= -1 && y <= 1 && x >= -1 && x <= 1 || (poly = Perspective.getCanvasTilePoly((Client)this.client, (LocalPoint)(marker = new LocalPoint(xPos = startTile.getX() - x * 128, yPos = startTile.getY() - y * 128)))) == null) continue;
                OverlayUtil.renderPolygon(graphics, poly, color);
            }
        }
    }
}

