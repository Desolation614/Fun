/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.wintertodt.config;

public enum WintertodtNotifyDamage {
    OFF("Off"),
    INTERRUPT("On Interrupt"),
    ALWAYS("Always");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private WintertodtNotifyDamage(String name) {
        this.name = name;
    }
}

