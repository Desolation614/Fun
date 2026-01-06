/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.itemstats;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="itemstat")
public interface ItemStatConfig
extends Config {
    @ConfigItem(keyName="consumableStats", name="Enable consumable stats", description="Enables tooltips for consumable items (food, boosts).")
    default public boolean consumableStats() {
        return true;
    }

    @ConfigItem(keyName="equipmentStats", name="Enable equipment stats", description="Enables tooltips for equipment items (combat bonuses, weight, prayer bonuses).")
    default public boolean equipmentStats() {
        return true;
    }

    @ConfigItem(keyName="geStats", name="Enable GE item information", description="Shows an item information panel when buying items in the GE.")
    default public boolean geStats() {
        return true;
    }

    @ConfigItem(keyName="relative", name="Show relative", description="Show relative stat change in tooltip.")
    default public boolean relative() {
        return true;
    }

    @ConfigItem(keyName="absolute", name="Show absolute", description="Show absolute stat change in tooltip.")
    default public boolean absolute() {
        return true;
    }

    @ConfigItem(keyName="theoretical", name="Show theoretical", description="Show theoretical stat change in tooltip.")
    default public boolean theoretical() {
        return false;
    }

    @ConfigItem(keyName="showWeight", name="Show weight", description="Show weight in tooltip.")
    default public boolean showWeight() {
        return true;
    }

    @ConfigItem(keyName="showStatsInBank", name="Show stats in bank", description="Show item stats on bank items tooltip.")
    default public boolean showStatsInBank() {
        return true;
    }

    @ConfigItem(keyName="alwaysShowBaseStats", name="Always show base stats", description="Always include the base items stats in the tooltip.")
    default public boolean alwaysShowBaseStats() {
        return false;
    }

    @ConfigItem(keyName="colorBetterUncapped", name="Better (uncapped)", description="Color to show when the stat change is fully consumed.", position=10)
    default public Color colorBetterUncapped() {
        return new Color(0x33EE33);
    }

    @ConfigItem(keyName="colorBetterSomecapped", name="Better (some capped)", description="Color to show when some stat changes are capped, but some are not.", position=11)
    default public Color colorBetterSomeCapped() {
        return new Color(10284595);
    }

    @ConfigItem(keyName="colorBetterCapped", name="Better (capped)", description="Color to show when the stat change is positive, but not fully consumed.", position=12)
    default public Color colorBetterCapped() {
        return new Color(0xEEEE33);
    }

    @ConfigItem(keyName="colorNoChange", name="No change", description="Color to show when there is no change.", position=13)
    default public Color colorNoChange() {
        return new Color(0xEEEEEE);
    }

    @ConfigItem(keyName="colorWorse", name="Worse", description="Color to show when the stat goes down.", position=14)
    default public Color colorWorse() {
        return new Color(0xEE3333);
    }
}

