/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.MenuEntry
 */
package net.runelite.client.plugins.grounditems;

import net.runelite.api.MenuEntry;

class MenuEntryWithCount {
    private final MenuEntry entry;
    private int count = 1;

    void increment() {
        ++this.count;
    }

    public MenuEntryWithCount(MenuEntry entry) {
        this.entry = entry;
    }

    public MenuEntry getEntry() {
        return this.entry;
    }

    public int getCount() {
        return this.count;
    }
}

