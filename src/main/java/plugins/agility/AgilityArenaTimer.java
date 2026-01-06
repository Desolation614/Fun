/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.agility;

import java.awt.image.BufferedImage;
import java.time.temporal.ChronoUnit;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Timer;

class AgilityArenaTimer
extends Timer {
    AgilityArenaTimer(Plugin plugin, BufferedImage image) {
        super(1L, ChronoUnit.MINUTES, image, plugin);
        this.setTooltip("Time left until location changes");
    }
}

