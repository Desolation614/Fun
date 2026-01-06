/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.pyramidplunder;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="pyramidplunder")
public interface PyramidPlunderConfig
extends Config {
    @ConfigItem(position=0, keyName="hideTimer", name="Hide default timer", description="Hides the default pyramid plunder timer.")
    default public boolean hideTimer() {
        return true;
    }

    @ConfigItem(position=1, keyName="showExactTimer", name="Show exact timer", description="Displays the amount of time remaining as an infobox.")
    default public boolean showExactTimer() {
        return true;
    }

    @ConfigItem(position=2, keyName="timerLowWarning", name="Timer low warning", description="Determines the time when the timers color will change.")
    default public int timerLowWarning() {
        return 30;
    }

    @Alpha
    @ConfigItem(position=3, keyName="highlightDoorsColor", name="Highlight doors", description="Selects the color for highlighting tomb doors.")
    default public Color highlightDoorsColor() {
        return Color.green;
    }

    @ConfigItem(position=4, keyName="highlightDoors", name="Highlight doors", description="Highlights the four tomb doors in each room.")
    default public boolean highlightDoors() {
        return true;
    }

    @Alpha
    @ConfigItem(position=5, keyName="highlightSpeartrapColor", name="Highlight speartrap", description="Selects the color for highlighting speartraps.")
    default public Color highlightSpeartrapsColor() {
        return Color.orange;
    }

    @ConfigItem(position=6, keyName="highlightSpeartraps", name="Highlight speartraps", description="Highlight the spear traps at the entrance of each room.")
    default public boolean highlightSpeartraps() {
        return true;
    }

    @Alpha
    @ConfigItem(position=7, keyName="highlightContainersColor", name="Highlight containers", description="Selects the color for highlighting urns, chests and sarcophagus.")
    default public Color highlightContainersColor() {
        return Color.yellow;
    }

    @ConfigItem(position=8, keyName="highlightUrnsFloor", name="Highlight urns floor", description="Highlight the urns starting at selected floor and up.")
    default public int highlightUrnsFloor() {
        return 9;
    }

    @ConfigItem(position=9, keyName="highlightedChestFloor", name="Highlight chest floor", description="Highlight the grand gold chest starting at selected floor and up.")
    default public int highlightChestFloor() {
        return 9;
    }

    @ConfigItem(position=10, keyName="highlightedSarcophagusFloor", name="Highlight sarcophagus floor", description="Highlight the sarcophagus starting at selected floor and up.")
    default public int highlightSarcophagusFloor() {
        return 9;
    }
}

