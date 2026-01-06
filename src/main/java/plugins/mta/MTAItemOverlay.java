/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.widgets.WidgetItem
 */
package net.runelite.client.plugins.mta;

import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.mta.MTAPlugin;
import net.runelite.client.plugins.mta.MTARoom;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

class MTAItemOverlay
extends WidgetItemOverlay {
    private final MTAPlugin plugin;

    @Inject
    public MTAItemOverlay(MTAPlugin plugin) {
        this.plugin = plugin;
        this.showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        for (MTARoom room : this.plugin.getRooms()) {
            if (!room.inside()) continue;
            room.renderItemOverlay(graphics, itemId, widgetItem);
        }
    }
}

