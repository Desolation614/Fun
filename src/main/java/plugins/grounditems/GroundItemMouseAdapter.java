/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.grounditems;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.plugins.grounditems.GroundItemsPlugin;

class GroundItemMouseAdapter
extends MouseAdapter {
    @Inject
    private GroundItemsPlugin plugin;

    GroundItemMouseAdapter() {
    }

    @Override
    public MouseEvent mousePressed(MouseEvent e) {
        Point mousePos = e.getPoint();
        if (this.plugin.isHotKeyPressed()) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (this.plugin.getHiddenBoxBounds() != null && this.plugin.getHiddenBoxBounds().getKey().contains(mousePos)) {
                    this.plugin.updateList(this.plugin.getHiddenBoxBounds().getValue().getName(), true);
                    e.consume();
                    return e;
                }
                if (this.plugin.getHighlightBoxBounds() != null && this.plugin.getHighlightBoxBounds().getKey().contains(mousePos)) {
                    this.plugin.updateList(this.plugin.getHighlightBoxBounds().getValue().getName(), false);
                    e.consume();
                    return e;
                }
                if (this.plugin.getTextBoxBounds() != null && this.plugin.getTextBoxBounds().getKey().contains(mousePos)) {
                    this.plugin.updateList(this.plugin.getTextBoxBounds().getValue().getName(), false);
                    e.consume();
                    return e;
                }
            } else if (SwingUtilities.isRightMouseButton(e) && this.plugin.getTextBoxBounds() != null && this.plugin.getTextBoxBounds().getKey().contains(mousePos)) {
                this.plugin.updateList(this.plugin.getTextBoxBounds().getValue().getName(), true);
                e.consume();
                return e;
            }
        }
        return e;
    }
}

