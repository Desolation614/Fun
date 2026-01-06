/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.virtuallevels;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="virtuallevels")
public interface VirtualLevelsConfig
extends Config {
    @ConfigItem(keyName="virtualTotalLevel", name="Virtual total level", description="Count virtual levels towards total level.", position=0)
    default public boolean virtualTotalLevel() {
        return true;
    }
}

