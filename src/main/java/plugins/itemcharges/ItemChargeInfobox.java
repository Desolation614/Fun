/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.itemcharges;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.itemcharges.ItemChargePlugin;
import net.runelite.client.ui.overlay.infobox.Counter;

class ItemChargeInfobox
extends Counter {
    private final ItemChargePlugin plugin;
    private final int item;

    ItemChargeInfobox(ItemChargePlugin plugin, BufferedImage image, String name, int charges, int item) {
        super(image, plugin, charges);
        this.setTooltip(name);
        this.plugin = plugin;
        this.item = item;
    }

    @Override
    public Color getTextColor() {
        return this.getPlugin().getColor(this.getCount());
    }

    public ItemChargePlugin getPlugin() {
        return this.plugin;
    }

    public int getItem() {
        return this.item;
    }

    @Override
    public String toString() {
        return "ItemChargeInfobox(plugin=" + String.valueOf(this.getPlugin()) + ", item=" + this.getItem() + ")";
    }
}

