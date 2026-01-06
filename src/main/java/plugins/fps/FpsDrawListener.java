/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.events.FocusChanged
 */
package net.runelite.client.plugins.fps;

import javax.inject.Inject;
import net.runelite.api.events.FocusChanged;
import net.runelite.client.plugins.fps.FpsConfig;

public class FpsDrawListener
implements Runnable {
    private static final int SAMPLE_SIZE = 4;
    private final FpsConfig config;
    private long targetDelay = 0L;
    private boolean isFocused = true;
    private long lastMillis = 0L;
    private final long[] lastDelays = new long[4];
    private int lastDelayIndex = 0;
    private long sleepDelay = 0L;

    @Inject
    private FpsDrawListener(FpsConfig config) {
        this.config = config;
        this.reloadConfig();
    }

    void reloadConfig() {
        this.lastMillis = System.currentTimeMillis();
        int fps = this.config.limitFpsUnfocused() && !this.isFocused ? this.config.maxFpsUnfocused() : this.config.maxFps();
        this.sleepDelay = this.targetDelay = (long)(1000 / Math.max(1, fps));
        for (int i = 0; i < 4; ++i) {
            this.lastDelays[i] = this.targetDelay;
        }
    }

    void onFocusChanged(FocusChanged event) {
        this.isFocused = event.isFocused();
        this.reloadConfig();
    }

    private boolean isEnforced() {
        return this.config.limitFps() || this.config.limitFpsUnfocused() && !this.isFocused;
    }

    @Override
    public void run() {
        long now;
        if (!this.isEnforced()) {
            return;
        }
        long before = this.lastMillis;
        this.lastMillis = now = System.currentTimeMillis();
        this.lastDelayIndex = (this.lastDelayIndex + 1) % 4;
        this.lastDelays[this.lastDelayIndex] = now - before;
        long averageDelay = 0L;
        for (int i = 0; i < 4; ++i) {
            averageDelay += this.lastDelays[i];
        }
        if ((averageDelay /= (long)this.lastDelays.length) > this.targetDelay) {
            --this.sleepDelay;
        } else if (averageDelay < this.targetDelay) {
            ++this.sleepDelay;
        }
        if (this.sleepDelay > 0L) {
            try {
                Thread.sleep(this.sleepDelay);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }
}

