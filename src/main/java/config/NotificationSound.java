/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

public enum NotificationSound {
    NATIVE("Native"),
    CUSTOM("Custom"),
    OFF("Off");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private NotificationSound(String name) {
        this.name = name;
    }
}

