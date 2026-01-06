/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.grounditems.config;

public enum ItemHighlightMode {
    NONE("None"),
    OVERLAY("Overlay"),
    MENU("Right-click menu"),
    BOTH("Both");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private ItemHighlightMode(String name) {
        this.name = name;
    }
}

