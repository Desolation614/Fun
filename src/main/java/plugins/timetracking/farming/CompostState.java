/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.farming;

public enum CompostState {
    COMPOST(6032),
    SUPERCOMPOST(6034),
    ULTRACOMPOST(21483);

    private final int itemId;

    private CompostState(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return this.itemId;
    }
}

