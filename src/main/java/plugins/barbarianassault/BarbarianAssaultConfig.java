/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.barbarianassault;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="barbarianAssault")
public interface BarbarianAssaultConfig
extends Config {
    @ConfigItem(keyName="showTimer", name="Show call change timer", description="Show time to next call change.")
    default public boolean showTimer() {
        return true;
    }

    @ConfigItem(keyName="showHealerBars", name="Show health bars for teammates when healer", description="Displays team health for healer.")
    default public boolean showHealerBars() {
        return true;
    }

    @ConfigItem(keyName="waveTimes", name="Show wave and game duration", description="Displays wave and game duration.")
    default public boolean waveTimes() {
        return true;
    }
}

