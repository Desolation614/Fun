/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

public enum ExpandResizeType {
    KEEP_WINDOW_SIZE("Keep window size"),
    KEEP_GAME_SIZE("Keep game size");

    private final String type;

    public String toString() {
        return this.type;
    }

    public String getType() {
        return this.type;
    }

    private ExpandResizeType(String type) {
        this.type = type;
    }
}

