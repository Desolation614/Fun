/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

public enum TooltipPositionType {
    ABOVE_CURSOR("Above cursor"),
    UNDER_CURSOR("Under cursor");

    private final String type;

    public String toString() {
        return this.type;
    }

    public String getType() {
        return this.type;
    }

    private TooltipPositionType(String type) {
        this.type = type;
    }
}

