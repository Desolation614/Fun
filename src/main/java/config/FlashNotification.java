/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.config;

public enum FlashNotification {
    DISABLED("Off"),
    FLASH_TWO_SECONDS("Flash for 2 seconds"),
    SOLID_TWO_SECONDS("Solid for 2 seconds"),
    FLASH_UNTIL_CANCELLED("Flash until cancelled"),
    SOLID_UNTIL_CANCELLED("Solid until cancelled");

    private final String type;

    public String toString() {
        return this.type;
    }

    public String getType() {
        return this.type;
    }

    private FlashNotification(String type) {
        this.type = type;
    }
}

