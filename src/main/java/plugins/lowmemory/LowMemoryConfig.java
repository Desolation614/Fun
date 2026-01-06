/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.lowmemory;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="lowmemory")
public interface LowMemoryConfig
extends Config {
    public static final String GROUP = "lowmemory";

    @ConfigItem(keyName="lowDetail", name="Low detail", description="Hides ground detail and simplifies textures.", position=0)
    default public boolean lowDetail() {
        return true;
    }

    @ConfigItem(keyName="hideLowerPlanes", name="Hide lower planes", description="Only renders the current plane you are on.", position=1)
    default public boolean hideLowerPlanes() {
        return false;
    }
}

