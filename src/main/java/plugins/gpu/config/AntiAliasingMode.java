/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.gpu.config;

public enum AntiAliasingMode {
    DISABLED("Disabled", 0),
    MSAA_2("MSAA x2", 2),
    MSAA_4("MSAA x4", 4),
    MSAA_8("MSAA x8", 8),
    MSAA_16("MSAA x16", 16);

    private final String name;
    private final int samples;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public int getSamples() {
        return this.samples;
    }

    private AntiAliasingMode(String name, int samples) {
        this.name = name;
        this.samples = samples;
    }
}

