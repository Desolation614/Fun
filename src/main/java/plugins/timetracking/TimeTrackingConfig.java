/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Units;
import net.runelite.client.plugins.timetracking.SortOrder;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.TimeFormatMode;

@ConfigGroup(value="timetracking")
public interface TimeTrackingConfig
extends Config {
    public static final String CONFIG_GROUP = "timetracking";
    public static final String FARM_TICK_OFFSET = "farmTickOffset";
    public static final String FARM_TICK_OFFSET_PRECISION = "farmTickOffsetPrecision";
    public static final String AUTOWEED = "autoweed";
    public static final String BIRD_HOUSE = "birdhouse";
    public static final String TIMERS = "timers";
    public static final String STOPWATCHES = "stopwatches";
    public static final String PREFER_SOONEST = "preferSoonest";
    public static final String NOTIFY = "notify";
    public static final String BIRDHOUSE_NOTIFY = "birdHouseNotification";
    public static final String COMPOST = "compost";
    public static final String PROTECTED = "protected";

    @ConfigItem(keyName="timeFormatMode", name="Time format", description="What format to display times in.", position=1)
    default public TimeFormatMode timeFormatMode() {
        return TimeFormatMode.ABSOLUTE_24H;
    }

    @ConfigItem(keyName="timerNotification", name="Timer notification", description="Notify you whenever a timer has finished counting down.", position=2)
    default public Notification timerNotification() {
        return Notification.OFF;
    }

    @ConfigItem(keyName="farmingContractInfoBox", name="Show farming contract infobox", description="Show an infobox of your current farming contract when inside the farming guild.", position=4)
    default public boolean farmingContractInfoBox() {
        return true;
    }

    @ConfigItem(keyName="defaultTimerMinutes", name="Default time", description="The default time for the timer in minutes.", position=5)
    @Units(value=" mins")
    default public int defaultTimerMinutes() {
        return 5;
    }

    @ConfigItem(keyName="sortOrder", name="Sort order", description="The order in which to sort the timers.", position=6)
    default public SortOrder sortOrder() {
        return SortOrder.NONE;
    }

    @ConfigItem(keyName="timerWarningThreshold", name="Warning threshold", description="The time at which to change the timer color to the warning color.", position=6)
    @Units(value="s")
    default public int timerWarningThreshold() {
        return 10;
    }

    @ConfigItem(keyName="preferSoonest", name="Prefer soonest completion", description="When displaying completion times on the overview, prefer showing the soonest any patch will complete.", position=7)
    default public boolean preferSoonest() {
        return false;
    }

    @ConfigItem(keyName="activeTab", name="Active tab", description="The currently selected tab.", hidden=true)
    default public Tab activeTab() {
        return Tab.CLOCK;
    }

    @ConfigItem(keyName="activeTab", name="", description="", hidden=true)
    public void setActiveTab(Tab var1);
}

