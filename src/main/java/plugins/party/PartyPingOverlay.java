/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Perspective
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.party;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.party.PartyPlugin;
import net.runelite.client.plugins.party.data.PartyTilePingData;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class PartyPingOverlay
extends Overlay {
    private final Client client;
    private final PartyPlugin plugin;

    @Inject
    private PartyPingOverlay(Client client, PartyPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.plugin.getPartyDataMap().isEmpty()) {
            return null;
        }
        List<PartyTilePingData> list = this.plugin.getPendingTilePings();
        synchronized (list) {
            Iterator<PartyTilePingData> iterator = this.plugin.getPendingTilePings().iterator();
            while (iterator.hasNext()) {
                PartyTilePingData next = iterator.next();
                if (next.getAlpha() <= 0) {
                    iterator.remove();
                    continue;
                }
                this.renderPing(graphics, next);
                long elapsedTimeMillis = (System.nanoTime() - next.getCreationTime()) / 1000000L;
                next.setAlpha((int)Math.max(0L, 255L - elapsedTimeMillis / 4L));
            }
        }
        return null;
    }

    private void renderPing(Graphics2D graphics, PartyTilePingData ping) {
        LocalPoint localPoint = LocalPoint.fromWorld((Client)this.client, (WorldPoint)ping.getPoint());
        if (localPoint == null) {
            return;
        }
        Polygon poly = Perspective.getCanvasTilePoly((Client)this.client, (LocalPoint)localPoint);
        if (poly == null) {
            return;
        }
        Color color = new Color(ping.getColor().getRed(), ping.getColor().getGreen(), ping.getColor().getBlue(), ping.getAlpha());
        OverlayUtil.renderPolygon(graphics, poly, color);
    }
}

