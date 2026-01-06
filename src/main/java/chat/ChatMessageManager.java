/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Strings
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Multimap
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.MessageNode
 *  net.runelite.api.Player
 *  net.runelite.api.events.ScriptCallbackEvent
 */
package net.runelite.client.chat;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.awt.Color;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColor;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ChatColorConfig;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

@Singleton
public class ChatMessageManager {
    private static final Set<Integer> TUTORIAL_ISLAND_REGIONS = ImmutableSet.of((Object)12336, (Object)12335, (Object)12592, (Object)12080, (Object)12079, (Object)12436, (Object[])new Integer[0]);
    private final Multimap<ChatMessageType, ChatColor> colorCache = HashMultimap.create();
    private final Client client;
    private final ChatColorConfig chatColorConfig;
    private final ClientThread clientThread;
    private final Queue<QueuedMessage> queuedMessages = new ConcurrentLinkedQueue<QueuedMessage>();

    @Inject
    private ChatMessageManager(Client client, ChatColorConfig chatColorConfig, ClientThread clientThread, EventBus eventBus) {
        this.client = client;
        this.chatColorConfig = chatColorConfig;
        this.clientThread = clientThread;
        eventBus.register(this);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("textrecolor")) {
            this.loadColors();
            this.clientThread.invokeLater(() -> ((Client)this.client).refreshChat());
        }
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        this.loadColors();
        this.clientThread.invokeLater(() -> ((Client)this.client).refreshChat());
    }

    @VisibleForTesting
    void colorChatMessage() {
        int[] intStack = this.client.getIntStack();
        Object[] objectStack = this.client.getObjectStack();
        int isize = this.client.getIntStackSize();
        int osize = this.client.getObjectStackSize();
        int uid = intStack[isize - 1];
        boolean splitpmbox = intStack[isize - 2] == 1;
        MessageNode messageNode = (MessageNode)this.client.getMessages().get((long)uid);
        assert (messageNode != null) : "chat message build for unknown message";
        String message = (String)objectStack[osize - 2];
        String username = (String)objectStack[osize - 3];
        String channel = (String)objectStack[osize - 4];
        ChatMessageType chatMessageType = messageNode.getType();
        boolean isChatboxTransparent = this.client.isResized() && this.client.getVarbitValue(4608) == 1;
        Color usernameColor = null;
        Color channelColor = null;
        switch (chatMessageType) {
            case TRADEREQ: 
            case AUTOTYPER: 
            case PUBLICCHAT: 
            case MODCHAT: {
                String sanitizedUsername = Text.removeTags(username).replace('\u00a0', ' ');
                if (this.client.getLocalPlayer().getName().equals(sanitizedUsername)) {
                    usernameColor = isChatboxTransparent ? this.chatColorConfig.transparentPlayerUsername() : this.chatColorConfig.opaquePlayerUsername();
                    break;
                }
                if (this.client.isFriended(sanitizedUsername, true)) {
                    usernameColor = isChatboxTransparent ? this.chatColorConfig.transparentPublicFriendUsernames() : this.chatColorConfig.opaquePublicFriendUsernames();
                    break;
                }
                usernameColor = isChatboxTransparent ? this.chatColorConfig.transparentUsername() : this.chatColorConfig.opaqueUsername();
                break;
            }
            case FRIENDSCHAT: 
            case FRIENDSCHATNOTIFICATION: {
                usernameColor = isChatboxTransparent ? this.chatColorConfig.transparentFriendsChatUsernames() : this.chatColorConfig.opaqueFriendsChatUsernames();
                channelColor = isChatboxTransparent ? this.chatColorConfig.transparentFriendsChatChannelName() : this.chatColorConfig.opaqueFriendsChatChannelName();
                break;
            }
            case CLAN_CHAT: 
            case CLAN_MESSAGE: 
            case CLAN_GIM_CHAT: 
            case CLAN_GIM_MESSAGE: {
                usernameColor = isChatboxTransparent ? this.chatColorConfig.transparentClanChatUsernames() : this.chatColorConfig.opaqueClanChatUsernames();
                channelColor = isChatboxTransparent ? this.chatColorConfig.transparentClanChannelName() : this.chatColorConfig.opaqueClanChannelName();
                break;
            }
            case CLAN_GUEST_CHAT: 
            case CLAN_GUEST_MESSAGE: {
                usernameColor = isChatboxTransparent ? this.chatColorConfig.transparentClanChatGuestUsernames() : this.chatColorConfig.opaqueClanChatGuestUsernames();
                Color color = channelColor = isChatboxTransparent ? this.chatColorConfig.transparentClanChannelGuestName() : this.chatColorConfig.opaqueClanGuestChatChannelName();
            }
        }
        if (usernameColor != null) {
            objectStack[osize - 3] = ColorUtil.wrapWithColorTag(username, usernameColor);
        }
        if (channelColor != null && !Strings.isNullOrEmpty((String)channel)) {
            objectStack[osize - 4] = ColorUtil.wrapWithColorTag(channel, channelColor);
        }
        String prefix = "";
        if (chatMessageType == ChatMessageType.CLAN_GIM_CHAT || chatMessageType == ChatMessageType.CLAN_GIM_MESSAGE) {
            message = message.substring(1);
            prefix = "|";
        }
        if (messageNode.getRuneLiteFormatMessage() != null) {
            message = this.formatRuneLiteMessage(messageNode.getRuneLiteFormatMessage(), chatMessageType, splitpmbox);
        }
        Collection chatColors = this.colorCache.get((Object)chatMessageType);
        for (ChatColor chatColor : chatColors) {
            if (chatColor.isTransparent() != isChatboxTransparent || chatColor.getType() != ChatColorType.NORMAL || chatColor.isDefault()) continue;
            Color color = chatColor.getColor();
            message = ColorUtil.wrapWithColorTag(message.replace("</col>", ColorUtil.colorTag(color)).replaceAll("<br>", "<br>" + ColorUtil.colorTag(color)), color);
            break;
        }
        objectStack[osize - 2] = prefix + message;
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent) {
        Color usernameColor;
        boolean wrap;
        String eventName;
        switch (eventName = scriptCallbackEvent.getEventName()) {
            case "splitPrivChatUsernameColor": {
                wrap = false;
                break;
            }
            case "privChatUsername": {
                wrap = true;
                break;
            }
            case "chatMessageBuilding": {
                this.colorChatMessage();
                return;
            }
            default: {
                return;
            }
        }
        boolean isChatboxTransparent = this.client.isResized() && this.client.getVarbitValue(4608) == 1;
        Color color = usernameColor = isChatboxTransparent ? this.chatColorConfig.transparentPrivateUsernames() : this.chatColorConfig.opaquePrivateUsernames();
        if (usernameColor == null) {
            return;
        }
        Object[] objectStack = this.client.getObjectStack();
        int objectStackSize = this.client.getObjectStackSize();
        String fromToUsername = (String)objectStack[objectStackSize - 1];
        fromToUsername = wrap ? ColorUtil.wrapWithColorTag(fromToUsername, usernameColor) : ColorUtil.colorTag(usernameColor);
        objectStack[objectStackSize - 1] = fromToUsername;
    }

    private static Color getDefaultColor(ChatMessageType type, boolean transparent) {
        if (!transparent) {
            switch (type) {
                case PUBLICCHAT: 
                case MODCHAT: {
                    return JagexColors.CHAT_PUBLIC_TEXT_OPAQUE_BACKGROUND;
                }
                case PRIVATECHATOUT: 
                case MODPRIVATECHAT: 
                case PRIVATECHAT: {
                    return JagexColors.CHAT_PRIVATE_MESSAGE_TEXT_OPAQUE_BACKGROUND;
                }
                case FRIENDSCHAT: 
                case CLAN_CHAT: 
                case CLAN_GIM_CHAT: 
                case CLAN_GUEST_CHAT: {
                    return JagexColors.CHAT_FC_TEXT_OPAQUE_BACKGROUND;
                }
                case FRIENDSCHATNOTIFICATION: 
                case CLAN_MESSAGE: 
                case CLAN_GIM_MESSAGE: 
                case CLAN_GUEST_MESSAGE: 
                case ITEM_EXAMINE: 
                case OBJECT_EXAMINE: 
                case NPC_EXAMINE: 
                case CONSOLE: {
                    return JagexColors.CHAT_GAME_EXAMINE_TEXT_OPAQUE_BACKGROUND;
                }
            }
        } else {
            switch (type) {
                case PUBLICCHAT: 
                case MODCHAT: {
                    return JagexColors.CHAT_PUBLIC_TEXT_TRANSPARENT_BACKGROUND;
                }
                case PRIVATECHATOUT: 
                case MODPRIVATECHAT: 
                case PRIVATECHAT: {
                    return JagexColors.CHAT_PRIVATE_MESSAGE_TEXT_TRANSPARENT_BACKGROUND;
                }
                case FRIENDSCHAT: 
                case CLAN_CHAT: 
                case CLAN_GIM_CHAT: 
                case CLAN_GUEST_CHAT: {
                    return JagexColors.CHAT_FC_TEXT_TRANSPARENT_BACKGROUND;
                }
                case FRIENDSCHATNOTIFICATION: 
                case CLAN_MESSAGE: 
                case CLAN_GIM_MESSAGE: 
                case CLAN_GUEST_MESSAGE: 
                case ITEM_EXAMINE: 
                case OBJECT_EXAMINE: 
                case NPC_EXAMINE: 
                case CONSOLE: {
                    return JagexColors.CHAT_GAME_EXAMINE_TEXT_TRANSPARENT_BACKGROUND;
                }
            }
        }
        return null;
    }

    private static int getSettingsColor(ChatMessageType type, boolean transparent) {
        if (transparent) {
            switch (type) {
                case PUBLICCHAT: 
                case MODCHAT: {
                    return 3000;
                }
                case PRIVATECHATOUT: 
                case MODPRIVATECHAT: 
                case PRIVATECHAT: 
                case LOGINLOGOUTNOTIFICATION: {
                    return 3001;
                }
                case AUTOTYPER: 
                case MODAUTOTYPER: {
                    return 3002;
                }
                case BROADCAST: {
                    return 3003;
                }
                case FRIENDSCHAT: {
                    return 3004;
                }
                case CLAN_CHAT: {
                    return 3005;
                }
                case TRADEREQ: {
                    return 3006;
                }
                case CHALREQ_TRADE: 
                case CHALREQ_FRIENDSCHAT: 
                case CHALREQ_CLANCHAT: {
                    return 3007;
                }
                case CLAN_GUEST_CHAT: {
                    return 3061;
                }
                case CLAN_GIM_CHAT: {
                    return 3194;
                }
                case CLAN_MESSAGE: {
                    return 3195;
                }
                case CLAN_GIM_MESSAGE: {
                    return 3196;
                }
            }
        } else {
            switch (type) {
                case PUBLICCHAT: 
                case MODCHAT: {
                    return 2992;
                }
                case PRIVATECHATOUT: 
                case MODPRIVATECHAT: 
                case PRIVATECHAT: 
                case LOGINLOGOUTNOTIFICATION: {
                    return 2993;
                }
                case AUTOTYPER: 
                case MODAUTOTYPER: {
                    return 2994;
                }
                case BROADCAST: {
                    return 2995;
                }
                case FRIENDSCHAT: {
                    return 2996;
                }
                case CLAN_CHAT: {
                    return 2997;
                }
                case TRADEREQ: {
                    return 2998;
                }
                case CHALREQ_TRADE: 
                case CHALREQ_FRIENDSCHAT: 
                case CHALREQ_CLANCHAT: {
                    return 2999;
                }
                case CLAN_GUEST_CHAT: {
                    return 3060;
                }
                case CLAN_GIM_CHAT: {
                    return 3191;
                }
                case CLAN_MESSAGE: {
                    return 3192;
                }
                case CLAN_GIM_MESSAGE: {
                    return 3193;
                }
            }
        }
        return -1;
    }

    private void loadColors() {
        this.colorCache.clear();
        for (ChatMessageType chatMessageType : ChatMessageType.values()) {
            Color defaultOpaque;
            Color defaultTransparent = ChatMessageManager.getDefaultColor(chatMessageType, true);
            if (defaultTransparent != null) {
                this.cacheColor(new ChatColor(ChatColorType.NORMAL, defaultTransparent, true, true, ChatMessageManager.getSettingsColor(chatMessageType, true)), chatMessageType);
            }
            if ((defaultOpaque = ChatMessageManager.getDefaultColor(chatMessageType, false)) == null) continue;
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, defaultOpaque, false, true, ChatMessageManager.getSettingsColor(chatMessageType, false)), chatMessageType);
        }
        if (this.chatColorConfig.opaquePublicChat() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaquePublicChat(), false), ChatMessageType.PUBLICCHAT);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaquePublicChat(), false), ChatMessageType.MODCHAT);
        }
        if (this.chatColorConfig.opaquePublicChatHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaquePublicChatHighlight(), false), ChatMessageType.PUBLICCHAT);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaquePublicChatHighlight(), false), ChatMessageType.MODCHAT);
        }
        if (this.chatColorConfig.opaquePrivateMessageSent() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaquePrivateMessageSent(), false), ChatMessageType.PRIVATECHATOUT);
        }
        if (this.chatColorConfig.opaquePrivateMessageSentHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaquePrivateMessageSentHighlight(), false), ChatMessageType.PRIVATECHATOUT);
        }
        if (this.chatColorConfig.opaquePrivateMessageReceived() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaquePrivateMessageReceived(), false), ChatMessageType.PRIVATECHAT);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaquePrivateMessageReceived(), false), ChatMessageType.MODPRIVATECHAT);
        }
        if (this.chatColorConfig.opaquePrivateMessageReceivedHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaquePrivateMessageReceivedHighlight(), false), ChatMessageType.PRIVATECHAT);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaquePrivateMessageReceivedHighlight(), false), ChatMessageType.MODPRIVATECHAT);
        }
        if (this.chatColorConfig.opaqueFriendsChatInfo() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueFriendsChatInfo(), false), ChatMessageType.FRIENDSCHATNOTIFICATION);
        }
        if (this.chatColorConfig.opaqueFriendsChatInfoHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueFriendsChatInfoHighlight(), false), ChatMessageType.FRIENDSCHATNOTIFICATION);
        }
        if (this.chatColorConfig.opaqueFriendsChatMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueFriendsChatMessage(), false), ChatMessageType.FRIENDSCHAT);
        }
        if (this.chatColorConfig.opaqueFriendsChatMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueFriendsChatMessageHighlight(), false), ChatMessageType.FRIENDSCHAT);
        }
        if (this.chatColorConfig.opaqueClanChatInfo() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueClanChatInfo(), false), ChatMessageType.CLAN_MESSAGE);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueClanChatInfo(), false), ChatMessageType.CLAN_GIM_MESSAGE);
        }
        if (this.chatColorConfig.opaqueClanChatInfoHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueClanChatInfoHighlight(), false), ChatMessageType.CLAN_MESSAGE);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueClanChatInfoHighlight(), false), ChatMessageType.CLAN_GIM_MESSAGE);
        }
        if (this.chatColorConfig.opaqueClanChatMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueClanChatMessage(), false), ChatMessageType.CLAN_CHAT);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueClanChatMessage(), false), ChatMessageType.CLAN_GIM_CHAT);
        }
        if (this.chatColorConfig.opaqueClanChatMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueClanChatMessageHighlight(), false), ChatMessageType.CLAN_CHAT);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueClanChatMessageHighlight(), false), ChatMessageType.CLAN_GIM_CHAT);
        }
        if (this.chatColorConfig.opaqueClanChatGuestInfo() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueClanChatGuestInfo(), false), ChatMessageType.CLAN_GUEST_MESSAGE);
        }
        if (this.chatColorConfig.opaqueClanChatGuestInfoHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueClanChatGuestInfoHighlight(), false), ChatMessageType.CLAN_GUEST_MESSAGE);
        }
        if (this.chatColorConfig.opaqueClanChatGuestMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueClanChatGuestMessage(), false), ChatMessageType.CLAN_GUEST_CHAT);
        }
        if (this.chatColorConfig.opaqueClanChatGuestMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueClanChatGuestMessageHighlight(), false), ChatMessageType.CLAN_GUEST_CHAT);
        }
        if (this.chatColorConfig.opaqueAutochatMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueAutochatMessage(), false), ChatMessageType.AUTOTYPER);
        }
        if (this.chatColorConfig.opaqueAutochatMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueAutochatMessageHighlight(), false), ChatMessageType.AUTOTYPER);
        }
        if (this.chatColorConfig.opaqueTradeChatMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueTradeChatMessage(), false), ChatMessageType.TRADEREQ);
        }
        if (this.chatColorConfig.opaqueTradeChatMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueTradeChatMessageHighlight(), false), ChatMessageType.TRADEREQ);
        }
        if (this.chatColorConfig.opaqueServerMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueServerMessage(), false), ChatMessageType.GAMEMESSAGE);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueServerMessage(), false), ChatMessageType.ENGINE);
        }
        if (this.chatColorConfig.opaqueServerMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueServerMessageHighlight(), false), ChatMessageType.GAMEMESSAGE);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueServerMessageHighlight(), false), ChatMessageType.ENGINE);
        }
        if (this.chatColorConfig.opaqueGameMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueGameMessage(), false), ChatMessageType.CONSOLE);
        }
        if (this.chatColorConfig.opaqueGameMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueGameMessageHighlight(), false), ChatMessageType.CONSOLE);
        }
        if (this.chatColorConfig.opaqueExamine() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueExamine(), false), ChatMessageType.OBJECT_EXAMINE);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueExamine(), false), ChatMessageType.NPC_EXAMINE);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueExamine(), false), ChatMessageType.ITEM_EXAMINE);
        }
        if (this.chatColorConfig.opaqueExamineHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueExamineHighlight(), false), ChatMessageType.OBJECT_EXAMINE);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueExamineHighlight(), false), ChatMessageType.NPC_EXAMINE);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueExamineHighlight(), false), ChatMessageType.ITEM_EXAMINE);
        }
        if (this.chatColorConfig.opaqueFiltered() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaqueFiltered(), false), ChatMessageType.SPAM);
        }
        if (this.chatColorConfig.opaqueFilteredHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.opaqueFilteredHighlight(), false), ChatMessageType.SPAM);
        }
        if (this.chatColorConfig.opaquePrivateUsernames() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.opaquePrivateUsernames(), false), ChatMessageType.LOGINLOGOUTNOTIFICATION);
        }
        if (this.chatColorConfig.transparentPublicChat() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentPublicChat(), true), ChatMessageType.PUBLICCHAT);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentPublicChat(), true), ChatMessageType.MODCHAT);
        }
        if (this.chatColorConfig.transparentPublicChatHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentPublicChatHighlight(), true), ChatMessageType.PUBLICCHAT);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentPublicChatHighlight(), true), ChatMessageType.MODCHAT);
        }
        if (this.chatColorConfig.transparentPrivateMessageSent() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentPrivateMessageSent(), true), ChatMessageType.PRIVATECHATOUT);
        }
        if (this.chatColorConfig.transparentPrivateMessageSentHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentPrivateMessageSentHighlight(), true), ChatMessageType.PRIVATECHATOUT);
        }
        if (this.chatColorConfig.transparentPrivateMessageReceived() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentPrivateMessageReceived(), true), ChatMessageType.PRIVATECHAT);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentPrivateMessageReceived(), true), ChatMessageType.MODPRIVATECHAT);
        }
        if (this.chatColorConfig.transparentPrivateMessageReceivedHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentPrivateMessageReceivedHighlight(), true), ChatMessageType.PRIVATECHAT);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentPrivateMessageReceivedHighlight(), true), ChatMessageType.MODPRIVATECHAT);
        }
        if (this.chatColorConfig.transparentFriendsChatInfo() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentFriendsChatInfo(), true), ChatMessageType.FRIENDSCHATNOTIFICATION);
        }
        if (this.chatColorConfig.transparentFriendsChatInfoHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentFriendsChatInfoHighlight(), true), ChatMessageType.FRIENDSCHATNOTIFICATION);
        }
        if (this.chatColorConfig.transparentFriendsChatMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentFriendsChatMessage(), true), ChatMessageType.FRIENDSCHAT);
        }
        if (this.chatColorConfig.transparentFriendsChatMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentFriendsChatMessageHighlight(), true), ChatMessageType.FRIENDSCHAT);
        }
        if (this.chatColorConfig.transparentClanChatInfo() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentClanChatInfo(), true), ChatMessageType.CLAN_MESSAGE);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentClanChatInfo(), true), ChatMessageType.CLAN_GIM_MESSAGE);
        }
        if (this.chatColorConfig.transparentClanChatInfoHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentClanChatInfoHighlight(), true), ChatMessageType.CLAN_MESSAGE);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentClanChatInfoHighlight(), true), ChatMessageType.CLAN_GIM_MESSAGE);
        }
        if (this.chatColorConfig.transparentClanChatMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentClanChatMessage(), true), ChatMessageType.CLAN_CHAT);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentClanChatMessage(), true), ChatMessageType.CLAN_GIM_CHAT);
        }
        if (this.chatColorConfig.transparentClanChatMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentClanChatMessageHighlight(), true), ChatMessageType.CLAN_CHAT);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentClanChatMessageHighlight(), true), ChatMessageType.CLAN_GIM_CHAT);
        }
        if (this.chatColorConfig.transparentClanChatGuestInfo() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentClanChatGuestInfo(), true), ChatMessageType.CLAN_GUEST_MESSAGE);
        }
        if (this.chatColorConfig.transparentClanChatGuestInfoHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentClanChatGuestInfoHighlight(), true), ChatMessageType.CLAN_GUEST_MESSAGE);
        }
        if (this.chatColorConfig.transparentClanChatGuestMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentClanChatGuestMessage(), true), ChatMessageType.CLAN_GUEST_CHAT);
        }
        if (this.chatColorConfig.transparentClanChatGuestMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentClanChatGuestMessageHighlight(), true), ChatMessageType.CLAN_GUEST_CHAT);
        }
        if (this.chatColorConfig.transparentAutochatMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentAutochatMessage(), true), ChatMessageType.AUTOTYPER);
        }
        if (this.chatColorConfig.transparentAutochatMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentAutochatMessageHighlight(), true), ChatMessageType.AUTOTYPER);
        }
        if (this.chatColorConfig.transparentTradeChatMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentTradeChatMessage(), true), ChatMessageType.TRADEREQ);
        }
        if (this.chatColorConfig.transparentTradeChatMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentTradeChatMessageHighlight(), true), ChatMessageType.TRADEREQ);
        }
        if (this.chatColorConfig.transparentServerMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentServerMessage(), true), ChatMessageType.GAMEMESSAGE);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentServerMessage(), true), ChatMessageType.ENGINE);
        }
        if (this.chatColorConfig.transparentServerMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentServerMessageHighlight(), true), ChatMessageType.GAMEMESSAGE);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentServerMessageHighlight(), true), ChatMessageType.ENGINE);
        }
        if (this.chatColorConfig.transparentGameMessage() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentGameMessage(), true), ChatMessageType.CONSOLE);
        }
        if (this.chatColorConfig.transparentGameMessageHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentGameMessageHighlight(), true), ChatMessageType.CONSOLE);
        }
        if (this.chatColorConfig.transparentExamine() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentExamine(), true), ChatMessageType.OBJECT_EXAMINE);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentExamine(), true), ChatMessageType.NPC_EXAMINE);
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentExamine(), true), ChatMessageType.ITEM_EXAMINE);
        }
        if (this.chatColorConfig.transparentExamineHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentExamineHighlight(), true), ChatMessageType.OBJECT_EXAMINE);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentExamineHighlight(), true), ChatMessageType.NPC_EXAMINE);
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentExamineHighlight(), true), ChatMessageType.ITEM_EXAMINE);
        }
        if (this.chatColorConfig.transparentFiltered() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentFiltered(), true), ChatMessageType.SPAM);
        }
        if (this.chatColorConfig.transparentFilteredHighlight() != null) {
            this.cacheColor(new ChatColor(ChatColorType.HIGHLIGHT, this.chatColorConfig.transparentFilteredHighlight(), true), ChatMessageType.SPAM);
        }
        if (this.chatColorConfig.transparentPrivateUsernames() != null) {
            this.cacheColor(new ChatColor(ChatColorType.NORMAL, this.chatColorConfig.transparentPrivateUsernames(), true), ChatMessageType.LOGINLOGOUTNOTIFICATION);
        }
    }

    private void cacheColor(ChatColor chatColor, ChatMessageType ... types) {
        for (ChatMessageType chatMessageType : types) {
            this.colorCache.remove((Object)chatMessageType, (Object)chatColor);
            this.colorCache.put((Object)chatMessageType, (Object)chatColor);
        }
    }

    public void queue(QueuedMessage message) {
        this.queuedMessages.add(message);
    }

    public void process() {
        QueuedMessage msg;
        while ((msg = this.queuedMessages.poll()) != null) {
            this.add(msg);
        }
    }

    private void add(QueuedMessage message) {
        Player player = this.client.getLocalPlayer();
        if (player != null && TUTORIAL_ISLAND_REGIONS.contains(player.getWorldLocation().getRegionID())) {
            return;
        }
        MessageNode line = this.client.addChatMessage(message.getType(), (String)MoreObjects.firstNonNull((Object)message.getName(), (Object)""), (String)MoreObjects.firstNonNull((Object)message.getRuneLiteFormattedMessage(), (Object)message.getValue()), message.getSender());
        line.setRuneLiteFormatMessage(message.getRuneLiteFormattedMessage());
        if (message.getTimestamp() != 0) {
            line.setTimestamp(message.getTimestamp());
        }
    }

    @Deprecated
    public void update(MessageNode messageNode) {
    }

    @VisibleForTesting
    String formatRuneLiteMessage(String runeLiteFormatMessage, ChatMessageType type, boolean pmbox) {
        boolean transparentChatbox = this.client.getVarbitValue(4608) != 0;
        boolean transparent = this.client.isResized() && transparentChatbox;
        Collection chatColors = this.colorCache.get((Object)type);
        for (ChatColor chatColor : chatColors) {
            String colstr;
            if (chatColor.isTransparent() != transparent) continue;
            if (pmbox && chatColor.getType() == ChatColorType.NORMAL) {
                colstr = "</col>";
            } else {
                Color color = chatColor.getColor();
                int varp = chatColor.getSetting();
                if (varp != -1) {
                    assert (chatColor.isDefault());
                    int v = this.client.getVarpValue(varp);
                    if (v != 0) {
                        color = new Color(v - 1);
                    }
                }
                colstr = ColorUtil.colorTag(color);
            }
            runeLiteFormatMessage = runeLiteFormatMessage.replace("<col" + chatColor.getType().name() + ">", colstr);
        }
        return runeLiteFormatMessage;
    }
}

