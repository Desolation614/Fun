/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.hiscore;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="hiscore")
public interface HiscoreConfig
extends Config {
    @ConfigItem(position=1, keyName="playerOption", name="Player option", description="Add Lookup option to players.")
    default public boolean playerOption() {
        return true;
    }

    @ConfigItem(position=2, keyName="menuOption", name="Menu option", description="Show Lookup option in menus.")
    default public boolean menuOption() {
        return true;
    }

    @ConfigItem(position=3, keyName="virtualLevels", name="Display virtual levels", description="Display levels over 99 in the hiscore panel.")
    default public boolean virtualLevels() {
        return true;
    }

    @ConfigItem(position=4, keyName="autocomplete", name="Autocomplete", description="Predict names when typing a name to lookup.")
    default public boolean autocomplete() {
        return true;
    }

    @ConfigItem(position=5, keyName="bountylookup", name="Bounty lookup", description="Automatically lookup the stats of your bounty hunter target.")
    default public boolean bountylookup() {
        return false;
    }
}

