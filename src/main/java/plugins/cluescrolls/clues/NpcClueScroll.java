/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.cluescrolls.clues;

import java.util.Collection;
import java.util.Collections;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;

public interface NpcClueScroll {
    public String[] getNpcs(ClueScrollPlugin var1);

    default public Collection<Integer> getNpcRegions() {
        return Collections.emptyList();
    }
}

