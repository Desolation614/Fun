/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.grounditems.config;

public enum PriceDisplayMode {
    HA("High Alchemy"),
    GE("Grand Exchange"),
    BOTH("Both"),
    OFF("Off");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private PriceDisplayMode(String name) {
        this.name = name;
    }
}

