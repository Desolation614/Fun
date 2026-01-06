/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.cannon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.cannon.CannonPlugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

class CannonCounter
extends InfoBox {
    private final CannonPlugin plugin;

    CannonCounter(BufferedImage img, CannonPlugin plugin) {
        super(img, plugin);
        this.plugin = plugin;
    }

    @Override
    public String getText() {
        return String.valueOf(this.plugin.getCballsLeft());
    }

    @Override
    public Color getTextColor() {
        return this.plugin.getStateColor();
    }
}

