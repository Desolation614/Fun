/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.menuentryswapper;

public enum HouseAdvertisementMode {
    VIEW("View"),
    ADD_HOUSE("Add-House"),
    VISIT_LAST("Visit-Last");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private HouseAdvertisementMode(String name) {
        this.name = name;
    }
}

