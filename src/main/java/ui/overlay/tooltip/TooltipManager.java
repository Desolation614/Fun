/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Singleton
 */
package net.runelite.client.ui.overlay.tooltip;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import net.runelite.client.ui.overlay.tooltip.Tooltip;

@Singleton
public class TooltipManager {
    private final List<Tooltip> tooltips = new ArrayList<Tooltip>();

    public void add(Tooltip tooltip) {
        this.tooltips.add(tooltip);
    }

    public void addFront(Tooltip tooltip) {
        this.tooltips.add(0, tooltip);
    }

    public void clear() {
        this.tooltips.clear();
    }

    public List<Tooltip> getTooltips() {
        return this.tooltips;
    }
}

