/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.fishing;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Units;

@ConfigGroup(value="fishing")
public interface FishingConfig
extends Config {
    @ConfigItem(position=0, keyName="onlyCurrent", name="Display only currently fished fish", description="Configures whether only current fished fish's fishing spots are displayed.")
    default public boolean onlyCurrentSpot() {
        return false;
    }

    @ConfigItem(position=1, keyName="showTiles", name="Display spot tiles", description="Configures whether tiles for fishing spots are highlighted.")
    default public boolean showSpotTiles() {
        return true;
    }

    @ConfigItem(position=2, keyName="showIcons", name="Display spot icons", description="Configures whether icons for fishing spots are displayed.")
    default public boolean showSpotIcons() {
        return true;
    }

    @ConfigItem(position=3, keyName="showNames", name="Display spot names", description="Configures whether names for fishing spots are displayed.")
    default public boolean showSpotNames() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName="overlayColor", name="Overlay color", description="Color of overlays.", position=4)
    default public Color getOverlayColor() {
        return Color.CYAN;
    }

    @Alpha
    @ConfigItem(keyName="minnowsOverlayColor", name="Minnows overlay", description="Color of overlays for minnows.", position=5)
    default public Color getMinnowsOverlayColor() {
        return Color.RED;
    }

    @Alpha
    @ConfigItem(keyName="aerialOverlayColor", name="Aerial overlay", description="Color of overlays when 1-tick aerial fishing.", position=6)
    default public Color getAerialOverlayColor() {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(keyName="harpoonfishOverlayColor", name="Harpoonfish overlay", description="Color of overlays for bubbling harpoonfish spots.", position=6)
    default public Color getHarpoonfishOverlayColor() {
        return Color.GREEN;
    }

    @ConfigItem(position=7, keyName="statTimeout", name="Reset stats", description="The time until fishing session data is reset in minutes.")
    @Units(value=" mins")
    default public int statTimeout() {
        return 5;
    }

    @ConfigItem(position=8, keyName="showFishingStats", name="Show fishing session stats", description="Display the fishing session stats.")
    default public boolean showFishingStats() {
        return true;
    }

    @ConfigItem(position=9, keyName="showMinnowOverlay", name="Show minnow movement overlay", description="Display the minnow progress pie overlay.")
    default public boolean showMinnowOverlay() {
        return true;
    }

    @ConfigItem(position=10, keyName="flyingFishNotification", name="Flying fish notification", description="Send a notification when a flying fish spawns on your fishing spot.")
    default public Notification flyingFishNotification() {
        return Notification.ON;
    }

    @ConfigItem(position=11, keyName="trawlerTimer", name="Trawler timer in m:ss", description="Trawler timer will display a more accurate timer in M:SS format.")
    default public boolean trawlerTimer() {
        return true;
    }

    @ConfigItem(position=12, keyName="trawlerContribution", name="Trawler contribution", description="Display the exact number of trawler contribution points gained.")
    default public boolean trawlerContribution() {
        return true;
    }
}

