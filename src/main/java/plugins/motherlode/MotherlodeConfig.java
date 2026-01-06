/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.motherlode;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="motherlode")
public interface MotherlodeConfig
extends Config {
    @ConfigItem(keyName="showVeins", name="Show pay-dirt mining spots", description="Configures whether or not the pay-dirt mining spots are displayed.")
    default public boolean showVeins() {
        return true;
    }

    @ConfigItem(keyName="showRocks", name="Show rocks obstacles", description="Configures whether or not the fallen rocks obstacles are displayed.")
    default public boolean showRockFalls() {
        return true;
    }

    @ConfigItem(keyName="showGemsFound", name="Track gems found", description="Tracks gems found from mining in the loot tracker.")
    default public boolean trackGemsFound() {
        return true;
    }

    @ConfigItem(keyName="showOresFound", name="Track ores found", description="Tracks ores found from mining in the loot tracker.")
    default public boolean trackOresFound() {
        return true;
    }

    @ConfigItem(keyName="showBrokenStruts", name="Show broken struts", description="Shows broken water wheel struts.")
    default public boolean showBrokenStruts() {
        return true;
    }
}

