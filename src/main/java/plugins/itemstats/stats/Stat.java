/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.itemstats.stats;

import net.runelite.api.Client;

public abstract class Stat {
    private final String name;

    Stat(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract int getValue(Client var1);

    public abstract int getMaximum(Client var1);
}

