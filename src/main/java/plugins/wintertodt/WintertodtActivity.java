/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.wintertodt;

enum WintertodtActivity {
    IDLE("IDLE"),
    WOODCUTTING("Woodcutting"),
    FLETCHING("Fletching"),
    FEEDING_BRAZIER("Feeding"),
    FIXING_BRAZIER("Fixing"),
    LIGHTING_BRAZIER("Lighting");

    private final String actionString;

    private WintertodtActivity(String actionString) {
        this.actionString = actionString;
    }

    public String getActionString() {
        return this.actionString;
    }
}

