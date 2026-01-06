/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.reportbutton;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.reportbutton.TimeFormat;
import net.runelite.client.plugins.reportbutton.TimeStyle;

@ConfigGroup(value="reportButton")
public interface ReportButtonConfig
extends Config {
    @ConfigItem(keyName="time", name="Display options", description="Configures what text the report button shows.")
    default public TimeStyle time() {
        return TimeStyle.LOGIN_TIME;
    }

    @ConfigItem(keyName="switchTimeFormat", name="Time format", description="Configures time between 12 or 24 hour time format.")
    default public TimeFormat switchTimeFormat() {
        return TimeFormat.TIME_12H;
    }
}

