/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.specialcounter;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Notification;

@ConfigGroup(value="specialcounter")
public interface SpecialCounterConfig
extends Config {
    @ConfigItem(position=0, keyName="thresholdNotification", name="Threshold notifications", description="Sends a notification when your special attack counter exceeds the threshold.")
    default public Notification thresholdNotification() {
        return Notification.OFF;
    }

    @ConfigItem(position=1, keyName="specDrops", name="Spec drops", description="Draws an overlay over the player when a special attack hits.")
    default public boolean specDrops() {
        return true;
    }

    @ConfigItem(position=1, keyName="specDropMisses", name="Spec drop misses", description="Draws an overlay over the player when a special attack misses.")
    default public boolean specDropMisses() {
        return false;
    }

    @ConfigItem(position=2, keyName="specDropColor", name="Spec drop color", description="Text color for spec drops.")
    default public Color specDropColor() {
        return Color.WHITE;
    }

    @ConfigItem(position=3, keyName="infobox", name="Infobox", description="Adds an infobox counting special attacks.")
    default public boolean infobox() {
        return true;
    }

    @ConfigItem(position=3, keyName="defenceDrainInfobox", name="Show defence drain infobox", description="If infoboxes are enabled, adds a defence drain percentage infobox for Dragon warhammer and Elder maul.")
    default public boolean defenceDrainInfobox() {
        return true;
    }

    @ConfigItem(position=10, keyName="dragonWarhammerThreshold", name="Dragon warhammer", description="Threshold for Dragon warhammer (0 to disable).")
    default public int dragonWarhammerThreshold() {
        return 0;
    }

    @ConfigItem(position=15, keyName="elderMaulThreshold", name="Elder maul", description="Threshold for Elder maul (0 to disable).")
    default public int elderMaulThreshold() {
        return 0;
    }

    @ConfigItem(position=20, keyName="arclightThreshold", name="Arclight", description="Threshold for Arclight (0 to disable).")
    default public int arclightThreshold() {
        return 0;
    }

    @ConfigItem(position=30, keyName="darklightThreshold", name="Darklight", description="Threshold for Darklight (0 to disable).")
    default public int darklightThreshold() {
        return 0;
    }

    @ConfigItem(position=31, keyName="emberlightThreshold", name="Emberlight", description="Threshold for Emberlight (0 to disable).")
    default public int emberlightThreshold() {
        return 0;
    }

    @ConfigItem(position=40, keyName="bandosGodswordThreshold", name="Bandos godsword", description="Threshold for Bandos godsword (0 to disable).")
    default public int bandosGodswordThreshold() {
        return 0;
    }

    @ConfigItem(position=50, keyName="bulwarkThreshold", name="Dinh's bulwark", description="Threshold for Dinh's bulwark (0 to disable).")
    default public int bulwarkThreshold() {
        return 0;
    }
}

