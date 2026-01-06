/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.raids;

import java.util.Arrays;
import java.util.List;
import net.runelite.client.plugins.raids.RaidRoom;
import net.runelite.client.plugins.raids.RoomType;

class RotationSolver {
    private static final List[] ROTATIONS = new List[]{Arrays.asList(RaidRoom.TEKTON, RaidRoom.VASA, RaidRoom.GUARDIANS, RaidRoom.MYSTICS, RaidRoom.SHAMANS, RaidRoom.MUTTADILES, RaidRoom.VANGUARDS, RaidRoom.VESPULA), Arrays.asList(RaidRoom.TEKTON, RaidRoom.MUTTADILES, RaidRoom.GUARDIANS, RaidRoom.VESPULA, RaidRoom.SHAMANS, RaidRoom.VASA, RaidRoom.VANGUARDS, RaidRoom.MYSTICS), Arrays.asList(RaidRoom.VESPULA, RaidRoom.VANGUARDS, RaidRoom.MUTTADILES, RaidRoom.SHAMANS, RaidRoom.MYSTICS, RaidRoom.GUARDIANS, RaidRoom.VASA, RaidRoom.TEKTON), Arrays.asList(RaidRoom.MYSTICS, RaidRoom.VANGUARDS, RaidRoom.VASA, RaidRoom.SHAMANS, RaidRoom.VESPULA, RaidRoom.GUARDIANS, RaidRoom.MUTTADILES, RaidRoom.TEKTON)};

    RotationSolver() {
    }

    static boolean solve(RaidRoom[] rooms) {
        if (rooms == null) {
            return false;
        }
        List match = null;
        Integer start = null;
        Integer index = null;
        int known = 0;
        for (int i = 0; i < rooms.length; ++i) {
            if (rooms[i] == null || rooms[i].getType() != RoomType.COMBAT || rooms[i] == RaidRoom.UNKNOWN_COMBAT) continue;
            if (start == null) {
                start = i;
            }
            ++known;
        }
        if (known < 2) {
            return false;
        }
        if (known == rooms.length) {
            return true;
        }
        block1: for (List rotation : ROTATIONS) {
            for (int i = 0; i < rotation.size(); ++i) {
                if (rooms[start] != rotation.get(i)) continue;
                for (int j = start + 1; j < rooms.length; ++j) {
                    if (rooms[j].getType() == RoomType.COMBAT && rooms[j] != RaidRoom.UNKNOWN_COMBAT && rooms[j] != rotation.get(Math.floorMod(i + j - start, rotation.size()))) continue block1;
                }
                if (match != null && match != rotation) {
                    return false;
                }
                index = i - start;
                match = rotation;
            }
        }
        if (match == null) {
            return false;
        }
        for (int i = 0; i < rooms.length; ++i) {
            if (rooms[i] == null || rooms[i].getType() == RoomType.COMBAT && rooms[i] != RaidRoom.UNKNOWN_COMBAT) continue;
            rooms[i] = (RaidRoom)((Object)match.get(Math.floorMod(index + i, match.size())));
        }
        return true;
    }
}

