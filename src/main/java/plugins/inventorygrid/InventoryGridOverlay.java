/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Point
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetItem
 */
package net.runelite.client.plugins.inventorygrid;

import com.google.inject.Inject;
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.inventorygrid.InventoryGridConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.AsyncBufferedImage;

class InventoryGridOverlay
extends Overlay {
    private static final int INVENTORY_SIZE = 28;
    private static final int DISTANCE_TO_ACTIVATE_HOVER = 5;
    private final InventoryGridConfig config;
    private final Client client;
    private final ItemManager itemManager;
    private java.awt.Point initialMousePoint;
    private boolean hoverActive = false;

    @Inject
    private InventoryGridOverlay(InventoryGridConfig config, Client client, ItemManager itemManager) {
        this.itemManager = itemManager;
        this.client = client;
        this.config = config;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget draggingWidget = this.client.getDraggedWidget();
        if (draggingWidget == null) {
            this.initialMousePoint = null;
            this.hoverActive = false;
            return null;
        }
        assert (draggingWidget.isIf3());
        if (draggingWidget.getId() != 983043 && draggingWidget.getId() != 983044 && draggingWidget.getId() != 0x950000) {
            return null;
        }
        Widget inventoryWidget = draggingWidget.getParent();
        Point mouse = this.client.getMouseCanvasPosition();
        java.awt.Point mousePoint = new java.awt.Point(mouse.getX(), mouse.getY());
        int draggedItemIndex = draggingWidget.getIndex();
        WidgetItem draggedItem = InventoryGridOverlay.getWidgetItem(inventoryWidget, draggedItemIndex);
        Rectangle initialBounds = draggedItem.getCanvasBounds(false);
        if (this.initialMousePoint == null) {
            this.initialMousePoint = mousePoint;
        }
        if (draggedItem.getId() == -1 || this.client.getDragTime() <= draggingWidget.getDragDeadTime() || !this.hoverActive && this.initialMousePoint.distance(mousePoint) < 5.0) {
            return null;
        }
        this.hoverActive = true;
        for (int i = 0; i < 28; ++i) {
            WidgetItem targetWidgetItem = InventoryGridOverlay.getWidgetItem(inventoryWidget, i);
            Rectangle bounds = targetWidgetItem.getCanvasBounds(false);
            boolean inBounds = bounds.contains(mousePoint);
            if (this.config.showItem() && inBounds) {
                this.drawItem(graphics, bounds, draggedItem);
                this.drawItem(graphics, initialBounds, targetWidgetItem);
            }
            if (this.config.showHighlight() && inBounds) {
                graphics.setColor(this.config.highlightColor());
                graphics.fill(bounds);
                continue;
            }
            if (!this.config.showGrid()) continue;
            graphics.setColor(this.config.gridColor());
            graphics.fill(bounds);
        }
        return null;
    }

    private static WidgetItem getWidgetItem(Widget parentWidget, int idx) {
        assert (parentWidget.isIf3());
        Widget wi = parentWidget.getChild(idx);
        return new WidgetItem(wi.getItemId(), wi.getItemQuantity(), wi.getBounds(), parentWidget, wi.getBounds());
    }

    private void drawItem(Graphics2D graphics, Rectangle bounds, WidgetItem item) {
        if (item.getId() == -1) {
            return;
        }
        AsyncBufferedImage draggedItemImage = this.itemManager.getImage(item.getId(), item.getQuantity(), false);
        int x = (int)bounds.getX();
        int y = (int)bounds.getY();
        graphics.setComposite(AlphaComposite.SrcOver.derive(0.3f));
        graphics.drawImage((Image)draggedItemImage, x, y, null);
        graphics.setComposite(AlphaComposite.SrcOver);
    }
}

