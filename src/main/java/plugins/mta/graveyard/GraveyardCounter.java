/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.mta.graveyard;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;

public class GraveyardCounter
extends Counter {
    GraveyardCounter(BufferedImage image, Plugin plugin) {
        super(image, plugin, 0);
    }

    @Override
    public Color getTextColor() {
        int count = this.getCount();
        if (count >= 16) {
            return Color.GREEN;
        }
        if (count == 0) {
            return Color.RED;
        }
        return Color.ORANGE;
    }
}

