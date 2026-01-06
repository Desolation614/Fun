/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.barbarianassault;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.runelite.client.util.RSTimeUnit;

class GameTimer {
    private final Instant startTime;
    private Instant prevWave;

    GameTimer() {
        this.prevWave = this.startTime = Instant.now();
    }

    String getTime(boolean waveTime) {
        Instant now = Instant.now();
        Duration elapsed = waveTime ? Duration.between(this.prevWave, now) : Duration.between(this.startTime, now).minus(Duration.of(1L, RSTimeUnit.GAME_TICKS));
        return GameTimer.formatTime(LocalTime.ofSecondOfDay(elapsed.getSeconds()));
    }

    void setWaveStartTime() {
        this.prevWave = Instant.now();
    }

    private static String formatTime(LocalTime time) {
        if (time.getHour() > 0) {
            return time.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        if (time.getMinute() > 9) {
            return time.format(DateTimeFormatter.ofPattern("mm:ss"));
        }
        return time.format(DateTimeFormatter.ofPattern("m:ss"));
    }
}

