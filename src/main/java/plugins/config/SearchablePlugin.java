/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.config;

import java.util.List;

interface SearchablePlugin {
    public String getSearchableName();

    public List<String> getKeywords();

    default public boolean isPinned() {
        return false;
    }

    default public int installs() {
        return 0;
    }
}

