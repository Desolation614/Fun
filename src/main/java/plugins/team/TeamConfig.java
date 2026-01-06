/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.team;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(value="teamCapes")
public interface TeamConfig
extends Config {
    public static final String GROUP = "teamCapes";
    @ConfigSection(name="Team", description="Configuration for teams.", position=10)
    public static final String teamSection = "teamSection";
    @ConfigSection(name="Friends chat", description="Configuration for friends chat.", position=20)
    public static final String friendsChatSection = "friendsChatSection";
    @ConfigSection(name="Clan chat", description="Configuration for clan chat.", position=30)
    public static final String clanChatSection = "clanChatSection";

    @ConfigItem(keyName="teamCapesOverlay", name="Team cape overlay", description="Configures whether to show the team cape overlay.", position=0, section="teamSection")
    default public boolean teamCapesOverlay() {
        return false;
    }

    @ConfigItem(keyName="minimumCapeCount", name="Minimum cape count", description="Configures the minimum number of team capes which must be present before being displayed.", position=1, section="teamSection")
    default public int getMinimumCapeCount() {
        return 1;
    }

    @ConfigItem(keyName="friendsChatMemberCounter", name="Friends chat members counter", description="Show the amount of friends chat members near you.", position=0, section="friendsChatSection")
    default public boolean friendsChatMemberCounter() {
        return false;
    }

    @ConfigItem(keyName="clanChatMemberCounter", name="Clan chat members counter", description="Show the amount of clan chat members near you.", position=0, section="clanChatSection")
    default public boolean clanChatMemberCounter() {
        return false;
    }
}

