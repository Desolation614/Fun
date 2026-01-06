/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.mta;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="mta")
public interface MTAConfig
extends Config {
    @ConfigItem(keyName="alchemy", name="Enable alchemy room", description="Configures whether or not the alchemy room overlay is enabled.", position=0)
    default public boolean alchemy() {
        return true;
    }

    @ConfigItem(keyName="graveyard", name="Enable graveyard room", description="Configures whether or not the graveyard room overlay is enabled.", position=1)
    default public boolean graveyard() {
        return true;
    }

    @ConfigItem(keyName="telekinetic", name="Enable telekinetic room", description="Configures whether or not the telekinetic room overlay is enabled.", position=2)
    default public boolean telekinetic() {
        return true;
    }

    @ConfigItem(keyName="enchantment", name="Enable enchantment room", description="Configures whether or not the enchantment room overlay is enabled.", position=3)
    default public boolean enchantment() {
        return true;
    }
}

