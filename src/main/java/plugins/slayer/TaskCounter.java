/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.slayer;

import java.awt.image.BufferedImage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;

class TaskCounter
extends Counter {
    TaskCounter(BufferedImage img, Plugin plugin, int amount) {
        super(img, plugin, amount);
    }
}

