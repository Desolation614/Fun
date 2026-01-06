/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.raids;

import net.runelite.client.plugins.raids.RaidRoom;

public enum RoomType {
    START("Start", '#'),
    END("End", '\u00a4'),
    SCAVENGERS("Scavengers", 'S'),
    FARMING("Farming", 'F'),
    EMPTY("Empty", ' '),
    COMBAT("Combat", 'C'),
    PUZZLE("Puzzle", 'P');

    private final String name;
    private final char code;

    RaidRoom getUnsolvedRoom() {
        switch (this) {
            case START: {
                return RaidRoom.START;
            }
            case END: {
                return RaidRoom.END;
            }
            case SCAVENGERS: {
                return RaidRoom.SCAVENGERS;
            }
            case FARMING: {
                return RaidRoom.FARMING;
            }
            case COMBAT: {
                return RaidRoom.UNKNOWN_COMBAT;
            }
            case PUZZLE: {
                return RaidRoom.UNKNOWN_PUZZLE;
            }
        }
        return RaidRoom.EMPTY;
    }

    static RoomType fromCode(char code) {
        for (RoomType type : RoomType.values()) {
            if (type.getCode() != code) continue;
            return type;
        }
        return EMPTY;
    }

    private RoomType(String name, char code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public char getCode() {
        return this.code;
    }
}

