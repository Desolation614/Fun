/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.blastfurnace;

import java.time.temporal.ChronoUnit;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.blastfurnace.BlastFurnacePlugin;
import net.runelite.client.ui.overlay.infobox.Timer;

class ForemanTimer
extends Timer {
    private static final String TOOLTIP_TEXT = "Foreman Fee";

    ForemanTimer(BlastFurnacePlugin plugin, ItemManager itemManager) {
        super(10L, ChronoUnit.MINUTES, itemManager.getImage(764), plugin);
        this.setTooltip(TOOLTIP_TEXT);
    }
}

