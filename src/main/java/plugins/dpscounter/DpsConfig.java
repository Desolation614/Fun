/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.dpscounter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="dpscounter")
public interface DpsConfig
extends Config {
    @ConfigItem(position=0, keyName="showDamage", name="Show damage", description="Show total damage instead of DPS.")
    default public boolean showDamage() {
        return false;
    }

    @ConfigItem(position=1, keyName="autopause", name="Auto pause", description="Pause the DPS tracker when a boss dies.")
    default public boolean autopause() {
        return false;
    }

    @ConfigItem(position=2, keyName="autoreset", name="Auto reset", description="Reset the DPS tracker when a boss dies.")
    default public boolean autoreset() {
        return false;
    }

    @ConfigItem(position=3, keyName="bossDamage", name="Only boss damage", description="Only count damage done to the boss, and not to other NPCs.")
    default public boolean bossDamage() {
        return false;
    }
}

