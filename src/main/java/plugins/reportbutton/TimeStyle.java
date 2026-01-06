/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.reportbutton;

public enum TimeStyle {
    DATE("Date"),
    LOGIN_TIME("Login Timer"),
    UTC("UTC Time"),
    JAGEX("Jagex HQ Time"),
    LOCAL_TIME("Local Time"),
    GAME_TICKS("Game Ticks"),
    IDLE_TIME("Idle Time");

    private final String name;

    private TimeStyle(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}

