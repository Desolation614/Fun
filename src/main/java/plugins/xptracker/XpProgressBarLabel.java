/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.xptracker;

import java.util.function.Function;
import net.runelite.client.plugins.xptracker.XpInfoBox;
import net.runelite.client.plugins.xptracker.XpSnapshotSingle;

public enum XpProgressBarLabel {
    PERCENTAGE(snap -> XpInfoBox.TWO_DECIMAL_FORMAT.format(snap.getSkillProgressToGoal()) + "%"),
    TIME_TO_LEVEL(XpSnapshotSingle::getTimeTillGoal),
    HOURS_TO_LEVEL(XpSnapshotSingle::getTimeTillGoalHours);

    private final Function<XpSnapshotSingle, String> valueFunc;

    public Function<XpSnapshotSingle, String> getValueFunc() {
        return this.valueFunc;
    }

    private XpProgressBarLabel(Function<XpSnapshotSingle, String> valueFunc) {
        this.valueFunc = valueFunc;
    }
}

