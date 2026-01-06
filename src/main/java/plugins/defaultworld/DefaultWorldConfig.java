/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.defaultworld;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="defaultworld")
public interface DefaultWorldConfig
extends Config {
    public static final String GROUP = "defaultworld";

    @ConfigItem(keyName="defaultWorld", name="Default world", description="World to use as default one.")
    default public int getWorld() {
        return 0;
    }

    @ConfigItem(keyName="useLastWorld", name="Use last world", description="Use the last world you used as the default.")
    default public boolean useLastWorld() {
        return false;
    }

    @ConfigItem(keyName="lastWorld", name="", description="", hidden=true)
    default public int lastWorld() {
        return 0;
    }

    @ConfigItem(keyName="lastWorld", name="", description="")
    public void lastWorld(int var1);
}

