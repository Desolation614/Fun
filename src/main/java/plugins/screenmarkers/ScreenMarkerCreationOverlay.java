/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.screenmarkers;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import javax.inject.Inject;
import net.runelite.client.plugins.screenmarkers.ScreenMarker;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class ScreenMarkerCreationOverlay
extends Overlay {
    private final ScreenMarkerPlugin plugin;

    @Inject
    private ScreenMarkerCreationOverlay(ScreenMarkerPlugin plugin) {
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.setPriority(0.75f);
        this.setMovable(true);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        ScreenMarker marker = this.plugin.getCurrentMarker();
        if (marker == null) {
            return null;
        }
        int thickness = marker.getBorderThickness();
        int offset = thickness / 2;
        int width = this.getBounds().width - thickness;
        int height = this.getBounds().height - thickness;
        graphics.setStroke(this.createStripedStroke(thickness));
        graphics.setColor(marker.getColor());
        graphics.drawRect(offset, offset, width, height);
        return this.getBounds().getSize();
    }

    private Stroke createStripedStroke(int thickness) {
        return new BasicStroke(thickness, 0, 2, 0.0f, new float[]{9.0f}, 0.0f);
    }
}

