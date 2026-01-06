/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.prayer;

import java.awt.Color;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.prayer.PrayerType;
import net.runelite.client.ui.overlay.infobox.InfoBox;

class PrayerCounter
extends InfoBox {
    private final PrayerType prayerType;

    PrayerCounter(Plugin plugin, PrayerType prayerType) {
        super(null, plugin);
        this.prayerType = prayerType;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public Color getTextColor() {
        return null;
    }

    @Override
    public String getTooltip() {
        return this.prayerType.getDescription();
    }

    public PrayerType getPrayerType() {
        return this.prayerType;
    }
}

