/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package net.runelite.client.plugins.screenmarkers;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import lombok.NonNull;
import net.runelite.client.plugins.screenmarkers.ScreenMarker;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerRenderable;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class ScreenMarkerOverlay
extends Overlay {
    private final ScreenMarker marker;
    private final ScreenMarkerRenderable screenMarkerRenderable;

    ScreenMarkerOverlay(@NonNull ScreenMarker marker) {
        if (marker == null) {
            throw new NullPointerException("marker is marked non-null but is null");
        }
        this.marker = marker;
        this.screenMarkerRenderable = new ScreenMarkerRenderable();
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
        this.setPriority(0.75f);
        this.setMovable(true);
        this.setResizable(true);
        this.setMinimumSize(16);
        this.setResettable(false);
    }

    @Override
    public String getName() {
        return "marker" + this.marker.getId();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.marker.isVisible()) {
            return null;
        }
        Dimension preferredSize = this.getPreferredSize();
        if (preferredSize == null) {
            return null;
        }
        this.screenMarkerRenderable.setBorderThickness(this.marker.getBorderThickness());
        this.screenMarkerRenderable.setColor(this.marker.getColor());
        this.screenMarkerRenderable.setFill(this.marker.getFill());
        this.screenMarkerRenderable.setStroke(new BasicStroke(this.marker.getBorderThickness()));
        this.screenMarkerRenderable.setSize(preferredSize);
        this.screenMarkerRenderable.setLabel(this.marker.isLabelled() ? this.marker.getName() : "");
        return this.screenMarkerRenderable.render(graphics);
    }

    public ScreenMarker getMarker() {
        return this.marker;
    }
}

