/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  net.runelite.api.Point
 *  net.runelite.api.TileObject
 */
package net.runelite.client.plugins.herbiboars;

import com.google.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Set;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.client.plugins.herbiboars.HerbiboarConfig;
import net.runelite.client.plugins.herbiboars.HerbiboarPlugin;
import net.runelite.client.plugins.herbiboars.TrailToSpot;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class HerbiboarMinimapOverlay
extends Overlay {
    private final HerbiboarPlugin plugin;
    private final HerbiboarConfig config;

    @Inject
    public HerbiboarMinimapOverlay(HerbiboarPlugin plugin, HerbiboarConfig config) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.config.isTrailShown() || !this.plugin.isInHerbiboarArea()) {
            return null;
        }
        TrailToSpot nextTrail = this.plugin.getNextTrail();
        int finishId = this.plugin.getFinishId();
        Set<Integer> shownTrailIds = this.plugin.getShownTrails();
        for (TileObject tileObject : this.plugin.getTrails().values()) {
            int id = tileObject.getId();
            Point minimapLocation = tileObject.getMinimapLocation();
            if (minimapLocation == null || !shownTrailIds.contains(id) || finishId <= 0 && (nextTrail == null || nextTrail.getFootprintIds().contains(id))) continue;
            OverlayUtil.renderMinimapLocation(graphics, minimapLocation, this.config.getTrailColor());
        }
        return null;
    }
}

