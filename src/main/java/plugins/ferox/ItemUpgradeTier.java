/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.ferox;

enum ItemUpgradeTier {
    I("8B5738"),
    II("B0B0B0"),
    III("347D2C"),
    IV("1974D2"),
    V("FF4500");

    private final String hexColor;

    private ItemUpgradeTier(String hexColor) {
        this.hexColor = hexColor;
    }

    String getHexColor() {
        return this.hexColor;
    }
}

