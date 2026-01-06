/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.runelite.client.plugins.blastmine;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public enum BlastMineRockType {
    NORMAL(28579, 28580),
    CHISELED(28581, 28582),
    LOADED(28583, 28584),
    LIT(28585, 28586),
    EXPLODED(28587, 28588);

    private static final Map<Integer, BlastMineRockType> rockTypes;
    private final int[] objectIds;

    private BlastMineRockType(int ... objectIds) {
        this.objectIds = objectIds;
    }

    public static BlastMineRockType getRockType(int objectId) {
        return rockTypes.get(objectId);
    }

    public int[] getObjectIds() {
        return this.objectIds;
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (BlastMineRockType type : BlastMineRockType.values()) {
            for (int spotId : type.getObjectIds()) {
                builder.put((Object)spotId, (Object)type);
            }
        }
        rockTypes = builder.build();
    }
}

