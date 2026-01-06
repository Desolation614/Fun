/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.runelite.client.plugins.tithefarm;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public enum TitheFarmPlantType {
    EMPTY("Empty", 27383, 27383),
    GOLOVANOVA("Golovanova", 27393, 27384, 27385, 27386, 27387, 27388, 27389, 27390, 27391, 27392, 27393, 27394),
    BOLOGANO("Bologano", 27404, 27395, 27396, 27397, 27398, 27399, 27400, 27401, 27402, 27403, 27404, 27405),
    LOGAVANO("Logavano", 27415, 27406, 27407, 27408, 27409, 27410, 27411, 27412, 27413, 27414, 27415, 27416);

    private final String name;
    private final int baseId;
    private final int[] objectIds;
    private static final Map<Integer, TitheFarmPlantType> plantTypes;

    private TitheFarmPlantType(String name, int baseId, int ... objectIds) {
        this.name = name;
        this.baseId = baseId;
        this.objectIds = objectIds;
    }

    public static TitheFarmPlantType getPlantType(int objectId) {
        return plantTypes.get(objectId);
    }

    public String getName() {
        return this.name;
    }

    public int getBaseId() {
        return this.baseId;
    }

    public int[] getObjectIds() {
        return this.objectIds;
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (TitheFarmPlantType type : TitheFarmPlantType.values()) {
            for (int spotId : type.getObjectIds()) {
                builder.put((Object)spotId, (Object)type);
            }
        }
        plantTypes = builder.build();
    }
}

