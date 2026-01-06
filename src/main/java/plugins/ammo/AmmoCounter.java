/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.ammo;

import java.awt.image.BufferedImage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.util.QuantityFormatter;

class AmmoCounter
extends Counter {
    private final int itemID;
    private final String name;

    AmmoCounter(Plugin plugin, int itemID, int count, String name, BufferedImage image) {
        super(image, plugin, count);
        this.itemID = itemID;
        this.name = name;
    }

    @Override
    public String getText() {
        return QuantityFormatter.quantityToRSDecimalStack(this.getCount());
    }

    @Override
    public String getTooltip() {
        return this.name;
    }

    public int getItemID() {
        return this.itemID;
    }
}

