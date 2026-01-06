/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.FriendsChatRank
 */
package net.runelite.client.plugins.chatchannel;

import java.awt.Color;
import net.runelite.api.FriendsChatRank;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(value="clanchat")
public interface ChatChannelConfig
extends Config {
    public static final String GROUP = "clanchat";
    @ConfigSection(name="Friends chat", description="Configuration for friends chat.", position=10)
    public static final String friendsChatSection = "friendsChat";
    @ConfigSection(name="Clan chat", description="Configuration for clan chat.", position=20)
    public static final String clanChatSection = "clanChat";
    @ConfigSection(name="Guest clan chat", description="Configuration for guest clan chat.", position=30)
    public static final String guestClanChatSection = "guestClanChat";

    @ConfigItem(keyName="joinLeaveTimeout", name="Join/leave timeout", description="Set the timeout duration of join/leave messages. A value of 0 will make the messages permanent.", position=0)
    default public int joinLeaveTimeout() {
        return 20;
    }

    @ConfigItem(keyName="clanChatIcons", name="Chat icons", description="Show rank icons next to friends chat members.", position=1, section="friendsChat")
    default public boolean chatIcons() {
        return true;
    }

    @ConfigItem(keyName="recentChats", name="Recent chats", description="Show recent friends chats.", position=2, section="friendsChat")
    default public boolean recentChats() {
        return true;
    }

    @ConfigItem(keyName="chatsData", name="", description="", hidden=true)
    default public String chatsData() {
        return "";
    }

    @ConfigItem(keyName="chatsData", name="", description="")
    public void chatsData(String var1);

    @ConfigItem(keyName="showJoinLeave", name="Show join/leave", description="Adds a temporary message notifying when a member joins or leaves.", position=4, section="friendsChat")
    default public boolean showFriendsChatJoinLeave() {
        return false;
    }

    @ConfigItem(keyName="joinLeaveRank", name="Join/leave rank", description="Only show join/leave messages for members at or above this rank.", position=5, section="friendsChat")
    default public FriendsChatRank joinLeaveRank() {
        return FriendsChatRank.UNRANKED;
    }

    @ConfigItem(keyName="privateMessageIcons", name="Private message icons", description="Add rank icons to private messages received from members.", position=7, section="friendsChat")
    default public boolean privateMessageIcons() {
        return false;
    }

    @ConfigItem(keyName="publicChatIcons", name="Public chat icons", description="Add rank icons to public chat messages from members.", position=8, section="friendsChat")
    default public boolean publicChatIcons() {
        return false;
    }

    @ConfigItem(keyName="confirmKicks", name="Confirm kicks", description="Shows a chat prompt to confirm kicks.", position=10, section="friendsChat")
    default public boolean confirmKicks() {
        return false;
    }

    @ConfigItem(keyName="showIgnores", name="Recolor ignored players", description="Recolor members who are on your ignore list.", position=11, section="friendsChat")
    default public boolean showIgnores() {
        return true;
    }

    @ConfigItem(keyName="showIgnoresColor", name="Ignored color", description="Allows you to change the color of the ignored players in your friends chat.", position=12, section="friendsChat")
    default public Color showIgnoresColor() {
        return Color.RED;
    }

    @ConfigItem(keyName="clanChatShowJoinLeave", name="Show join/leave", description="Adds a temporary message notifying when a member joins or leaves.", position=0, section="clanChat")
    default public boolean clanChatShowJoinLeave() {
        return false;
    }

    @ConfigItem(keyName="clanChatShowOnlineMemberCount", name="Show online member count", description="Shows the number of online clan members at the end of the clan's name.", position=1, section="clanChat")
    default public boolean clanChatShowOnlineMemberCount() {
        return false;
    }

    @ConfigItem(keyName="guestClanChatShowJoinLeave", name="Show join/leave", description="Adds a temporary message notifying when a member joins or leaves.", position=0, section="guestClanChat")
    default public boolean guestClanChatShowJoinLeave() {
        return false;
    }

    @ConfigItem(keyName="guestClanChatShowOnlineMemberCount", name="Show online member count", description="Shows the number of online guest clan members at the end of the clan's name.", position=1, section="guestClanChat")
    default public boolean guestClanChatShowOnlineMemberCount() {
        return false;
    }
}

