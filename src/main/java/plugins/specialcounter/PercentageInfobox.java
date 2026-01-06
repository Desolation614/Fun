/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.specialcounter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.specialcounter.SpecialCounterPlugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

class PercentageInfobox
extends InfoBox {
    private float percent = 1.0f;

    PercentageInfobox(BufferedImage image, SpecialCounterPlugin plugin) {
        super(image, plugin);
    }

    void mul(float p) {
        this.percent *= p;
    }

    @Override
    public String getTooltip() {
        return "Opponent defence has been reduced by " + this.getText() + ".";
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    @Override
    public String getText() {
        return (int)((1.0f - this.percent) * 100.0f) + "%";
    }
}

