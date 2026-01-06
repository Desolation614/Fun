/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.discord;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup(value="discord")
public interface DiscordConfig
extends Config {
    @ConfigItem(keyName="elapsedTime", name="Elapsed time", description="Configures elapsed time shown.", position=1)
    default public ElapsedTimeType elapsedTimeType() {
        return ElapsedTimeType.TOTAL;
    }

    @ConfigItem(keyName="actionTimeout", name="Activity timeout", description="Configures after how long of not updating activity will be reset (in minutes).", position=2)
    @Units(value=" mins")
    default public int actionTimeout() {
        return 5;
    }

    @ConfigItem(keyName="showMainMenu", name="Main menu", description="Show status when in main menu.", position=3)
    default public boolean showMainMenu() {
        return true;
    }

    @ConfigItem(keyName="showSkillActivity", name="Skilling", description="Show your activity while training skills.", position=4)
    default public boolean showSkillingActivity() {
        return true;
    }

    @ConfigItem(keyName="showBossActivity", name="Bosses", description="Show your activity and location while at bosses.", position=5)
    default public boolean showBossActivity() {
        return true;
    }

    @ConfigItem(keyName="showCityActivity", name="Cities", description="Show your activity and location while in cities.", position=6)
    default public boolean showCityActivity() {
        return true;
    }

    @ConfigItem(keyName="showDungeonActivity", name="Dungeons", description="Show your activity and location while in dungeons.", position=7)
    default public boolean showDungeonActivity() {
        return true;
    }

    @ConfigItem(keyName="showMinigameActivity", name="Minigames", description="Show your activity and location while in minigames.", position=8)
    default public boolean showMinigameActivity() {
        return true;
    }

    @ConfigItem(keyName="showRaidingActivity", name="Raids", description="Show your activity and location while in raids.", position=9)
    default public boolean showRaidingActivity() {
        return true;
    }

    @ConfigItem(keyName="showRegionsActivity", name="Regions", description="Show your activity and location while in other regions.", position=10)
    default public boolean showRegionsActivity() {
        return true;
    }

    public static enum ElapsedTimeType {
        TOTAL("Total elapsed time"),
        ACTIVITY("Per activity"),
        HIDDEN("Hide elapsed time");

        private final String value;

        public String toString() {
            return this.value;
        }

        private ElapsedTimeType(String value) {
            this.value = value;
        }
    }
}

