/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.menuentryswapper;

public enum FairyRingMode {
    ZANARIS("Zanaris"),
    LAST_DESTINATION("Last-Destination"),
    CONFIGURE("Configure"),
    OFF("Off");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private FairyRingMode(String name) {
        this.name = name;
    }
}

