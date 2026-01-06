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

public class Timer
extends InfoBox {
    private final Instant startTime;
    private Instant endTime;
    private Duration duration;

    public Timer(long period, ChronoUnit unit, BufferedImage image, Plugin plugin) {
        super(image, plugin);
        Preconditions.checkArgument((period > 0L ? 1 : 0) != 0, (Object)"negative period!");
        this.startTime = Instant.now();
        this.duration = Duration.of(period, unit);
        this.endTime = this.startTime.plus(this.duration);
    }

    @Override
    public String getText() {
        Duration timeLeft = Duration.between(Instant.now(), this.endTime);
        int seconds = (int)(timeLeft.toMillis() / 1000L);
        int minutes = seconds % 3600 / 60;
        int secs = seconds % 60;
        if (seconds / 60 >= 60) {
            int hours = seconds / 3600;
            return String.format("%dh:%02d", hours, minutes);
        }
        return String.format("%d:%02d", minutes, secs);
    }

    @Override
    public Color getTextColor() {
        Duration timeLeft = Duration.between(Instant.now(), this.endTime);
        if ((double)timeLeft.getSeconds() < (double)this.duration.getSeconds() * 0.1) {
            return Color.RED.brighter();
        }
        return Color.WHITE;
    }

    @Override
    public boolean render() {
        Duration timeLeft = Duration.between(Instant.now(), this.endTime);
        return !timeLeft.isNegative();
    }

    @Override
    public boolean cull() {
        Duration timeLeft = Duration.between(Instant.now(), this.endTime);
        return timeLeft.isZero() || timeLeft.isNegative();
    }

    public void setDuration(Duration duration) {
        Preconditions.checkArgument((!duration.isNegative() ? 1 : 0) != 0, (Object)"negative duration");
        this.duration = duration;
        this.endTime = this.startTime.plus(duration);
    }

    public void updateDuration(Duration duration) {
        Preconditions.checkArgument((!duration.isNegative() ? 1 : 0) != 0, (Object)"negative duration");
        this.endTime = Instant.now().plus(duration);
        this.duration = Duration.between(this.startTime, this.endTime);
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public String toString() {
        return "Timer(startTime=" + String.valueOf(this.getStartTime()) + ", endTime=" + String.valueOf(this.getEndTime()) + ", duration=" + String.valueOf(this.getDuration()) + ")";
    }
}

