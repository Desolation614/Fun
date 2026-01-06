/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.entityhider;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="entityhider")
public interface EntityHiderConfig
extends Config {
    public static final String GROUP = "entityhider";

    @ConfigItem(position=1, keyName="hidePlayers", name="Hide others", description="Configures whether or not other players are hidden.")
    default public boolean hideOthers() {
        return true;
    }

    @ConfigItem(position=2, keyName="hidePlayers2D", name="Hide others 2D", description="Configures whether or not other players 2D elements are hidden.")
    default public boolean hideOthers2D() {
        return true;
    }

    @ConfigItem(position=3, keyName="hidePartyMembers", name="Hide party members", description="Configures whether or not party members are hidden.")
    default public boolean hidePartyMembers() {
        return false;
    }

    @ConfigItem(position=4, keyName="hideFriends", name="Hide friends", description="Configures whether or not friends are hidden.")
    default public boolean hideFriends() {
        return false;
    }

    @ConfigItem(position=5, keyName="hideClanMates", name="Hide friends chat members", description="Configures whether or not friends chat members are hidden.")
    default public boolean hideFriendsChatMembers() {
        return false;
    }

    @ConfigItem(position=6, keyName="hideClanChatMembers", name="Hide clan chat members", description="Configures whether or not clan chat members are hidden.")
    default public boolean hideClanChatMembers() {
        return false;
    }

    @ConfigItem(position=7, keyName="hideIgnores", name="Hide ignores", description="Configures whether or not ignored players are hidden.")
    default public boolean hideIgnores() {
        return false;
    }

    @ConfigItem(position=8, keyName="hideLocalPlayer", name="Hide local player", description="Configures whether or not the local player is hidden.")
    default public boolean hideLocalPlayer() {
        return false;
    }

    @ConfigItem(position=9, keyName="hideLocalPlayer2D", name="Hide local player 2D", description="Configures whether or not the local player's 2D elements are hidden.")
    default public boolean hideLocalPlayer2D() {
        return false;
    }

    @ConfigItem(position=10, keyName="hideNPCs", name="Hide NPCs", description="Configures whether or not NPCs are hidden.")
    default public boolean hideNPCs() {
        return false;
    }

    @ConfigItem(position=11, keyName="hideNPCs2D", name="Hide NPCs 2D", description="Configures whether or not NPCs 2D elements are hidden.")
    default public boolean hideNPCs2D() {
        return false;
    }

    @ConfigItem(position=12, keyName="hidePets", name="Hide other players' pets", description="Configures whether or not other player pets are hidden.")
    default public boolean hidePets() {
        return false;
    }

    @ConfigItem(position=13, keyName="hideAttackers", name="Hide attackers", description="Configures whether or not NPCs/players attacking you are hidden.")
    default public boolean hideAttackers() {
        return false;
    }

    @ConfigItem(position=14, keyName="hideProjectiles", name="Hide projectiles", description="Configures whether or not projectiles are hidden.")
    default public boolean hideProjectiles() {
        return false;
    }

    @ConfigItem(position=15, keyName="hideDeadNpcs", name="Hide dead NPCs", description="Hides NPCs when their health reaches 0.")
    default public boolean hideDeadNpcs() {
        return false;
    }

    @ConfigItem(position=16, keyName="hideThralls", name="Hide thralls", description="Configures whether or not thralls are hidden.")
    default public boolean hideThralls() {
        return false;
    }

    @ConfigItem(position=17, keyName="hideRandomEvents", name="Hide random events", description="Configures whether or not random events are hidden.")
    default public boolean hideRandomEvents() {
        return false;
    }
}

