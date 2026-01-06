/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.grounditems.config;

public enum MenuHighlightMode {
    OPTION("Menu option"),
    NAME("Menu item name"),
    BOTH("Both");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private MenuHighlightMode(String name) {
        this.name = name;
    }
}

