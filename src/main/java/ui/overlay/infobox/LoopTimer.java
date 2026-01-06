/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package net.runelite.client.ui.overlay.infobox;

import com.google.common.base.Preconditions;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class LoopTimer
extends InfoBox {
    private final Instant startTime;
    private final Duration duration;
    private final boolean reverse;

    public LoopTimer(long period, ChronoUnit unit, BufferedImage image, Plugin plugin, boolean reverse) {
        super(image, plugin);
        Preconditions.checkArgument((period > 0L ? 1 : 0) != 0, (Object)"negative period!");
        this.startTime = Instant.now();
        this.duration = Duration.of(period, unit);
        this.reverse = reverse;
    }

    public LoopTimer(long period, ChronoUnit unit, BufferedImage image, Plugin plugin) {
        this(period, unit, image, plugin, false);
    }

    @Override
    public String getText() {
        Duration progress = this.getProgress();
        int seconds = (int)(progress.toMillis() / 1000L);
        int minutes = seconds % 3600 / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    @Override
    public Color getTextColor() {
        Duration progress = this.getProgress();
        if ((double)progress.getSeconds() < (double)this.duration.getSeconds() * 0.1) {
            return Color.RED.brighter();
        }
        return Color.WHITE;
    }

    private Duration getProgress() {
        Duration passed = Duration.between(this.startTime, Instant.now());
        long passedMillis = passed.toMillis();
        long durationMillis = this.duration.toMillis();
        long progress = passedMillis % durationMillis;
        return Duration.ofMillis(this.reverse ? durationMillis - progress : progress);
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public boolean isReverse() {
        return this.reverse;
    }

    public String toString() {
        return "LoopTimer(startTime=" + String.valueOf(this.getStartTime()) + ", duration=" + String.valueOf(this.getDuration()) + ", reverse=" + this.isReverse() + ")";
    }
}

