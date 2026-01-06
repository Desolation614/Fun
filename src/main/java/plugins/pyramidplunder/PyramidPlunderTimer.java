/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.pyramidplunder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import net.runelite.api.Client;
import net.runelite.client.plugins.pyramidplunder.PyramidPlunderConfig;
import net.runelite.client.plugins.pyramidplunder.PyramidPlunderPlugin;
import net.runelite.client.ui.overlay.infobox.Timer;

class PyramidPlunderTimer
extends Timer {
    private final PyramidPlunderConfig config;
    private final Client client;

    public PyramidPlunderTimer(Duration duration, BufferedImage image, PyramidPlunderPlugin plugin, PyramidPlunderConfig config, Client client) {
        super(duration.toMillis(), ChronoUnit.MILLIS, image, plugin);
        this.config = config;
        this.client = client;
    }

    @Override
    public Color getTextColor() {
        long secondsLeft = Duration.between(Instant.now(), this.getEndTime()).getSeconds();
        return secondsLeft < (long)this.config.timerLowWarning() ? Color.RED.brighter() : Color.white;
    }

    @Override
    public String getTooltip() {
        int floor = this.client.getVarbitValue(2377);
        int thievingLevel = this.client.getVarbitValue(2376);
        return String.format("Time remaining. Floor: %d. Thieving level: %d", floor, thievingLevel);
    }

    @Override
    public boolean render() {
        return this.config.showExactTimer();
    }
}

