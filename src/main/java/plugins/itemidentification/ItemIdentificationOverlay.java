/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  net.runelite.api.widgets.WidgetItem
 */
package net.runelite.client.plugins.itemidentification;

import com.google.inject.Inject;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.itemidentification.ItemIdentification;
import net.runelite.client.plugins.itemidentification.ItemIdentificationConfig;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;

class ItemIdentificationOverlay
extends WidgetItemOverlay {
    private final ItemIdentificationConfig config;
    private final ItemManager itemManager;

    @Inject
    ItemIdentificationOverlay(ItemIdentificationConfig config, ItemManager itemManager) {
        this.config = config;
        this.itemManager = itemManager;
        this.showOnInventory();
        this.showOnBank();
        this.showOnInterfaces(4, 464, 81, 128, 616);
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        ItemIdentification iden = this.findItemIdentification(itemId);
        if (iden == null || !iden.type.enabled.test(this.config)) {
            return;
        }
        graphics.setFont(FontManager.getRunescapeSmallFont());
        this.renderText(graphics, widgetItem.getCanvasBounds(), iden);
    }

    private void renderText(Graphics2D graphics, Rectangle bounds, ItemIdentification iden) {
        TextComponent textComponent = new TextComponent();
        textComponent.setPosition(new Point(bounds.x - 1, bounds.y + bounds.height - 1));
        textComponent.setColor(this.config.textColor());
        switch (this.config.identificationType()) {
            case SHORT: {
                textComponent.setText(iden.shortName);
                break;
            }
            case MEDIUM: {
                textComponent.setText(iden.medName);
            }
        }
        textComponent.render(graphics);
    }

    private ItemIdentification findItemIdentification(int itemID) {
        int realItemId = this.itemManager.canonicalize(itemID);
        return ItemIdentification.get(realItemId);
    }
}

