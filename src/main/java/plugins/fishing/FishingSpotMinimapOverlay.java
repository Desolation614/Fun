/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.NPC
 *  net.runelite.api.Point
 */
package net.runelite.client.plugins.fishing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.plugins.fishing.FishingConfig;
import net.runelite.client.plugins.fishing.FishingPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class FishingSpotMinimapOverlay
extends Overlay {
    private final FishingPlugin plugin;
    private final FishingConfig config;
    private boolean hidden;

    @Inject
    public FishingSpotMinimapOverlay(FishingPlugin plugin, FishingConfig config) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.hidden) {
            return null;
        }
        for (NPC npc : this.plugin.getFishingSpots()) {
            Color color;
            FishingSpot spot = FishingSpot.findSpot(npc.getId());
            if (spot == null || this.config.onlyCurrentSpot() && this.plugin.getCurrentSpot() != null && this.plugin.getCurrentSpot() != spot) continue;
            Color color2 = npc.getGraphic() == 1387 ? this.config.getMinnowsOverlayColor() : (color = npc.getId() == 10569 ? this.config.getHarpoonfishOverlayColor() : this.config.getOverlayColor());
            Point minimapLocation = npc.getMinimapLocation();
            if (minimapLocation == null) continue;
            OverlayUtil.renderMinimapLocation(graphics, minimapLocation, color.darker());
        }
        return null;
    }

    void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}

