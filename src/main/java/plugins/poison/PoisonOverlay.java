/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Point
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.poison;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.poison.PoisonPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

class PoisonOverlay
extends Overlay {
    private final PoisonPlugin plugin;
    private final Client client;
    private final TooltipManager tooltipManager;

    @Inject
    private PoisonOverlay(PoisonPlugin plugin, Client client, TooltipManager tooltipManager) {
        this.plugin = plugin;
        this.client = client;
        this.tooltipManager = tooltipManager;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.plugin.getLastDamage() <= 0) {
            return null;
        }
        Widget healthOrb = this.client.getWidget(0xA00006);
        if (healthOrb == null || healthOrb.isHidden()) {
            return null;
        }
        Rectangle bounds = healthOrb.getBounds();
        if (bounds.getX() <= 0.0) {
            return null;
        }
        Point mousePosition = this.client.getMouseCanvasPosition();
        if (bounds.contains(mousePosition.getX(), mousePosition.getY())) {
            this.tooltipManager.add(new Tooltip(this.plugin.createTooltip()));
        }
        return null;
    }
}

