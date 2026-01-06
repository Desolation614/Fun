/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.farming;

import java.awt.Color;
import net.runelite.client.ui.ColorScheme;

public enum CropState {
    HARVESTABLE(ColorScheme.PROGRESS_COMPLETE_COLOR),
    GROWING(ColorScheme.PROGRESS_COMPLETE_COLOR),
    DISEASED(ColorScheme.PROGRESS_INPROGRESS_COLOR),
    DEAD(ColorScheme.PROGRESS_ERROR_COLOR),
    EMPTY(ColorScheme.MEDIUM_GRAY_COLOR),
    FILLING(ColorScheme.PROGRESS_INPROGRESS_COLOR);

    private final Color color;

    private CropState(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }
}

