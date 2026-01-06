/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.combatlevel;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="combatlevel")
public interface CombatLevelConfig
extends Config {
    @ConfigItem(keyName="showLevelsUntil", name="Calculate next level", description="Mouse over the combat level to calculate what skill levels will increase combat.")
    default public boolean showLevelsUntil() {
        return true;
    }

    @ConfigItem(keyName="showPreciseCombatLevel", name="Show precise combat level", description="Displays your combat level with accurate decimals.")
    default public boolean showPreciseCombatLevel() {
        return true;
    }

    @ConfigItem(keyName="wildernessAttackLevelRange", name="Show level range in wilderness", description="Displays a PvP-world-like attack level range in the wilderness.")
    default public boolean wildernessAttackLevelRange() {
        return true;
    }
}

