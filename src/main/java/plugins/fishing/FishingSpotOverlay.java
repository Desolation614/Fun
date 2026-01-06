/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.NPC
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.fishing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.fishing.FishingConfig;
import net.runelite.client.plugins.fishing.FishingPlugin;
import net.runelite.client.plugins.fishing.MinnowSpot;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.client.util.ImageUtil;

class FishingSpotOverlay
extends Overlay {
    private static final Duration MINNOW_MOVE = Duration.ofSeconds(15L);
    private static final Duration MINNOW_WARN = Duration.ofSeconds(3L);
    private static final int ONE_TICK_AERIAL_FISHING = 3;
    private final FishingPlugin plugin;
    private final FishingConfig config;
    private final Client client;
    private final ItemManager itemManager;
    private boolean hidden;

    @Inject
    private FishingSpotOverlay(FishingPlugin plugin, FishingConfig config, Client client, ItemManager itemManager) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        this.itemManager = itemManager;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.hidden) {
            return null;
        }
        FishingSpot previousSpot = null;
        WorldPoint previousLocation = null;
        for (NPC npc : this.plugin.getFishingSpots()) {
            String text;
            Point textLocation;
            Polygon poly;
            MinnowSpot minnowSpot;
            FishingSpot spot = FishingSpot.findSpot(npc.getId());
            if (spot == null || this.config.onlyCurrentSpot() && this.plugin.getCurrentSpot() != null && this.plugin.getCurrentSpot() != spot || previousSpot == spot && previousLocation.equals((Object)npc.getWorldLocation())) continue;
            Color color = npc.getGraphic() == 1387 ? this.config.getMinnowsOverlayColor() : (spot == FishingSpot.COMMON_TENCH && npc.getWorldLocation().distanceTo2D(this.client.getLocalPlayer().getWorldLocation()) <= 3 ? this.config.getAerialOverlayColor() : (spot == FishingSpot.HARPOONFISH && npc.getId() == 10569 ? this.config.getHarpoonfishOverlayColor() : this.config.getOverlayColor()));
            if (spot == FishingSpot.MINNOW && this.config.showMinnowOverlay() && (minnowSpot = this.plugin.getMinnowSpots().get(npc.getIndex())) != null) {
                LocalPoint localPoint;
                Point location;
                long millisLeft = MINNOW_MOVE.toMillis() - Duration.between(minnowSpot.getTime(), Instant.now()).toMillis();
                if (millisLeft < MINNOW_WARN.toMillis()) {
                    color = Color.ORANGE;
                }
                if ((location = Perspective.localToCanvas((Client)this.client, (LocalPoint)(localPoint = npc.getLocalLocation()), (int)this.client.getPlane())) != null) {
                    ProgressPieComponent pie = new ProgressPieComponent();
                    pie.setFill(color);
                    pie.setBorderColor(color);
                    pie.setPosition(location);
                    pie.setProgress((float)millisLeft / (float)MINNOW_MOVE.toMillis());
                    pie.render(graphics);
                }
            }
            if (this.config.showSpotTiles() && (poly = npc.getCanvasTilePoly()) != null) {
                OverlayUtil.renderPolygon(graphics, poly, color.darker());
            }
            if (this.config.showSpotIcons()) {
                Point imageLocation;
                BufferedImage fishImage = this.itemManager.getImage(spot.getFishSpriteId());
                if (spot == FishingSpot.COMMON_TENCH && npc.getWorldLocation().distanceTo2D(this.client.getLocalPlayer().getWorldLocation()) <= 3) {
                    fishImage = ImageUtil.outlineImage(this.itemManager.getImage(spot.getFishSpriteId()), color);
                }
                if (fishImage != null && (imageLocation = npc.getCanvasImageLocation(fishImage, npc.getLogicalHeight())) != null) {
                    OverlayUtil.renderImageLocation(graphics, imageLocation, fishImage);
                }
            }
            if (this.config.showSpotNames() && (textLocation = npc.getCanvasTextLocation(graphics, text = spot.getName(), npc.getLogicalHeight() + 40)) != null) {
                OverlayUtil.renderTextLocation(graphics, textLocation, text, color.darker());
            }
            previousSpot = spot;
            previousLocation = npc.getWorldLocation();
        }
        return null;
    }

    void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}

