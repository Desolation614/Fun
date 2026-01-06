/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.xptracker;

import java.util.EnumSet;
import java.util.Set;

class XpPauseStateSingle {
    private final Set<XpPauseReason> pauseReasons = EnumSet.noneOf(XpPauseReason.class);
    private long lastChangeMillis;
    private long xp;

    boolean isPaused() {
        return !this.pauseReasons.isEmpty();
    }

    boolean login() {
        return this.pauseReasons.remove((Object)XpPauseReason.PAUSED_LOGOUT);
    }

    boolean logout() {
        return this.pauseReasons.add(XpPauseReason.PAUSED_LOGOUT);
    }

    boolean timeout() {
        return this.pauseReasons.add(XpPauseReason.PAUSED_TIMEOUT);
    }

    boolean manualPause() {
        return this.pauseReasons.add(XpPauseReason.PAUSE_MANUAL);
    }

    boolean xpChanged(long xp) {
        this.xp = xp;
        this.lastChangeMillis = System.currentTimeMillis();
        return this.clearAll();
    }

    boolean unpause() {
        this.lastChangeMillis = System.currentTimeMillis();
        return this.clearAll();
    }

    private boolean clearAll() {
        if (this.pauseReasons.isEmpty()) {
            return false;
        }
        this.pauseReasons.clear();
        return true;
    }

    public long getLastChangeMillis() {
        return this.lastChangeMillis;
    }

    public long getXp() {
        return this.xp;
    }

    private static enum XpPauseReason {
        PAUSE_MANUAL,
        PAUSED_LOGOUT,
        PAUSED_TIMEOUT;

    }
}

