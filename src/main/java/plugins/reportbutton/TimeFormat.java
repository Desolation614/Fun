/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.reportbutton;

public enum TimeFormat {
    TIME_12H("12-hour"),
    TIME_24H("24-hour");

    private final String name;

    public String toString() {
        return this.name;
    }

    private TimeFormat(String name) {
        this.name = name;
    }
}

