/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.time.DurationFormatUtils
 */
package net.runelite.client.plugins.timersandbuffs;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import net.runelite.client.plugins.timersandbuffs.TimersAndBuffsPlugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import org.apache.commons.lang3.time.DurationFormatUtils;

class ElapsedTimer
extends InfoBox {
    private final Instant startTime;
    private final Instant lastTime;

    ElapsedTimer(BufferedImage image, TimersAndBuffsPlugin plugin, Instant startTime, Instant lastTime) {
        super(image, plugin);
        this.startTime = startTime;
        this.lastTime = lastTime;
    }

    @Override
    public String getText() {
        if (this.startTime == null) {
            return null;
        }
        Duration time = Duration.between(this.startTime, this.lastTime == null ? Instant.now() : this.lastTime);
        String formatString = "mm:ss";
        return DurationFormatUtils.formatDuration((long)time.toMillis(), (String)"mm:ss", (boolean)true);
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    @Override
    public String getTooltip() {
        if (this.startTime == null) {
            return null;
        }
        Duration time = Duration.between(this.startTime, this.lastTime == null ? Instant.now() : this.lastTime);
        return "Elapsed time: " + DurationFormatUtils.formatDuration((long)time.toMillis(), (String)"HH:mm:ss", (boolean)true);
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Instant getLastTime() {
        return this.lastTime;
    }
}

