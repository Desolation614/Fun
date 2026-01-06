/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.regenmeter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup(value="regenmeter")
public interface RegenMeterConfig
extends Config {
    @ConfigItem(keyName="showHitpoints", name="Show hitpoints regen", description="Show a ring around the hitpoints orb.")
    default public boolean showHitpoints() {
        return true;
    }

    @ConfigItem(keyName="showSpecial", name="Show spec. attack regen", description="Show a ring around the special attack orb.")
    default public boolean showSpecial() {
        return true;
    }

    @ConfigItem(keyName="showWhenNoChange", name="Show at full hitpoints", description="Always show the hitpoints regen orb, even if there will be no stat change.")
    default public boolean showWhenNoChange() {
        return false;
    }

    @ConfigItem(keyName="notifyBeforeHpRegenDuration", name="Hitpoint notification", description="Notify approximately when your next hitpoint is about to regen. A value of 0 will disable notification.")
    @Units(value="s")
    default public int getNotifyBeforeHpRegenSeconds() {
        return 0;
    }
}

