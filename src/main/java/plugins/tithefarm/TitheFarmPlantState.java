/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.tithefarm;

import net.runelite.client.plugins.tithefarm.TitheFarmPlantType;

enum TitheFarmPlantState {
    UNWATERED,
    WATERED,
    DEAD,
    GROWN;


    public static TitheFarmPlantState getState(int objectId) {
        TitheFarmPlantType plantType = TitheFarmPlantType.getPlantType(objectId);
        if (plantType == null) {
            return null;
        }
        int baseId = plantType.getBaseId();
        if (objectId == baseId) {
            return GROWN;
        }
        switch ((baseId - objectId) % 3) {
            case 0: {
                return UNWATERED;
            }
            case 2: {
                return WATERED;
            }
        }
        return DEAD;
    }
}

