/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.timetracking.hunter;

import javax.annotation.Nullable;

enum BirdHouse {
    NORMAL("Bird House", 21512),
    OAK("Oak Bird House", 21515),
    WILLOW("Willow Bird House", 21518),
    TEAK("Teak Bird House", 21521),
    MAPLE("Maple Bird House", 22192),
    MAHOGANY("Mahogany Bird House", 22195),
    YEW("Yew Bird House", 22198),
    MAGIC("Magic Bird House", 22201),
    REDWOOD("Redwood Bird House", 22204);

    private final String name;
    private final int itemID;

    @Nullable
    static BirdHouse fromVarpValue(int varp) {
        int index = (varp - 1) / 3;
        if (varp <= 0 || index >= BirdHouse.values().length) {
            return null;
        }
        return BirdHouse.values()[index];
    }

    private BirdHouse(String name, int itemID) {
        this.name = name;
        this.itemID = itemID;
    }

    public String getName() {
        return this.name;
    }

    public int getItemID() {
        return this.itemID;
    }
}

