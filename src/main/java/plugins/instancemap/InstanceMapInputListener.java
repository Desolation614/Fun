/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.instancemap;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseWheelListener;
import net.runelite.client.plugins.instancemap.InstanceMapOverlay;
import net.runelite.client.plugins.instancemap.InstanceMapPlugin;

public class InstanceMapInputListener
extends MouseAdapter
implements KeyListener,
MouseWheelListener {
    @Inject
    private InstanceMapPlugin plugin;
    @Inject
    private InstanceMapOverlay overlay;

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (!this.overlay.isMapShown()) {
            return;
        }
        if (event.getKeyCode() == 27) {
            this.plugin.closeMap();
            event.consume();
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

    @Override
    public MouseWheelEvent mouseWheelMoved(MouseWheelEvent event) {
        if (!this.overlay.isMapShown() || this.isNotWithinOverlay(event.getPoint())) {
            return event;
        }
        int direction = event.getWheelRotation();
        if (direction > 0) {
            this.plugin.ascendMap();
        } else {
            this.plugin.descendMap();
        }
        event.consume();
        return event;
    }

    @Override
    public MouseEvent mouseClicked(MouseEvent event) {
        if (!this.overlay.isMapShown() || this.isNotWithinOverlay(event.getPoint())) {
            return event;
        }
        event.consume();
        return event;
    }

    @Override
    public MouseEvent mousePressed(MouseEvent event) {
        if (!this.overlay.isMapShown() || this.isNotWithinOverlay(event.getPoint())) {
            return event;
        }
        if (SwingUtilities.isLeftMouseButton(event) && this.isWithinCloseButton(event.getPoint())) {
            this.plugin.closeMap();
        }
        event.consume();
        return event;
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent event) {
        if (this.overlay.isMapShown()) {
            this.overlay.setCloseButtonHovered(this.isWithinCloseButton(event.getPoint()));
        }
        return event;
    }

    private boolean isNotWithinOverlay(Point point) {
        return !this.overlay.getBounds().contains(point);
    }

    private boolean isWithinCloseButton(Point point) {
        Point overlayPoint = new Point(point.x - (int)this.overlay.getBounds().getX(), point.y - (int)this.overlay.getBounds().getY());
        return this.overlay.getCloseButtonBounds() != null && this.overlay.getCloseButtonBounds().contains(overlayPoint);
    }
}

