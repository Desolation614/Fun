/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.runelite.client.plugins.prayer;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

enum PrayerRestoreType {
    RESTOREPOT(3024, 3026, 3028, 3030, 24598, 24601, 24603, 24605),
    PRAYERPOT(2434, 139, 141, 143),
    SANFEWPOT(10925, 10927, 10929, 10931),
    HOLYWRENCH(9759, 9760, 13280, 13342, 6714, 13202);

    private static final Map<Integer, PrayerRestoreType> prayerRestores;
    private final int[] items;

    private PrayerRestoreType(int ... items) {
        this.items = items;
    }

    static PrayerRestoreType getType(int itemId) {
        return prayerRestores.get(itemId);
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (PrayerRestoreType prayerRestoreType : PrayerRestoreType.values()) {
            for (int itemId : prayerRestoreType.items) {
                builder.put((Object)itemId, (Object)prayerRestoreType);
            }
        }
        prayerRestores = builder.build();
    }
}

