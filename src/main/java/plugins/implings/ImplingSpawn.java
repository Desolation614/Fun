/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.implings;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.implings.ImplingType;

enum ImplingSpawn {
    SPAWN_BABY1(new WorldPoint(2563, 4291, 0), ImplingType.BABY),
    SPAWN_BABY2(new WorldPoint(2563, 4348, 0), ImplingType.BABY),
    SPAWN_BABY3(new WorldPoint(2569, 4323, 0), ImplingType.BABY),
    SPAWN_BABY4(new WorldPoint(2571, 4305, 0), ImplingType.BABY),
    SPAWN_BABY5(new WorldPoint(2581, 4300, 0), ImplingType.BABY),
    SPAWN_BABY6(new WorldPoint(2596, 4296, 0), ImplingType.BABY),
    SPAWN_BABY7(new WorldPoint(2609, 4339, 0), ImplingType.BABY),
    SPAWN_BABY8(new WorldPoint(2610, 4304, 0), ImplingType.BABY),
    SPAWN_BABY9(new WorldPoint(2615, 4322, 0), ImplingType.BABY),
    SPAWN_BABY10(new WorldPoint(2620, 4291, 0), ImplingType.BABY),
    SPAWN_BABY11(new WorldPoint(2620, 4348, 0), ImplingType.BABY),
    SPAWN_YOUNG1(new WorldPoint(2564, 4321, 0), ImplingType.YOUNG),
    SPAWN_YOUNG2(new WorldPoint(2573, 4330, 0), ImplingType.YOUNG),
    SPAWN_YOUNG3(new WorldPoint(2574, 4321, 0), ImplingType.YOUNG),
    SPAWN_YOUNG4(new WorldPoint(2590, 4348, 0), ImplingType.YOUNG),
    SPAWN_YOUNG5(new WorldPoint(2592, 4291, 0), ImplingType.YOUNG),
    SPAWN_YOUNG6(new WorldPoint(2595, 4343, 0), ImplingType.YOUNG),
    SPAWN_YOUNG7(new WorldPoint(2612, 4327, 0), ImplingType.YOUNG),
    SPAWN_YOUNG8(new WorldPoint(2612, 4309, 0), ImplingType.YOUNG),
    SPAWN_YOUNG9(new WorldPoint(2619, 4322, 0), ImplingType.YOUNG),
    SPAWN_YOUNG10(new WorldPoint(2587, 4300, 0), ImplingType.YOUNG),
    SPAWN_GOURMET1(new WorldPoint(2568, 4296, 0), ImplingType.GOURMET),
    SPAWN_GOURMET2(new WorldPoint(2569, 4327, 0), ImplingType.GOURMET),
    SPAWN_GOURMET3(new WorldPoint(2574, 4311, 0), ImplingType.GOURMET),
    SPAWN_GOURMET4(new WorldPoint(2574, 4311, 0), ImplingType.GOURMET),
    SPAWN_GOURMET5(new WorldPoint(2585, 4296, 0), ImplingType.GOURMET),
    SPAWN_GOURMET6(new WorldPoint(2597, 4293, 0), ImplingType.GOURMET),
    SPAWN_GOURMET7(new WorldPoint(2609, 4317, 0), ImplingType.GOURMET),
    SPAWN_GOURMET8(new WorldPoint(2615, 4298, 0), ImplingType.GOURMET),
    SPAWN_GOURMET9(new WorldPoint(2618, 4321, 0), ImplingType.GOURMET),
    SPAWN_EARTH1(new WorldPoint(2570, 4330, 0), ImplingType.EARTH),
    SPAWN_EARTH2(new WorldPoint(2598, 4340, 0), ImplingType.EARTH),
    SPAWN_EARTH3(new WorldPoint(2587, 4342, 0), ImplingType.EARTH),
    SPAWN_EARTH4(new WorldPoint(2612, 4310, 0), ImplingType.EARTH),
    SPAWN_EARTH5(new WorldPoint(2611, 4334, 0), ImplingType.EARTH),
    SPAWN_ESSENCE1(new WorldPoint(2574, 4317, 0), ImplingType.ESSENCE),
    SPAWN_ESSENCE2(new WorldPoint(2576, 4337, 0), ImplingType.ESSENCE),
    SPAWN_ESSENCE3(new WorldPoint(2585, 4298, 0), ImplingType.ESSENCE),
    SPAWN_ESSENCE4(new WorldPoint(2601, 4343, 0), ImplingType.ESSENCE),
    SPAWN_ESSENCE5(new WorldPoint(2612, 4318, 0), ImplingType.ESSENCE),
    SPAWN_ECLECTIC1(new WorldPoint(2567, 4319, 0), ImplingType.ECLECTIC),
    SPAWN_ECLECTIC2(new WorldPoint(2591, 4340, 0), ImplingType.ECLECTIC),
    SPAWN_ECLECTIC3(new WorldPoint(2591, 4295, 0), ImplingType.ECLECTIC),
    SPAWN_ECLECTIC4(new WorldPoint(2615, 4326, 0), ImplingType.ECLECTIC);

    private final WorldPoint spawnLocation;
    private final ImplingType type;

    private ImplingSpawn(WorldPoint spawnLocation, ImplingType type) {
        this.spawnLocation = spawnLocation;
        this.type = type;
    }

    public WorldPoint getSpawnLocation() {
        return this.spawnLocation;
    }

    public ImplingType getType() {
        return this.type;
    }
}

