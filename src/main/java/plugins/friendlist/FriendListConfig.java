/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.friendlist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="friendlist")
public interface FriendListConfig
extends Config {
    public static final String GROUP = "friendlist";

    @ConfigItem(keyName="showWorldOnLogin", name="Show world on login", description="Shows world number on friend login notifications.")
    default public boolean showWorldOnLogin() {
        return false;
    }
}

