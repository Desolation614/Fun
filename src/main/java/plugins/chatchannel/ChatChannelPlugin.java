/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.Runnables
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatLineBuffer
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.ChatPlayer
 *  net.runelite.api.Client
 *  net.runelite.api.FriendsChatManager
 *  net.runelite.api.FriendsChatMember
 *  net.runelite.api.FriendsChatRank
 *  net.runelite.api.GameState
 *  net.runelite.api.MessageNode
 *  net.runelite.api.NameableContainer
 *  net.runelite.api.clan.ClanChannel
 *  net.runelite.api.clan.ClanChannelMember
 *  net.runelite.api.clan.ClanRank
 *  net.runelite.api.clan.ClanSettings
 *  net.runelite.api.clan.ClanTitle
 *  net.runelite.api.events.ClanMemberJoined
 *  net.runelite.api.events.ClanMemberLeft
 *  net.runelite.api.events.FriendsChatChanged
 *  net.runelite.api.events.FriendsChatMemberJoined
 *  net.runelite.api.events.FriendsChatMemberLeft
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ScriptCallbackEvent
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.events.VarClientStrChanged
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.chatchannel;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.inject.Inject;
import net.runelite.api.ChatLineBuffer;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ChatPlayer;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatManager;
import net.runelite.api.FriendsChatMember;
import net.runelite.api.FriendsChatRank;
import net.runelite.api.GameState;
import net.runelite.api.MessageNode;
import net.runelite.api.NameableContainer;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.clan.ClanRank;
import net.runelite.api.clan.ClanSettings;
import net.runelite.api.clan.ClanTitle;
import net.runelite.api.events.ClanMemberJoined;
import net.runelite.api.events.ClanMemberLeft;
import net.runelite.api.events.FriendsChatChanged;
import net.runelite.api.events.FriendsChatMemberJoined;
import net.runelite.api.events.FriendsChatMemberLeft;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarClientStrChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.config.ChatColorConfig;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.chatchannel.ActivityType;
import net.runelite.client.plugins.chatchannel.ChatChannelConfig;
import net.runelite.client.plugins.chatchannel.MemberActivity;
import net.runelite.client.plugins.chatchannel.MemberJoinMessage;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.util.Text;

@PluginDescriptor(name="Chat Channels", description="Improvements for friends chat and clan chat.", tags={"icons", "rank", "recent", "clan", "friend", "channel"})
public class ChatChannelPlugin
extends Plugin {
    private static final int MAX_CHATS = 10;
    private static final String RECENT_TITLE = "Recent FCs";
    @VisibleForTesting
    static final int MESSAGE_DELAY = 10;
    @Inject
    private Client client;
    @Inject
    private ChatIconManager chatIconManager;
    @Inject
    private ChatChannelConfig config;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ChatboxPanelManager chatboxPanelManager;
    @Inject
    private ChatColorConfig chatColorConfig;
    private List<String> chats;
    private final Deque<MemberJoinMessage> joinMessages = new ArrayDeque<MemberJoinMessage>();
    private final List<MemberActivity> activityBuffer = new LinkedList<MemberActivity>();
    private int joinedTick;
    private boolean kickConfirmed = false;

    @Provides
    ChatChannelConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ChatChannelConfig.class);
    }

    @Override
    public void startUp() {
        this.chats = new ArrayList<String>(Text.fromCSV(this.config.chatsData()));
        if (this.config.showIgnores()) {
            this.clientThread.invoke(() -> this.colorIgnoredPlayers(this.config.showIgnoresColor()));
        }
        this.rebuildClanTitle();
    }

    @Override
    public void shutDown() {
        this.chats = null;
        this.clientThread.invoke(() -> this.colorIgnoredPlayers(Color.WHITE));
        this.rebuildFriendsChat();
        this.rebuildClanTitle();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("clanchat")) {
            if (!this.config.recentChats()) {
                this.rebuildFriendsChat();
            }
            Color ignoreColor = this.config.showIgnores() ? this.config.showIgnoresColor() : Color.WHITE;
            this.clientThread.invoke(() -> this.colorIgnoredPlayers(ignoreColor));
            this.rebuildClanTitle();
        }
    }

    @Subscribe
    public void onFriendsChatMemberJoined(FriendsChatMemberJoined event) {
        FriendsChatMember member = event.getMember();
        if (this.joinedTick == this.client.getTickCount()) {
            return;
        }
        if (!this.config.showFriendsChatJoinLeave() || member.getRank().getValue() < this.config.joinLeaveRank().getValue()) {
            return;
        }
        this.queueJoin((ChatPlayer)member, MemberActivity.ChatType.FRIENDS_CHAT);
    }

    @Subscribe
    public void onFriendsChatMemberLeft(FriendsChatMemberLeft event) {
        FriendsChatMember member = event.getMember();
        if (!this.config.showFriendsChatJoinLeave() || member.getRank().getValue() < this.config.joinLeaveRank().getValue()) {
            return;
        }
        this.queueLeave((ChatPlayer)member, MemberActivity.ChatType.FRIENDS_CHAT);
    }

    @Subscribe
    public void onClanMemberJoined(ClanMemberJoined clanMemberJoined) {
        MemberActivity.ChatType chatType = this.clanChannelToChatType(clanMemberJoined.getClanChannel());
        if (chatType != null && this.clanChannelJoinLeaveEnabled(chatType)) {
            this.queueJoin((ChatPlayer)clanMemberJoined.getClanMember(), chatType);
        }
    }

    @Subscribe
    public void onClanMemberLeft(ClanMemberLeft clanMemberLeft) {
        MemberActivity.ChatType chatType = this.clanChannelToChatType(clanMemberLeft.getClanChannel());
        if (chatType != null && this.clanChannelJoinLeaveEnabled(chatType)) {
            this.queueLeave((ChatPlayer)clanMemberLeft.getClanMember(), chatType);
        }
    }

    private MemberActivity.ChatType clanChannelToChatType(ClanChannel clanChannel) {
        return clanChannel == this.client.getClanChannel() ? MemberActivity.ChatType.CLAN_CHAT : (clanChannel == this.client.getGuestClanChannel() ? MemberActivity.ChatType.GUEST_CHAT : null);
    }

    private boolean clanChannelJoinLeaveEnabled(MemberActivity.ChatType chatType) {
        switch (chatType) {
            case CLAN_CHAT: {
                return this.config.clanChatShowJoinLeave();
            }
            case GUEST_CHAT: {
                return this.config.guestClanChatShowJoinLeave();
            }
        }
        return false;
    }

    private void queueJoin(ChatPlayer member, MemberActivity.ChatType chatType) {
        ListIterator<MemberActivity> iter = this.activityBuffer.listIterator();
        while (iter.hasNext()) {
            MemberActivity activity = iter.next();
            if (activity.getChatType() != chatType || activity.getMember().compareTo((Object)member) != 0) continue;
            iter.remove();
            return;
        }
        MemberActivity activity = new MemberActivity(ActivityType.JOINED, chatType, member, this.client.getTickCount());
        this.activityBuffer.add(activity);
    }

    private void queueLeave(ChatPlayer member, MemberActivity.ChatType chatType) {
        ListIterator<MemberActivity> iter = this.activityBuffer.listIterator();
        while (iter.hasNext()) {
            MemberActivity activity = iter.next();
            if (activity.getChatType() != chatType || activity.getMember().compareTo((Object)member) != 0) continue;
            iter.remove();
            return;
        }
        MemberActivity activity = new MemberActivity(ActivityType.LEFT, chatType, member, this.client.getTickCount());
        this.activityBuffer.add(activity);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        Widget chatList = this.client.getWidget(458764);
        if (chatList != null) {
            Widget owner = this.client.getWidget(458754);
            FriendsChatManager friendsChatManager = this.client.getFriendsChatManager();
            if ((friendsChatManager == null || friendsChatManager.getCount() <= 0) && chatList.getChildren() == null && !Strings.isNullOrEmpty((String)owner.getText()) && this.config.recentChats()) {
                this.loadFriendsChats();
            }
        }
        this.timeoutMessages();
        this.addActivityMessages();
    }

    private void timeoutMessages() {
        if (this.joinMessages.isEmpty()) {
            return;
        }
        int joinLeaveTimeout = this.config.joinLeaveTimeout();
        if (joinLeaveTimeout == 0) {
            return;
        }
        boolean removed = false;
        Iterator<MemberJoinMessage> it = this.joinMessages.iterator();
        while (it.hasNext()) {
            ChatLineBuffer ccInfoBuffer;
            MemberJoinMessage joinMessage = it.next();
            MessageNode messageNode = joinMessage.getMessageNode();
            int createdTick = joinMessage.getTick();
            if (this.client.getTickCount() <= createdTick + joinLeaveTimeout) break;
            it.remove();
            if (joinMessage.getGetMessageId() != messageNode.getId() || (ccInfoBuffer = (ChatLineBuffer)this.client.getChatLineMap().get(messageNode.getType().getType())) == null) continue;
            ccInfoBuffer.removeMessageNode(messageNode);
            removed = true;
        }
        if (removed) {
            this.clientThread.invoke(() -> this.client.runScript(new Object[]{216}));
        }
    }

    @VisibleForTesting
    void addActivityMessages() {
        if (this.activityBuffer.isEmpty()) {
            return;
        }
        ListIterator<MemberActivity> iter = this.activityBuffer.listIterator();
        while (iter.hasNext()) {
            MemberActivity activity = iter.next();
            if (activity.getTick() >= this.client.getTickCount() - 10) {
                return;
            }
            iter.remove();
            switch (activity.getChatType()) {
                case FRIENDS_CHAT: {
                    this.addActivityMessage((FriendsChatMember)activity.getMember(), activity.getActivityType());
                    break;
                }
                case CLAN_CHAT: 
                case GUEST_CHAT: {
                    this.addClanActivityMessage((ClanChannelMember)activity.getMember(), activity.getActivityType(), activity.getChatType());
                }
            }
        }
    }

    private void addActivityMessage(FriendsChatMember member, ActivityType activityType) {
        Color channelColor;
        Color textColor;
        FriendsChatManager friendsChatManager = this.client.getFriendsChatManager();
        if (friendsChatManager == null) {
            return;
        }
        String activityMessage = activityType == ActivityType.JOINED ? " has joined." : " has left.";
        FriendsChatRank rank = member.getRank();
        int rankIcon = -1;
        if (this.client.isResized() && this.client.getVarbitValue(4608) == 1) {
            textColor = (Color)MoreObjects.firstNonNull((Object)this.chatColorConfig.transparentFriendsChatInfo(), (Object)JagexColors.CHAT_FC_TEXT_TRANSPARENT_BACKGROUND);
            channelColor = (Color)MoreObjects.firstNonNull((Object)this.chatColorConfig.transparentFriendsChatChannelName(), (Object)JagexColors.CHAT_FC_NAME_TRANSPARENT_BACKGROUND);
        } else {
            textColor = (Color)MoreObjects.firstNonNull((Object)this.chatColorConfig.opaqueFriendsChatInfo(), (Object)JagexColors.CHAT_FC_TEXT_OPAQUE_BACKGROUND);
            channelColor = (Color)MoreObjects.firstNonNull((Object)this.chatColorConfig.opaqueFriendsChatChannelName(), (Object)JagexColors.CHAT_FC_NAME_OPAQUE_BACKGROUND);
        }
        if (this.config.chatIcons() && rank != null && rank != FriendsChatRank.UNRANKED) {
            rankIcon = this.chatIconManager.getIconNumber(rank);
        }
        ChatMessageBuilder message = new ChatMessageBuilder().append("[").append(channelColor, friendsChatManager.getName());
        if (rankIcon > -1) {
            message.append(" ").img(rankIcon);
        }
        message.append("] ").append(textColor, member.getName() + activityMessage);
        String messageString = message.build();
        MessageNode line = this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", messageString, "");
        MemberJoinMessage joinMessage = new MemberJoinMessage(line, line.getId(), this.client.getTickCount());
        this.joinMessages.addLast(joinMessage);
    }

    private void addClanActivityMessage(ClanChannelMember member, ActivityType activityType, MemberActivity.ChatType chatType) {
        ClanSettings clanSettings = chatType == MemberActivity.ChatType.CLAN_CHAT ? this.client.getClanSettings() : this.client.getGuestClanSettings();
        ClanRank rank = member.getRank();
        if (rank == null || clanSettings == null) {
            return;
        }
        ClanTitle clanTitle = clanSettings.titleForRank(rank);
        int rankIcon = -1;
        if (clanTitle != null) {
            rankIcon = this.chatIconManager.getIconNumber(clanTitle);
        }
        Color textColor = this.client.isResized() && this.client.getVarbitValue(4608) == 1 ? (Color)MoreObjects.firstNonNull((Object)(chatType == MemberActivity.ChatType.CLAN_CHAT ? this.chatColorConfig.transparentClanChatInfo() : this.chatColorConfig.transparentClanChatGuestInfo()), (Object)JagexColors.CHAT_FC_TEXT_TRANSPARENT_BACKGROUND) : (Color)MoreObjects.firstNonNull((Object)(chatType == MemberActivity.ChatType.CLAN_CHAT ? this.chatColorConfig.opaqueClanChatInfo() : this.chatColorConfig.opaqueClanChatGuestInfo()), (Object)JagexColors.CHAT_FC_TEXT_OPAQUE_BACKGROUND);
        ChatMessageBuilder message = new ChatMessageBuilder();
        if (rankIcon > -1) {
            message.img(rankIcon);
        }
        message.append(textColor, member.getName() + (activityType == ActivityType.JOINED ? " has joined." : " has left."));
        String messageString = message.build();
        MessageNode line = this.client.addChatMessage(chatType == MemberActivity.ChatType.CLAN_CHAT ? ChatMessageType.CLAN_MESSAGE : ChatMessageType.CLAN_GUEST_MESSAGE, "", messageString, "");
        MemberJoinMessage joinMessage = new MemberJoinMessage(line, line.getId(), this.client.getTickCount());
        this.joinMessages.addLast(joinMessage);
    }

    @Subscribe
    public void onVarClientStrChanged(VarClientStrChanged strChanged) {
        if (strChanged.getIndex() == 362 && this.config.recentChats()) {
            this.updateRecentChat(this.client.getVarcStrValue(362));
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged state) {
        GameState gameState = state.getGameState();
        if (gameState == GameState.LOGIN_SCREEN || gameState == GameState.CONNECTION_LOST || gameState == GameState.HOPPING) {
            this.joinMessages.clear();
        }
    }

    @Subscribe
    public void onFriendsChatChanged(FriendsChatChanged event) {
        if (event.isJoined()) {
            this.joinedTick = this.client.getTickCount();
        }
        this.activityBuffer.clear();
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent) {
        switch (scriptCallbackEvent.getEventName()) {
            case "confirmFriendsChatKick": {
                if (!this.config.confirmKicks() || this.kickConfirmed) break;
                int[] intStack = this.client.getIntStack();
                int size = this.client.getIntStackSize();
                intStack[size - 1] = 1;
                Object[] objectStack = this.client.getObjectStack();
                int objectStackSize = this.client.getObjectStackSize();
                String kickPlayerName = (String)objectStack[objectStackSize - 1];
                this.clientThread.invokeLater(() -> this.confirmKickPlayer(kickPlayerName));
                break;
            }
            case "chatMessageBuilding": {
                int iconNumber;
                int objectStackSize;
                Object[] objectStack;
                String name;
                FriendsChatRank rank;
                int uid = this.client.getIntStack()[this.client.getIntStackSize() - 1];
                MessageNode messageNode = (MessageNode)this.client.getMessages().get((long)uid);
                assert (messageNode != null) : "chat message build for unknown message";
                ChatMessageType messageType = messageNode.getType();
                switch (messageType) {
                    case PRIVATECHAT: 
                    case MODPRIVATECHAT: {
                        if (this.config.privateMessageIcons()) break;
                        return;
                    }
                    case PUBLICCHAT: 
                    case MODCHAT: {
                        if (this.config.publicChatIcons()) break;
                        return;
                    }
                    case FRIENDSCHAT: {
                        if (this.config.chatIcons()) break;
                        return;
                    }
                    default: {
                        return;
                    }
                }
                if ((rank = this.getRank(Text.removeTags(name = (String)(objectStack = this.client.getObjectStack())[(objectStackSize = this.client.getObjectStackSize()) - 3]))) == null || rank == FriendsChatRank.UNRANKED || (iconNumber = this.chatIconManager.getIconNumber(rank)) <= -1) break;
                String img = "<img=" + iconNumber + ">";
                objectStack[objectStackSize - 3] = img + name;
            }
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() == 1658) {
            if (this.config.showIgnores()) {
                this.colorIgnoredPlayers(this.config.showIgnoresColor());
            }
            FriendsChatManager friendsChatManager = this.client.getFriendsChatManager();
            Widget chatTitle = this.client.getWidget(458753);
            if (friendsChatManager != null && friendsChatManager.getCount() > 0 && chatTitle != null) {
                chatTitle.setText(chatTitle.getText() + " (" + friendsChatManager.getCount() + "/" + friendsChatManager.getSize() + ")");
            }
        } else if (event.getScriptId() == 4396) {
            if (this.config.clanChatShowOnlineMemberCount()) {
                this.updateClanTitle(45940737, this.client.getClanChannel());
            }
            if (this.config.guestClanChatShowOnlineMemberCount()) {
                this.updateClanTitle(46006273, this.client.getGuestClanChannel());
            }
        }
    }

    private FriendsChatRank getRank(String playerName) {
        FriendsChatManager friendsChatManager = this.client.getFriendsChatManager();
        if (friendsChatManager == null) {
            return FriendsChatRank.UNRANKED;
        }
        FriendsChatMember friendsChatMember = (FriendsChatMember)friendsChatManager.findByName(playerName);
        return friendsChatMember != null ? friendsChatMember.getRank() : FriendsChatRank.UNRANKED;
    }

    private void rebuildFriendsChat() {
        Widget chat = this.client.getWidget(458752);
        if (chat == null) {
            return;
        }
        Object[] args = chat.getOnVarTransmitListener();
        this.clientThread.invokeLater(() -> this.client.runScript(args));
    }

    private void loadFriendsChats() {
        Widget chatOwner = this.client.getWidget(458754);
        Widget chatList = this.client.getWidget(458764);
        if (chatList == null || chatOwner == null) {
            return;
        }
        chatOwner.setText(RECENT_TITLE);
        int y = 2;
        chatList.setChildren(null);
        for (String chat : Lists.reverse(this.chats)) {
            Widget widget = chatList.createChild(-1, 4);
            widget.setFontId(494);
            widget.setTextColor(0xFFFFFF);
            widget.setText(chat);
            widget.setOriginalHeight(14);
            widget.setOriginalWidth(142);
            widget.setOriginalY(y);
            widget.setOriginalX(20);
            widget.revalidate();
            y += 14;
        }
    }

    private void updateRecentChat(String s) {
        if (Strings.isNullOrEmpty((String)s)) {
            return;
        }
        s = Text.toJagexName(s);
        this.chats.removeIf(s::equalsIgnoreCase);
        this.chats.add(s);
        while (this.chats.size() > 10) {
            this.chats.remove(0);
        }
        this.config.chatsData(Text.toCSV(this.chats));
    }

    private void confirmKickPlayer(String kickPlayerName) {
        this.chatboxPanelManager.openTextMenuInput("Attempting to kick: " + kickPlayerName).option("1. Confirm kick", () -> this.clientThread.invoke(() -> {
            this.kickConfirmed = true;
            this.client.runScript(new Object[]{3764, kickPlayerName});
            this.kickConfirmed = false;
        })).option("2. Cancel", Runnables.doNothing()).build();
    }

    private void colorIgnoredPlayers(Color ignoreColor) {
        Widget chatList = this.client.getWidget(458764);
        if (chatList == null || chatList.getChildren() == null) {
            return;
        }
        NameableContainer ignoreContainer = this.client.getIgnoreContainer();
        for (int i = 0; i < chatList.getChildren().length; i += 3) {
            Widget listWidget = chatList.getChild(i);
            String memberName = listWidget.getText();
            if (memberName.isEmpty() || ignoreContainer.findByName(memberName) == null) continue;
            listWidget.setTextColor(ignoreColor.getRGB());
        }
    }

    private void rebuildClanTitle() {
        this.clientThread.invokeLater(() -> {
            Widget w = this.client.getWidget(45940736);
            if (w != null) {
                this.client.runScript(w.getOnVarTransmitListener());
            }
        });
        this.clientThread.invokeLater(() -> {
            Widget w = this.client.getWidget(46006272);
            if (w != null) {
                this.client.runScript(w.getOnVarTransmitListener());
            }
        });
    }

    private void updateClanTitle(int widget, ClanChannel channel) {
        Widget header = this.client.getWidget(widget);
        if (header != null && channel != null) {
            Widget title = header.getChild(0);
            title.setText(title.getText() + " (" + channel.getMembers().size() + ")");
        }
    }
}

