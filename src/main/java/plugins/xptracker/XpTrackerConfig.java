/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.xptracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Units;
import net.runelite.client.plugins.xptracker.XpPanelLabel;
import net.runelite.client.plugins.xptracker.XpProgressBarLabel;

@ConfigGroup(value="xpTracker")
public interface XpTrackerConfig
extends Config {
    @ConfigSection(name="Overlay", description="Canvas overlay options.", position=99)
    public static final String overlaySection = "overlay";

    @ConfigItem(keyName="saveState", name="Save between sessions", description="Saves xp trackers between sessions.")
    default public boolean saveState() {
        return true;
    }

    @ConfigItem(position=0, keyName="hideMaxed", name="Hide maxed skills", description="XP tracker will no longer track level 99 skills.")
    default public boolean hideMaxed() {
        return false;
    }

    @ConfigItem(position=1, keyName="logoutPausing", name="Pause on logout", description="Configures whether skills should pause on logout.")
    default public boolean pauseOnLogout() {
        return true;
    }

    @ConfigItem(position=2, keyName="intermediateLevelMarkers", name="Show intermediate level markers", description="Marks intermediate levels on the progress bar.")
    default public boolean showIntermediateLevels() {
        return false;
    }

    @ConfigItem(position=3, keyName="pauseSkillAfter", name="Auto pause after", description="Configures how many minutes passes before pausing a skill while in game and there's no XP, 0 means disabled.")
    @Units(value=" mins")
    default public int pauseSkillAfter() {
        return 0;
    }

    @ConfigItem(position=4, keyName="resetSkillRateAfter", name="Auto reset after", description="Configures how many minutes passes before resetting a skill's per hour rates while in game and there's no XP, 0 means disabled.")
    @Units(value=" mins")
    default public int resetSkillRateAfter() {
        return 0;
    }

    @ConfigItem(position=5, keyName="skillTabOverlayMenuOptions", name="Add skill tab canvas menu option", description="Configures whether a menu option to show/hide canvas XP trackers will be added to skills on the skill tab.", section="overlay")
    default public boolean skillTabOverlayMenuOptions() {
        return true;
    }

    @ConfigItem(position=6, keyName="onScreenDisplayMode", name="On-screen tracker display mode (top)", description="Configures the information displayed in the first line of on-screen XP overlays.", section="overlay")
    default public XpPanelLabel onScreenDisplayMode() {
        return XpPanelLabel.XP_GAINED;
    }

    @ConfigItem(position=7, keyName="onScreenDisplayModeBottom", name="On-screen tracker display mode (bottom)", description="Configures the information displayed in the second line of on-screen XP overlays.", section="overlay")
    default public XpPanelLabel onScreenDisplayModeBottom() {
        return XpPanelLabel.XP_HOUR;
    }

    @ConfigItem(position=8, keyName="xpPanelLabel1", name="Top-left XP info label", description="Configures the information displayed in the top-left of XP info box.")
    default public XpPanelLabel xpPanelLabel1() {
        return XpPanelLabel.XP_GAINED;
    }

    @ConfigItem(position=9, keyName="xpPanelLabel2", name="Top-right XP info label", description="Configures the information displayed in the top-right of XP info box.")
    default public XpPanelLabel xpPanelLabel2() {
        return XpPanelLabel.XP_LEFT;
    }

    @ConfigItem(position=10, keyName="xpPanelLabel3", name="Bottom-left XP info label", description="Configures the information displayed in the bottom-left of XP info box.")
    default public XpPanelLabel xpPanelLabel3() {
        return XpPanelLabel.XP_HOUR;
    }

    @ConfigItem(position=11, keyName="xpPanelLabel4", name="Bottom-right XP info label", description="Configures the information displayed in the bottom-right of XP info box.")
    default public XpPanelLabel xpPanelLabel4() {
        return XpPanelLabel.ACTIONS_LEFT;
    }

    @ConfigItem(position=12, keyName="progressBarLabel", name="Progress bar label", description="Configures the info box progress bar to show time to goal or percentage complete.")
    default public XpProgressBarLabel progressBarLabel() {
        return XpProgressBarLabel.PERCENTAGE;
    }

    @ConfigItem(position=13, keyName="progressBarTooltipLabel", name="Tooltip label", description="Configures the info box progress bar tooltip to show time to goal or percentage complete.")
    default public XpProgressBarLabel progressBarTooltipLabel() {
        return XpProgressBarLabel.TIME_TO_LEVEL;
    }

    @ConfigItem(position=14, keyName="prioritizeRecentXpSkills", name="Move recently trained skills to top", description="Configures whether skills should be organized by most recently gained XP.")
    default public boolean prioritizeRecentXpSkills() {
        return false;
    }

    @ConfigItem(position=15, keyName="wiseOldManOpenOption", name="Wise Old Man option", description="Adds an option to the XP info box right-click menu to open Wise Old Man.")
    default public boolean wiseOldManOpenOption() {
        return true;
    }
}

