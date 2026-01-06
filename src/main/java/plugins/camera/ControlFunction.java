/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.camera;

public enum ControlFunction {
    NONE("None"),
    CONTROL_TO_ZOOM("Hold to zoom"),
    CONTROL_TO_RESET("Reset zoom");

    private final String name;

    public String toString() {
        return this.getName();
    }

    public String getName() {
        return this.name;
    }

    private ControlFunction(String name) {
        this.name = name;
    }
}

