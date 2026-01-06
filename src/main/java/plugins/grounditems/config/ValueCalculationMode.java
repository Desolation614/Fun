/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.grounditems.config;

public enum ValueCalculationMode {
    HA("High Alchemy"),
    GE("Grand Exchange"),
    HIGHEST("Highest");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private ValueCalculationMode(String name) {
        this.name = name;
    }
}

