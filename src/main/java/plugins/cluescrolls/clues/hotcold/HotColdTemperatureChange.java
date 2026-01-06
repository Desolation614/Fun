/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.cluescrolls.clues.hotcold;

public enum HotColdTemperatureChange {
    WARMER("and warmer than"),
    SAME("and the same temperature as"),
    COLDER("but colder than");

    private final String text;

    public static HotColdTemperatureChange of(String message) {
        if (!message.endsWith(" last time.")) {
            return null;
        }
        for (HotColdTemperatureChange change : HotColdTemperatureChange.values()) {
            if (!message.contains(change.text)) continue;
            return change;
        }
        return null;
    }

    private HotColdTemperatureChange(String text) {
        this.text = text;
    }
}

