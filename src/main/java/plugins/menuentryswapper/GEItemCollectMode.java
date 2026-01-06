/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.menuentryswapper;

public enum GEItemCollectMode {
    DEFAULT("Default"),
    ITEMS("Collect-items"),
    NOTES("Collect-notes"),
    BANK("Bank");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private GEItemCollectMode(String name) {
        this.name = name;
    }
}

