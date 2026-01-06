/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.statusbars;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(value="statusbars")
public interface StatusBarsConfig
extends Config {
    public static final String GROUP = "statusbars";

    @ConfigItem(keyName="enableCounter", name="Show counters", description="Shows current value of the status on the bar.")
    default public boolean enableCounter() {
        return false;
    }

    @ConfigItem(keyName="enableSkillIcon", name="Show icons", description="Adds skill icons at the top of the bars.")
    default public boolean enableSkillIcon() {
        return true;
    }

    @ConfigItem(keyName="enableRestorationBars", name="Show restores", description="Visually shows how much will be restored to your status bar.")
    default public boolean enableRestorationBars() {
        return true;
    }

    @ConfigItem(keyName="leftBarMode", name="Left bar", description="Configures the left status bar.")
    default public BarMode leftBarMode() {
        return BarMode.HITPOINTS;
    }

    @ConfigItem(keyName="rightBarMode", name="Right bar", description="Configures the right status bar.")
    default public BarMode rightBarMode() {
        return BarMode.PRAYER;
    }

    @ConfigItem(keyName="hideAfterCombatDelay", name="Hide after combat delay", description="Amount of ticks before hiding status bars after no longer in combat. 0 = always show status bars.")
    @Units(value=" ticks")
    default public int hideAfterCombatDelay() {
        return 0;
    }

    @Range(min=3, max=40)
    @ConfigItem(keyName="barWidth", name="Bar width", description="The width of the status bars in the modern resizeable layout.")
    default public int barWidth() {
        return 20;
    }

    public static enum BarMode {
        DISABLED,
        HITPOINTS,
        PRAYER,
        RUN_ENERGY,
        SPECIAL_ATTACK,
        WARMTH;

    }
}

