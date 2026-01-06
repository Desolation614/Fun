/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.CharMatcher
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.FriendsChatManager
 *  net.runelite.api.MessageNode
 *  net.runelite.api.Player
 *  net.runelite.api.clan.ClanChannel
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.OverheadTextChanged
 *  net.runelite.api.events.ScriptCallbackEvent
 *  org.apache.commons.lang3.StringUtils
 */
package net.runelite.client.plugins.chatfilter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatManager;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.chatfilter.ChatFilterConfig;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.StringUtils;

@PluginDescriptor(name="Chat Filter", description="Censor user configurable words or patterns from chat", enabledByDefault=false)
public class ChatFilterPlugin
extends Plugin {
    private static final Splitter NEWLINE_SPLITTER = Splitter.on((String)"\n").omitEmptyStrings().trimResults();
    @VisibleForTesting
    static final String CENSOR_MESSAGE = "Hey, everyone, I just tried to say something very silly!";
    private static final Set<ChatMessageType> COLLAPSIBLE_MESSAGETYPES = ImmutableSet.of((Object)ChatMessageType.ENGINE, (Object)ChatMessageType.GAMEMESSAGE, (Object)ChatMessageType.ITEM_EXAMINE, (Object)ChatMessageType.NPC_EXAMINE, (Object)ChatMessageType.OBJECT_EXAMINE, (Object)ChatMessageType.SPAM, (Object[])new ChatMessageType[]{ChatMessageType.PUBLICCHAT, ChatMessageType.MODCHAT, ChatMessageType.NPC_SAY});
    private static final CharMatcher jagexPrintableCharMatcher = Text.JAGEX_PRINTABLE_CHAR_MATCHER;
    private List<Pattern> filteredPatterns = Collections.emptyList();
    private List<Pattern> filteredNamePatterns = Collections.emptyList();
    private final LinkedHashMap<String, Duplicate> duplicateChatCache = new LinkedHashMap<String, Duplicate>(){
        private static final int MAX_ENTRIES = 100;

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Duplicate> eldest) {
            return this.size() > 100;
        }
    };
    private final Map<ChatMessageType, FilterCacheMap> filterCache = new HashMap<ChatMessageType, FilterCacheMap>();
    @Inject
    private Client client;
    @Inject
    private ChatFilterConfig config;

    @Provides
    ChatFilterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ChatFilterConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.updateFilteredPatterns();
        this.client.refreshChat();
    }

    @Override
    protected void shutDown() throws Exception {
        this.filteredPatterns = Collections.emptyList();
        this.filteredNamePatterns = Collections.emptyList();
        this.duplicateChatCache.clear();
        this.filterCache.clear();
        this.client.refreshChat();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        switch (gameStateChanged.getGameState()) {
            case CONNECTION_LOST: 
            case HOPPING: 
            case LOGGING_IN: {
                this.duplicateChatCache.values().forEach(d -> {
                    d.messageId = -1;
                });
                this.filterCache.clear();
            }
        }
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event) {
        Duplicate duplicateCacheEntry;
        boolean shouldCollapse;
        if (!"chatFilterCheck".equals(event.getEventName())) {
            return;
        }
        int[] intStack = this.client.getIntStack();
        int intStackSize = this.client.getIntStackSize();
        Object[] objectStack = this.client.getObjectStack();
        int objectStackSize = this.client.getObjectStackSize();
        int messageType = intStack[intStackSize - 2];
        int messageId = intStack[intStackSize - 1];
        Object message = (String)objectStack[objectStackSize - 1];
        ChatMessageType chatMessageType = ChatMessageType.of((int)messageType);
        MessageNode messageNode = (MessageNode)this.client.getMessages().get((long)messageId);
        String name = messageNode.getName();
        int duplicateCount = 0;
        boolean blockMessage = false;
        switch (chatMessageType) {
            case PUBLICCHAT: 
            case MODCHAT: 
            case AUTOTYPER: 
            case PRIVATECHAT: 
            case MODPRIVATECHAT: 
            case FRIENDSCHAT: 
            case CLAN_CHAT: 
            case CLAN_GUEST_CHAT: 
            case CLAN_GIM_CHAT: {
                if (!this.canFilterPlayer(Text.removeTags(name))) break;
                blockMessage = (message = this.censorMessage(messageNode, name, (String)message)) == null;
                break;
            }
            case GAMEMESSAGE: 
            case ENGINE: 
            case FRIENDSCHATNOTIFICATION: 
            case ITEM_EXAMINE: 
            case NPC_EXAMINE: 
            case OBJECT_EXAMINE: 
            case SPAM: 
            case CLAN_MESSAGE: 
            case CLAN_GUEST_MESSAGE: 
            case CLAN_GIM_MESSAGE: 
            case NPC_SAY: {
                if (!this.config.filterGameChat()) break;
                boolean bl = blockMessage = (message = this.censorMessage(messageNode, null, (String)message)) == null;
            }
        }
        boolean bl = chatMessageType == ChatMessageType.PUBLICCHAT || chatMessageType == ChatMessageType.MODCHAT ? this.config.collapsePlayerChat() : (shouldCollapse = COLLAPSIBLE_MESSAGETYPES.contains(chatMessageType) && this.config.collapseGameChat());
        if (!blockMessage && shouldCollapse && (duplicateCacheEntry = this.duplicateChatCache.get(name + ":" + (String)message)) != null && duplicateCacheEntry.messageId != -1) {
            blockMessage = duplicateCacheEntry.messageId != messageId || (chatMessageType == ChatMessageType.PUBLICCHAT || chatMessageType == ChatMessageType.MODCHAT) && this.config.maxRepeatedPublicChats() > 0 && duplicateCacheEntry.count > this.config.maxRepeatedPublicChats();
            duplicateCount = duplicateCacheEntry.count;
        }
        if (blockMessage) {
            intStack[intStackSize - 3] = 0;
        } else {
            if (duplicateCount > 1) {
                message = (String)message + " (" + duplicateCount + ")";
            }
            objectStack[objectStackSize - 1] = message;
        }
    }

    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged event) {
        if (!(event.getActor() instanceof Player) || event.getActor().getName() == null || !this.canFilterPlayer(event.getActor().getName())) {
            return;
        }
        String message = this.censorMessage(event.getActor().getName(), event.getOverheadText());
        if (message == null) {
            message = " ";
        }
        event.getActor().setOverheadText(message);
    }

    @Subscribe(priority=-2.0f)
    public void onChatMessage(ChatMessage chatMessage) {
        if (COLLAPSIBLE_MESSAGETYPES.contains(chatMessage.getType())) {
            MessageNode messageNode = chatMessage.getMessageNode();
            String key = messageNode.getName() + ":" + messageNode.getValue();
            Duplicate duplicate = (Duplicate)this.duplicateChatCache.remove(key);
            if (duplicate == null) {
                duplicate = new Duplicate();
            }
            ++duplicate.count;
            duplicate.messageId = messageNode.getId();
            this.duplicateChatCache.put(key, duplicate);
        }
    }

    boolean canFilterPlayer(String playerName) {
        boolean isMessageFromSelf = playerName.equals(this.client.getLocalPlayer().getName());
        return !(isMessageFromSelf || !this.config.filterFriends() && this.client.isFriended(playerName, false) || !this.config.filterFriendsChat() && this.isFriendsChatMember(playerName) || !this.config.filterClanChat() && this.isClanChatMember(playerName));
    }

    private boolean isFriendsChatMember(String name) {
        FriendsChatManager friendsChatManager = this.client.getFriendsChatManager();
        return friendsChatManager != null && friendsChatManager.findByName(name) != null;
    }

    private boolean isClanChatMember(String name) {
        ClanChannel clanChannel = this.client.getClanChannel();
        if (clanChannel != null && clanChannel.findMember(name) != null) {
            return true;
        }
        clanChannel = this.client.getGuestClanChannel();
        return clanChannel != null && clanChannel.findMember(name) != null;
    }

    String censorMessage(String username, String message) {
        String strippedMessage = jagexPrintableCharMatcher.retainFrom((CharSequence)message).replace('\u00a0', ' ').replace("<lt>", "<").replace("<gt>", ">");
        String strippedAccents = this.stripAccents(strippedMessage);
        assert (strippedMessage.length() == strippedAccents.length());
        if (username != null && this.isNameFiltered(username)) {
            switch (this.config.filterType()) {
                case CENSOR_WORDS: {
                    return StringUtils.repeat((char)'*', (int)strippedMessage.length());
                }
                case CENSOR_MESSAGE: {
                    return CENSOR_MESSAGE;
                }
                case REMOVE_MESSAGE: {
                    return null;
                }
            }
        }
        boolean filtered = false;
        for (Pattern pattern : this.filteredPatterns) {
            Matcher m = pattern.matcher(strippedAccents);
            StringBuilder sb = new StringBuilder();
            int idx = 0;
            while (m.find()) {
                switch (this.config.filterType()) {
                    case CENSOR_WORDS: {
                        MatchResult matchResult = m.toMatchResult();
                        sb.append(strippedMessage, idx, matchResult.start()).append(StringUtils.repeat((char)'*', (int)matchResult.group().length()));
                        idx = m.end();
                        filtered = true;
                        break;
                    }
                    case CENSOR_MESSAGE: {
                        return CENSOR_MESSAGE;
                    }
                    case REMOVE_MESSAGE: {
                        return null;
                    }
                }
            }
            sb.append(strippedMessage.substring(idx));
            strippedMessage = sb.toString();
            assert (strippedMessage.length() == strippedAccents.length());
        }
        return filtered ? strippedMessage : message;
    }

    private String censorMessage(MessageNode messageNode, String username, String message) {
        FilterCacheMap map = this.filterCache.get(messageNode.getType());
        if (map == null) {
            map = new FilterCacheMap();
            this.filterCache.put(messageNode.getType(), map);
        }
        if (map.containsKey(messageNode.getId())) {
            return (String)map.get(messageNode.getId());
        }
        String censoredMessage = this.censorMessage(username, message);
        map.put(messageNode.getId(), censoredMessage);
        return censoredMessage;
    }

    void updateFilteredPatterns() {
        ArrayList<Pattern> patterns = new ArrayList<Pattern>();
        ArrayList<Pattern> namePatterns = new ArrayList<Pattern>();
        Text.fromCSV(this.config.filteredWords()).stream().map(this::stripAccents).map(s -> Pattern.compile(Pattern.quote(s), 2)).forEach(patterns::add);
        NEWLINE_SPLITTER.splitToList((CharSequence)this.config.filteredRegex()).stream().map(this::stripAccents).map(ChatFilterPlugin::compilePattern).filter(Objects::nonNull).forEach(patterns::add);
        NEWLINE_SPLITTER.splitToList((CharSequence)this.config.filteredNames()).stream().map(this::stripAccents).map(ChatFilterPlugin::compilePattern).filter(Objects::nonNull).forEach(namePatterns::add);
        this.filteredPatterns = patterns;
        this.filteredNamePatterns = namePatterns;
        this.filterCache.clear();
    }

    private String stripAccents(String input) {
        return this.config.stripAccents() ? StringUtils.stripAccents((String)input) : input;
    }

    private static Pattern compilePattern(String pattern) {
        try {
            return Pattern.compile(pattern, 2);
        }
        catch (PatternSyntaxException ex) {
            return null;
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!"chatfilter".equals(event.getGroup())) {
            return;
        }
        this.updateFilteredPatterns();
        this.client.refreshChat();
    }

    @VisibleForTesting
    boolean isNameFiltered(String playerName) {
        String sanitizedName = Text.standardize(playerName);
        for (Pattern pattern : this.filteredNamePatterns) {
            Matcher m = pattern.matcher(sanitizedName);
            if (!m.find()) continue;
            return true;
        }
        return false;
    }

    private static class FilterCacheMap
    extends LinkedHashMap<Integer, String> {
        private static final int MAX_ENTRIES = 100;

        private FilterCacheMap() {
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
            return this.size() > 100;
        }
    }

    private static class Duplicate {
        int messageId;
        int count;

        private Duplicate() {
        }
    }
}

