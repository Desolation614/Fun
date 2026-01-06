/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.widgets.WidgetItem
 */
package net.runelite.client.plugins.itemcharges;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.itemcharges.ItemChargeConfig;
import net.runelite.client.plugins.itemcharges.ItemChargePlugin;
import net.runelite.client.plugins.itemcharges.ItemChargeType;
import net.runelite.client.plugins.itemcharges.ItemWithCharge;
import net.runelite.client.plugins.itemcharges.ItemWithConfig;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;

class ItemChargeOverlay
extends WidgetItemOverlay {
    private final ItemChargePlugin itemChargePlugin;
    private final ItemChargeConfig config;

    @Inject
    ItemChargeOverlay(ItemChargePlugin itemChargePlugin, ItemChargeConfig config) {
        this.itemChargePlugin = itemChargePlugin;
        this.config = config;
        this.showOnInventory();
        this.showOnEquipment();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        int charges;
        ItemWithConfig itemWithConfig = ItemWithConfig.findItem(itemId);
        if (itemWithConfig != null) {
            if (!itemWithConfig.getType().getEnabled().test(this.config)) {
                return;
            }
            charges = this.itemChargePlugin.getItemCharges(itemWithConfig.getConfigKey());
        } else {
            ItemWithCharge chargeItem = ItemWithCharge.findItem(itemId);
            if (chargeItem == null) {
                return;
            }
            ItemChargeType type = chargeItem.getType();
            if (!type.getEnabled().test(this.config)) {
                return;
            }
            charges = chargeItem.getCharges();
        }
        graphics.setFont(FontManager.getRunescapeSmallFont());
        Rectangle bounds = widgetItem.getCanvasBounds();
        TextComponent textComponent = new TextComponent();
        textComponent.setPosition(new Point(bounds.x - 1, bounds.y + 15));
        textComponent.setText(charges < 0 ? "?" : String.valueOf(charges));
        textComponent.setColor(this.itemChargePlugin.getColor(charges));
        textComponent.render(graphics);
    }
}

