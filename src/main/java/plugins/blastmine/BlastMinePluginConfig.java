/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.blastmine;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="blastmine")
public interface BlastMinePluginConfig
extends Config {
    @ConfigItem(position=0, keyName="showOreOverlay", name="Show ore overlay", description="Configures whether or not the ore count overlay is displayed.")
    default public boolean showOreOverlay() {
        return true;
    }

    @ConfigItem(position=1, keyName="showRockIconOverlay", name="Show icons overlay", description="Configures whether or not the icon overlay is displayed.")
    default public boolean showRockIconOverlay() {
        return true;
    }

    @ConfigItem(position=2, keyName="showTimerOverlay", name="Show timer overlay", description="Configures whether or not the timer overlay is displayed.")
    default public boolean showTimerOverlay() {
        return true;
    }

    @ConfigItem(position=3, keyName="showWarningOverlay", name="Show explosion warning", description="Configures whether or not the explosion warning overlay is displayed.")
    default public boolean showWarningOverlay() {
        return true;
    }

    @Alpha
    @ConfigItem(position=4, keyName="hexTimerColor", name="Timer color", description="Color of timer overlay.")
    default public Color getTimerColor() {
        return new Color(217, 54, 0);
    }

    @Alpha
    @ConfigItem(position=5, keyName="hexWarningColor", name="Warning color", description="Color of warning overlay.")
    default public Color getWarningColor() {
        return new Color(217, 54, 0);
    }
}

