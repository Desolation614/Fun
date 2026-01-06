/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timersandbuffs;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.timersandbuffs.GameTimer;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;
import net.runelite.client.ui.overlay.infobox.Timer;

class TimerTimer
extends Timer {
    private final GameTimer timer;
    int ticks;

    TimerTimer(GameTimer timer, Duration duration, Plugin plugin) {
        super(duration.toMillis(), ChronoUnit.MILLIS, null, plugin);
        this.timer = timer;
        this.setPriority(InfoBoxPriority.MED);
    }

    public GameTimer getTimer() {
        return this.timer;
    }

    @Override
    public String getName() {
        return this.timer.name();
    }
}

