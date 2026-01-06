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
package net.runelite.client.plugins.npchighlight;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.npchighlight.MemorizedNpc;
import net.runelite.client.plugins.npchighlight.NpcIndicatorsConfig;
import net.runelite.client.plugins.npchighlight.NpcIndicatorsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class NpcRespawnOverlay
extends Overlay {
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final NumberFormat TIME_LEFT_FORMATTER = DecimalFormat.getInstance(Locale.US);
    private final Client client;
    private final NpcIndicatorsConfig config;
    private final NpcIndicatorsPlugin plugin;

    @Inject
    NpcRespawnOverlay(Client client, NpcIndicatorsConfig config, NpcIndicatorsPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Map<Integer, MemorizedNpc> deadNpcsToDisplay = this.plugin.getDeadNpcsToDisplay();
        if (deadNpcsToDisplay.isEmpty() || !this.config.showRespawnTimer()) {
            return null;
        }
        deadNpcsToDisplay.forEach((id, npc) -> this.renderNpcRespawn((MemorizedNpc)npc, graphics));
        return null;
    }

    private void renderNpcRespawn(MemorizedNpc npc, Graphics2D graphics) {
        if (npc.getPossibleRespawnLocations().isEmpty()) {
            return;
        }
        WorldPoint respawnLocation = npc.getPossibleRespawnLocations().get(0);
        LocalPoint lp = LocalPoint.fromWorld((Client)this.client, (int)respawnLocation.getX(), (int)respawnLocation.getY());
        if (lp == null) {
            return;
        }
        LocalPoint centerLp = new LocalPoint(lp.getX() + 128 * (npc.getNpcSize() - 1) / 2, lp.getY() + 128 * (npc.getNpcSize() - 1) / 2);
        Polygon poly = Perspective.getCanvasTileAreaPoly((Client)this.client, (LocalPoint)centerLp, (int)npc.getNpcSize());
        this.renderPoly(graphics, this.config.highlightColor(), this.config.fillColor(), poly);
        Instant now = Instant.now();
        double baseTick = (double)(npc.getDiedOnTick() + npc.getRespawnTime() - this.client.getTickCount()) * 0.6;
        double sinceLast = (double)(now.toEpochMilli() - this.plugin.getLastTickUpdate().toEpochMilli()) / 1000.0;
        double timeLeft = Math.max(0.0, baseTick - sinceLast);
        String timeLeftStr = TIME_LEFT_FORMATTER.format(timeLeft);
        int textWidth = graphics.getFontMetrics().stringWidth(timeLeftStr);
        int textHeight = graphics.getFontMetrics().getAscent();
        Point canvasPoint = Perspective.localToCanvas((Client)this.client, (LocalPoint)centerLp, (int)respawnLocation.getPlane());
        if (canvasPoint != null) {
            Point canvasCenterPoint = new Point(canvasPoint.getX() - textWidth / 2, canvasPoint.getY() + textHeight / 2);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, timeLeftStr, TEXT_COLOR);
        }
    }

    private void renderPoly(Graphics2D graphics, Color borderColor, Color fillColor, Shape polygon) {
        if (polygon != null) {
            graphics.setColor(borderColor);
            graphics.setStroke(new BasicStroke((float)this.config.borderWidth()));
            graphics.draw(polygon);
            graphics.setColor(fillColor);
            graphics.fill(polygon);
        }
    }

    static {
        ((DecimalFormat)TIME_LEFT_FORMATTER).applyPattern("#0.0");
    }
}

