/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.gpu.config;

public enum UIScalingMode {
    NEAREST("Nearest Neighbor", 0),
    LINEAR("Bilinear", 0),
    MITCHELL("Bicubic (Mitchell)", 1),
    CATMULL_ROM("Bicubic (Catmull-Rom)", 2),
    XBR("xBR", 3);

    private final String name;
    private final int mode;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public int getMode() {
        return this.mode;
    }

    private UIScalingMode(String name, int mode) {
        this.name = name;
        this.mode = mode;
    }
}

