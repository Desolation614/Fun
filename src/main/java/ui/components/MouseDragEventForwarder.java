/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public class MouseDragEventForwarder
extends MouseAdapter {
    private final Component target;

    public MouseDragEventForwarder(Component target) {
        this.target = target;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.processEvent(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.processEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.processEvent(e);
    }

    private void processEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            MouseEvent eventForTarget = SwingUtilities.convertMouseEvent((Component)e.getSource(), e, this.target);
            this.target.dispatchEvent(eventForTarget);
        }
    }
}

