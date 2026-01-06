/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.poison;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="poison")
public interface PoisonConfig
extends Config {
    public static final String GROUP = "poison";

    @ConfigItem(keyName="showInfoboxes", name="Show infoboxes", description="Configures whether to show the infoboxes.")
    default public boolean showInfoboxes() {
        return false;
    }

    @ConfigItem(keyName="changeHealthIcon", name="Change HP orb icon", description="Configures whether the HP orb icon should change color to match poison/disease.")
    default public boolean changeHealthIcon() {
        return true;
    }
}

