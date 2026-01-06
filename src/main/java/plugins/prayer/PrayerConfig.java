/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.prayer;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.prayer.PrayerFlickLocation;

@ConfigGroup(value="prayer")
public interface PrayerConfig
extends Config {
    public static final String GROUP = "prayer";

    @ConfigItem(position=0, keyName="prayerFlickLocation", name="Pray flick location", description="Choose where to display the prayer flick helper.")
    default public PrayerFlickLocation prayerFlickLocation() {
        return PrayerFlickLocation.NONE;
    }

    @Alpha
    @ConfigItem(position=1, keyName="prayerFlickColor", name="Pray flick color", description="Color of the flick helper on the prayer orb and prayer bar.")
    default public Color prayerFlickColor() {
        return Color.CYAN;
    }

    @ConfigItem(position=2, keyName="prayerFlickAlwaysOn", name="Never hide prayer flick helper", description="Show prayer flick helper regardless of if you're praying or not.")
    default public boolean prayerFlickAlwaysOn() {
        return false;
    }

    @ConfigItem(position=3, keyName="prayerIndicator", name="Boost indicator", description="Enable infoboxes for prayers.")
    default public boolean prayerIndicator() {
        return false;
    }

    @ConfigItem(position=4, keyName="prayerIndicatorOverheads", name="Overhead indicator", description="Also enable infoboxes for overheads.")
    default public boolean prayerIndicatorOverheads() {
        return false;
    }

    @ConfigItem(position=5, keyName="showPrayerDoseIndicator", name="Show prayer dose indicator", description="Enables the prayer dose indicator.")
    default public boolean showPrayerDoseIndicator() {
        return true;
    }

    @Alpha
    @ConfigItem(position=6, keyName="prayerDoseOrbStartColor", name="Dose indicator color", description="Color of the flashing ring around the prayer orb when a potion should be drank.")
    default public Color prayerDoseOrbStartColor() {
        return Color.CYAN;
    }

    @ConfigItem(position=7, keyName="showPrayerTooltip", name="Show prayer orb tooltip", description="Displays time remaining and prayer bonus as a tooltip on the quick-prayer icon.")
    default public boolean showPrayerStatistics() {
        return true;
    }

    @ConfigItem(position=8, keyName="showPrayerBar", name="Show prayer bar", description="Displays prayer bar under HP bar when praying.")
    default public boolean showPrayerBar() {
        return false;
    }

    @ConfigItem(position=9, keyName="prayerBarHideIfNotPraying", name="Hide bar while prayer is inactive", description="Prayer bar will be hidden while prayers are inactive.")
    default public boolean hideIfNotPraying() {
        return true;
    }

    @ConfigItem(position=10, keyName="prayerBarHideIfNonCombat", name="Hide bar while out-of-combat", description="Prayer bar will be hidden while out-of-combat.")
    default public boolean hideIfOutOfCombat() {
        return false;
    }

    @ConfigItem(position=11, keyName="replaceOrbText", name="Show time left", description="Show time remaining of current prayers in the prayer orb.")
    default public boolean replaceOrbText() {
        return false;
    }
}

