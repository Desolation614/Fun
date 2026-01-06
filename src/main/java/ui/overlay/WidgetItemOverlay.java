/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetItem
 */
package net.runelite.client.ui.overlay;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayPosition;

public abstract class WidgetItemOverlay
extends Overlay {
    private OverlayManager overlayManager;

    protected WidgetItemOverlay() {
        super.setPosition(OverlayPosition.DYNAMIC);
        super.setPriority(0.0f);
        super.setLayer(OverlayLayer.MANUAL);
    }

    public abstract void renderItemOverlay(Graphics2D var1, int var2, WidgetItem var3);

    @Override
    public Dimension render(Graphics2D graphics) {
        Collection<WidgetItem> widgetItems = this.overlayManager.getWidgetItems();
        Rectangle originalClipBounds = graphics.getClipBounds();
        Widget curClipParent = null;
        for (WidgetItem widgetItem : widgetItems) {
            boolean shouldClip;
            boolean dragging;
            Widget widget = widgetItem.getWidget();
            Widget parent = widget.getParent();
            Rectangle parentBounds = parent.getBounds();
            Rectangle itemCanvasBounds = widgetItem.getCanvasBounds();
            boolean bl = dragging = widgetItem.getDraggingCanvasBounds() != null;
            if (dragging) {
                shouldClip = itemCanvasBounds.x < parentBounds.x;
                shouldClip |= itemCanvasBounds.x + itemCanvasBounds.width >= parentBounds.x + parentBounds.width;
                shouldClip |= itemCanvasBounds.y < parentBounds.y;
                shouldClip |= itemCanvasBounds.y + itemCanvasBounds.height >= parentBounds.y + parentBounds.height;
            } else {
                shouldClip = itemCanvasBounds.y < parentBounds.y && itemCanvasBounds.y + itemCanvasBounds.height >= parentBounds.y;
                shouldClip |= itemCanvasBounds.y < parentBounds.y + parentBounds.height && itemCanvasBounds.y + itemCanvasBounds.height >= parentBounds.y + parentBounds.height;
                shouldClip |= itemCanvasBounds.x < parentBounds.x && itemCanvasBounds.x + itemCanvasBounds.width >= parentBounds.x;
                shouldClip |= itemCanvasBounds.x < parentBounds.x + parentBounds.width && itemCanvasBounds.x + itemCanvasBounds.width >= parentBounds.x + parentBounds.width;
            }
            if (shouldClip) {
                if (curClipParent != parent) {
                    graphics.setClip(parentBounds);
                    curClipParent = parent;
                }
            } else if (curClipParent != null && curClipParent != parent) {
                graphics.setClip(originalClipBounds);
                curClipParent = null;
            }
            this.renderItemOverlay(graphics, widgetItem.getId(), widgetItem);
        }
        return null;
    }

    protected void showOnInventory() {
        this.showOnInterfaces(192, 15, 301, 467, 238, 85, 149, 630, 335, 336, 674);
    }

    protected void showOnBank() {
        this.drawAfterLayer(786445);
        this.drawAfterLayer(47448074);
    }

    protected void showOnEquipment() {
        this.showOnInterfaces(387);
    }

    protected void showOnInterfaces(int ... ids) {
        Arrays.stream(ids).forEach(this::drawAfterInterface);
    }

    @Override
    public void setPosition(OverlayPosition position) {
        throw new IllegalStateException();
    }

    @Override
    public void setLayer(OverlayLayer layer) {
        throw new IllegalStateException();
    }

    void setOverlayManager(OverlayManager overlayManager) {
        this.overlayManager = overlayManager;
    }
}

