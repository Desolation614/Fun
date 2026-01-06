/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timersandbuffs;

import java.awt.Color;
import net.runelite.client.plugins.timersandbuffs.GameCounter;
import net.runelite.client.plugins.timersandbuffs.TimersAndBuffsPlugin;
import net.runelite.client.ui.overlay.infobox.Counter;

class BuffCounter
extends Counter {
    private final TimersAndBuffsPlugin plugin;
    private final GameCounter gameCounter;

    BuffCounter(TimersAndBuffsPlugin plugin, GameCounter gameCounter, int count) {
        super(null, plugin, count);
        this.plugin = plugin;
        this.gameCounter = gameCounter;
    }

    @Override
    public String getText() {
        return this.gameCounter.isShouldDisplayCount() ? Integer.toString(this.getCount()) : "";
    }

    @Override
    public Color getTextColor() {
        return this.gameCounter.getColorBoundaryType().shouldRecolor(this.getCount(), this.gameCounter.getBoundary()) ? this.gameCounter.getColor() : Color.WHITE;
    }

    TimersAndBuffsPlugin getPlugin() {
        return this.plugin;
    }

    GameCounter getGameCounter() {
        return this.gameCounter;
    }
}

