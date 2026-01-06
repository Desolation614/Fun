/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.screenmarkers;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerPlugin;

class ScreenMarkerMouseListener
extends MouseAdapter {
    private final ScreenMarkerPlugin plugin;

    ScreenMarkerMouseListener(ScreenMarkerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public MouseEvent mouseClicked(MouseEvent event) {
        if (SwingUtilities.isMiddleMouseButton(event)) {
            return event;
        }
        event.consume();
        return event;
    }

    @Override
    public MouseEvent mousePressed(MouseEvent event) {
        if (SwingUtilities.isMiddleMouseButton(event)) {
            return event;
        }
        if (SwingUtilities.isLeftMouseButton(event)) {
            Rectangle bounds = this.plugin.getSelectedWidgetBounds();
            if (bounds != null) {
                this.plugin.startCreation(bounds.getLocation(), bounds.getSize());
            } else {
                this.plugin.startCreation(event.getPoint());
            }
        } else if (this.plugin.isCreatingScreenMarker()) {
            this.plugin.finishCreation(true);
        }
        event.consume();
        return event;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent event) {
        if (SwingUtilities.isMiddleMouseButton(event)) {
            return event;
        }
        if (SwingUtilities.isLeftMouseButton(event) && this.plugin.isCreatingScreenMarker()) {
            this.plugin.completeSelection();
        }
        event.consume();
        return event;
    }

    @Override
    public MouseEvent mouseDragged(MouseEvent event) {
        if (!this.plugin.isCreatingScreenMarker()) {
            return event;
        }
        if (SwingUtilities.isLeftMouseButton(event)) {
            this.plugin.resizeMarker(event.getPoint());
        }
        return event;
    }
}

