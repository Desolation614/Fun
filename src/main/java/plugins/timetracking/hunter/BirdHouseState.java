/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.hunter;

import java.awt.Color;
import net.runelite.client.plugins.timetracking.hunter.BirdHouse;
import net.runelite.client.ui.ColorScheme;

enum BirdHouseState {
    SEEDED(ColorScheme.PROGRESS_COMPLETE_COLOR),
    BUILT(ColorScheme.PROGRESS_INPROGRESS_COLOR),
    EMPTY(ColorScheme.MEDIUM_GRAY_COLOR),
    UNKNOWN(ColorScheme.MEDIUM_GRAY_COLOR);

    private final Color color;

    static BirdHouseState fromVarpValue(int varp) {
        if (varp < 0 || varp > BirdHouse.values().length * 3) {
            return UNKNOWN;
        }
        if (varp == 0) {
            return EMPTY;
        }
        if (varp % 3 == 0) {
            return SEEDED;
        }
        return BUILT;
    }

    private BirdHouseState(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }
}

