/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

public enum WarningOnExit {
    ALWAYS("Always"),
    LOGGED_IN("Logged in"),
    NEVER("Never");

    private final String type;

    public String toString() {
        return this.type;
    }

    public String getType() {
        return this.type;
    }

    private WarningOnExit(String type) {
        this.type = type;
    }
}

