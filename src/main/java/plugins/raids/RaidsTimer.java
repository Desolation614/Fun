/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.raids;

import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.raids.RaidsConfig;
import net.runelite.client.ui.overlay.infobox.InfoBox;

class RaidsTimer
extends InfoBox {
    private final Instant startTime;
    private final RaidsConfig config;
    private Instant floorTime;
    private LocalTime time;
    private LocalTime firstFloorTime;
    private LocalTime secondFloorTime;
    private LocalTime thirdFloorTime;
    private LocalTime olmTime;
    private boolean stopped;

    public RaidsTimer(Plugin plugin, Instant startTime, RaidsConfig raidsConfig) {
        super(null, plugin);
        this.startTime = startTime;
        this.config = raidsConfig;
        this.floorTime = startTime;
        this.stopped = false;
    }

    public void timeFloor() {
        Duration elapsed = Duration.between(this.floorTime, Instant.now());
        if (this.firstFloorTime == null) {
            this.firstFloorTime = LocalTime.ofSecondOfDay(elapsed.getSeconds());
        } else if (this.secondFloorTime == null) {
            this.secondFloorTime = LocalTime.ofSecondOfDay(elapsed.getSeconds());
        } else if (this.thirdFloorTime == null) {
            this.thirdFloorTime = LocalTime.ofSecondOfDay(elapsed.getSeconds());
        }
        this.floorTime = Instant.now();
    }

    public void timeOlm() {
        Duration elapsed = Duration.between(this.floorTime, Instant.now());
        this.olmTime = LocalTime.ofSecondOfDay(elapsed.getSeconds());
    }

    @Override
    public String getText() {
        if (this.startTime == null) {
            return "";
        }
        if (!this.stopped) {
            Duration elapsed = Duration.between(this.startTime, Instant.now());
            this.time = LocalTime.ofSecondOfDay(elapsed.getSeconds());
        }
        if (this.time.getHour() > 0) {
            return this.time.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return this.time.format(DateTimeFormatter.ofPattern("mm:ss"));
    }

    @Override
    public Color getTextColor() {
        if (this.stopped) {
            return Color.GREEN;
        }
        return Color.WHITE;
    }

    @Override
    public String getTooltip() {
        StringBuilder builder = new StringBuilder();
        builder.append("Elapsed raid time: ");
        builder.append(this.time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        if (this.firstFloorTime != null) {
            builder.append("</br>First floor: ");
            builder.append(this.firstFloorTime.format(DateTimeFormatter.ofPattern("mm:ss")));
        }
        if (this.secondFloorTime != null) {
            builder.append("</br>Second floor: ");
            builder.append(this.secondFloorTime.format(DateTimeFormatter.ofPattern("mm:ss")));
        }
        if (this.thirdFloorTime != null) {
            builder.append("</br>Third floor: ");
            builder.append(this.thirdFloorTime.format(DateTimeFormatter.ofPattern("mm:ss")));
        }
        if (this.olmTime != null) {
            builder.append("</br>Olm: ");
            builder.append(this.olmTime.format(DateTimeFormatter.ofPattern("mm:ss")));
        }
        return builder.toString();
    }

    @Override
    public boolean render() {
        return this.config.raidsTimer();
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}

