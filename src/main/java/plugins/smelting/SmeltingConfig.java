/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.smelting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup(value="smelting")
public interface SmeltingConfig
extends Config {
    @ConfigItem(position=1, keyName="statTimeout", name="Reset stats", description="The time it takes for the current smelting session to be reset.")
    @Units(value=" mins")
    default public int statTimeout() {
        return 5;
    }
}

