/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.blastfurnace;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="blastfurnace")
public interface BlastFurnaceConfig
extends Config {
    @ConfigItem(keyName="showConveyorBelt", name="Show conveyor belt clickbox", description="Configures whether or not the clickbox for the conveyor belt is displayed.", position=1)
    default public boolean showConveyorBelt() {
        return false;
    }

    @ConfigItem(keyName="showBarDispenser", name="Show bar dispenser clickbox", description="Configures whether or not the clickbox for the bar dispenser is displayed.", position=2)
    default public boolean showBarDispenser() {
        return false;
    }

    @ConfigItem(keyName="showCofferTime", name="Show coffer time remaining", description="Configures whether or not the coffer time remaining is displayed.", position=3)
    default public boolean showCofferTime() {
        return true;
    }
}

