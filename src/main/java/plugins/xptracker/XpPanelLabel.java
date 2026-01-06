/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.xptracker;

import java.util.function.Function;
import net.runelite.client.plugins.xptracker.XpSnapshotSingle;
import net.runelite.client.util.QuantityFormatter;

public enum XpPanelLabel {
    TIME_TO_LEVEL("TTL", XpSnapshotSingle::getTimeTillGoalShort),
    XP_GAINED("XP Gained", snap -> XpPanelLabel.format(snap.getXpGainedInSession())),
    XP_HOUR("XP/hr", snap -> XpPanelLabel.format(snap.getXpPerHour())),
    XP_LEFT("XP Left", snap -> XpPanelLabel.format(snap.getXpRemainingToGoal())),
    ACTIONS_LEFT("Actions", snap -> XpPanelLabel.format(snap.getActionsRemainingToGoal())),
    ACTIONS_HOUR("Actions/hr", snap -> XpPanelLabel.format(snap.getActionsPerHour())),
    ACTIONS_DONE("Actions Done", snap -> XpPanelLabel.format(snap.getActionsInSession()));

    private final String key;
    private final Function<XpSnapshotSingle, String> valueFunc;

    private static String format(int val) {
        return val == Integer.MAX_VALUE ? "N/A" : QuantityFormatter.quantityToRSDecimalStack(val, true);
    }

    public String getKey() {
        return this.key;
    }

    public Function<XpSnapshotSingle, String> getValueFunc() {
        return this.valueFunc;
    }

    private XpPanelLabel(String key, Function<XpSnapshotSingle, String> valueFunc) {
        this.key = key;
        this.valueFunc = valueFunc;
    }
}

