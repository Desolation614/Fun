/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nullable
 *  net.runelite.api.ChatMessageType
 */
package net.runelite.client.plugins.chathistory;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.runelite.api.ChatMessageType;

enum ChatboxTab {
    ALL("Switch tab", 10616836, ChatMessageType.values()),
    PRIVATE(null, 10616847, ChatMessageType.PRIVATECHAT, ChatMessageType.PRIVATECHATOUT, ChatMessageType.MODPRIVATECHAT, ChatMessageType.LOGINLOGOUTNOTIFICATION),
    PUBLIC(null, 10616843, ChatMessageType.PUBLICCHAT, ChatMessageType.AUTOTYPER, ChatMessageType.MODCHAT, ChatMessageType.MODAUTOTYPER),
    GAME("Filter", 10616839, ChatMessageType.GAMEMESSAGE, ChatMessageType.ENGINE, ChatMessageType.BROADCAST, ChatMessageType.SNAPSHOTFEEDBACK, ChatMessageType.ITEM_EXAMINE, ChatMessageType.NPC_EXAMINE, ChatMessageType.OBJECT_EXAMINE, ChatMessageType.FRIENDNOTIFICATION, ChatMessageType.IGNORENOTIFICATION, ChatMessageType.CONSOLE, ChatMessageType.SPAM, ChatMessageType.PLAYERRELATED, ChatMessageType.TENSECTIMEOUT, ChatMessageType.WELCOME, ChatMessageType.YELLCHAT, ChatMessageType.MODYELLCHAT, ChatMessageType.BROADCAST_FRIEND, ChatMessageType.BROADCAST_WORLD, ChatMessageType.BROADCAST_GLOBAL, ChatMessageType.UNKNOWN),
    CHANNEL(null, 10616851, ChatMessageType.FRIENDSCHATNOTIFICATION, ChatMessageType.FRIENDSCHAT, ChatMessageType.CHALREQ_FRIENDSCHAT),
    CLAN(null, 10616855, ChatMessageType.CLAN_CHAT, ChatMessageType.CLAN_MESSAGE, ChatMessageType.CLAN_GUEST_CHAT, ChatMessageType.CLAN_GUEST_MESSAGE),
    TRADE_GROUP("Trade:</col> Show none", 10616859, ChatMessageType.TRADE_SENT, ChatMessageType.TRADEREQ, ChatMessageType.TRADE, ChatMessageType.CHALREQ_TRADE, ChatMessageType.CLAN_GIM_CHAT, ChatMessageType.CLAN_GIM_MESSAGE);

    private static final Map<Integer, ChatboxTab> TAB_MESSAGE_TYPES;
    @Nullable
    private final String after;
    private final int widgetId;
    private final ChatMessageType[] messageTypes;

    private ChatboxTab(String after, int widgetId, ChatMessageType ... messageTypes) {
        this.after = after;
        this.widgetId = widgetId;
        this.messageTypes = messageTypes;
    }

    static ChatboxTab of(int widgetId) {
        return TAB_MESSAGE_TYPES.get(widgetId);
    }

    @Nullable
    public String getAfter() {
        return this.after;
    }

    public int getWidgetId() {
        return this.widgetId;
    }

    public ChatMessageType[] getMessageTypes() {
        return this.messageTypes;
    }

    static {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (ChatboxTab t : ChatboxTab.values()) {
            builder.put((Object)t.widgetId, (Object)t);
        }
        TAB_MESSAGE_TYPES = builder.build();
    }
}

