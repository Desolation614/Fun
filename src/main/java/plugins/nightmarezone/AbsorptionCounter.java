/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.nightmarezone;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;

public class AbsorptionCounter
extends Counter {
    private int threshold;
    private Color aboveThresholdColor = Color.GREEN;
    private Color belowThresholdColor = Color.RED;

    AbsorptionCounter(BufferedImage image, Plugin plugin, int absorption, int threshold) {
        super(image, plugin, absorption);
        this.threshold = threshold;
    }

    @Override
    public Color getTextColor() {
        int absorption = this.getCount();
        if (absorption >= this.threshold) {
            return this.aboveThresholdColor;
        }
        return this.belowThresholdColor;
    }

    @Override
    public String getTooltip() {
        int absorption = this.getCount();
        return "Absorption: " + absorption;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setAboveThresholdColor(Color aboveThresholdColor) {
        this.aboveThresholdColor = aboveThresholdColor;
    }

    public void setBelowThresholdColor(Color belowThresholdColor) {
        this.belowThresholdColor = belowThresholdColor;
    }
}

