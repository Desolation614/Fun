/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking;

public enum TimeFormatMode {
    RELATIVE("Relative"),
    ABSOLUTE_12H("12 Hour"),
    ABSOLUTE_24H("24 Hour");

    private final String name;

    public String toString() {
        return this.name;
    }

    private TimeFormatMode(String name) {
        this.name = name;
    }
}

