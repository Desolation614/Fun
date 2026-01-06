/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.overlay.infobox;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class Counter
extends InfoBox {
    private int count;

    public Counter(BufferedImage image, Plugin plugin, int count) {
        super(image, plugin);
        this.count = count;
    }

    @Override
    public String getText() {
        return Integer.toString(this.getCount());
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    public String toString() {
        return "Counter(count=" + this.getCount() + ")";
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

