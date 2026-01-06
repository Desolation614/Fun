/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.runelite.client.plugins.woodcutting.config;

import com.google.common.collect.ImmutableMap;

public enum ClueNestTier {
    BEGINNER,
    EASY,
    MEDIUM,
    HARD,
    ELITE,
    DISABLED;

    private static final ImmutableMap<Integer, ClueNestTier> CLUE_NEST_ID_TO_TIER;

    public static ClueNestTier getTierFromItem(int itemId) {
        return (ClueNestTier)((Object)CLUE_NEST_ID_TO_TIER.get((Object)itemId));
    }

    static {
        CLUE_NEST_ID_TO_TIER = new ImmutableMap.Builder().put((Object)19718, (Object)ELITE).put((Object)19716, (Object)HARD).put((Object)19714, (Object)MEDIUM).put((Object)19712, (Object)EASY).put((Object)23127, (Object)BEGINNER).build();
    }
}

