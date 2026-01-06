/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.grounditems.config;

public enum OwnershipFilterMode {
    ALL("All"),
    TAKEABLE("Takeable"),
    DROPS("Drops");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private OwnershipFilterMode(String name) {
        this.name = name;
    }
}

