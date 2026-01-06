/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Splitter
 *  com.google.common.base.Strings
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.MessageNode
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameStateChanged
 */
package net.runelite.client.plugins.chatnotifications;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.Notifier;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.chatnotifications.ChatNotificationsConfig;
import net.runelite.client.util.Text;

@PluginDescriptor(name="Chat Notifications", description="Highlight and notify you of chat messages", tags={"duel", "messages", "notifications", "trade", "username"}, enabledByDefault=false)
public class ChatNotificationsPlugin
extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ChatNotificationsConfig config;
    @Inject
    private Notifier notifier;
    @Inject
    @Named(value="runelite.title")
    private String runeliteTitle;
    private Pattern usernameMatcher = null;
    private final List<Pattern> highlightPatterns = new ArrayList<Pattern>();

    @Provides
    ChatNotificationsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ChatNotificationsConfig.class);
    }

    @Override
    public void startUp() {
        this.updateHighlights();
    }

    @Override
    protected void shutDown() {
        this.usernameMatcher = null;
        this.highlightPatterns.clear();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case LOGIN_SCREEN: 
            case HOPPING: {
                this.usernameMatcher = null;
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("chatnotification")) {
            this.updateHighlights();
        }
    }

    private void updateHighlights() {
        this.highlightPatterns.clear();
        if (!this.config.highlightWordsString().trim().equals("")) {
            List<String> items = Text.fromCSV(this.config.highlightWordsString());
            String joined = items.stream().map(Text::escapeJagex).map(this::quoteAndIgnoreColor).collect(Collectors.joining("|"));
            this.highlightPatterns.add(Pattern.compile("(?:\\b|(?<=\\s)|\\A)(?:" + joined + ")(?:\\b|(?=\\s)|\\z)", 2));
        }
        Splitter.on((String)"\n").omitEmptyStrings().trimResults().splitToList((CharSequence)this.config.highlightRegexString()).stream().map(ChatNotificationsPlugin::compilePattern).filter(Objects::nonNull).forEach(this.highlightPatterns::add);
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
    public void onChatMessage(ChatMessage chatMessage) {
        String message;
        Matcher matcher;
        MessageNode messageNode = chatMessage.getMessageNode();
        boolean update = false;
        switch (chatMessage.getType()) {
            case TRADEREQ: {
                if (!chatMessage.getMessage().contains("wishes to trade with you.")) break;
                this.notifier.notify(this.config.notifyOnTrade(), chatMessage.getMessage());
                break;
            }
            case CHALREQ_TRADE: {
                if (!chatMessage.getMessage().contains("wishes to duel with you.") && !chatMessage.getMessage().contains("wishes to gamble with you.")) break;
                this.notifier.notify(this.config.notifyOnDuel(), chatMessage.getMessage());
                break;
            }
            case BROADCAST: {
                String broadcast = chatMessage.getMessage();
                int urlTokenIndex = broadcast.lastIndexOf(124);
                if (urlTokenIndex != -1) {
                    broadcast = broadcast.substring(0, urlTokenIndex);
                }
                this.notifier.notify(this.config.notifyOnBroadcast(), Text.removeFormattingTags(broadcast));
                break;
            }
            case PRIVATECHAT: 
            case MODPRIVATECHAT: {
                this.notifier.notify(this.config.notifyOnPM(), Text.removeTags(chatMessage.getName()) + ": " + chatMessage.getMessage());
                break;
            }
            case PRIVATECHATOUT: 
            case DIALOG: 
            case MESBOX: {
                return;
            }
            case MODCHAT: 
            case PUBLICCHAT: 
            case FRIENDSCHAT: 
            case CLAN_CHAT: 
            case CLAN_GUEST_CHAT: 
            case CLAN_GIM_CHAT: 
            case AUTOTYPER: 
            case MODAUTOTYPER: {
                if (this.client.getLocalPlayer() == null || !Text.toJagexName(Text.removeTags(chatMessage.getName())).equals(this.client.getLocalPlayer().getName())) break;
                return;
            }
            case CONSOLE: {
                if (!chatMessage.getName().equals(this.runeliteTitle)) break;
                return;
            }
        }
        if (this.usernameMatcher == null && this.client.getLocalPlayer() != null && this.client.getLocalPlayer().getName() != null) {
            String username = this.client.getLocalPlayer().getName();
            String pattern = Arrays.stream(username.split(" ")).map(s -> s.isEmpty() ? "" : Pattern.quote(s)).collect(Collectors.joining("[\u00a0 ]"));
            this.usernameMatcher = Pattern.compile("\\b" + pattern + "\\b", 2);
        }
        if (this.config.highlightOwnName() && this.usernameMatcher != null && (matcher = this.usernameMatcher.matcher(message = messageNode.getValue())).find()) {
            String username = this.client.getLocalPlayer().getName();
            StringBuffer stringBuffer = new StringBuffer();
            do {
                int start = matcher.start();
                String closeColor = (String)MoreObjects.firstNonNull((Object)ChatNotificationsPlugin.getLastColor(message.substring(0, start)), (Object)("<col" + String.valueOf((Object)ChatColorType.NORMAL) + ">"));
                String replacement = "<col" + ChatColorType.HIGHLIGHT.name() + "><u>" + username + "</u>" + closeColor;
                matcher.appendReplacement(stringBuffer, replacement);
            } while (matcher.find());
            matcher.appendTail(stringBuffer);
            messageNode.setValue(stringBuffer.toString());
            update = true;
            if (chatMessage.getType() == ChatMessageType.PUBLICCHAT || chatMessage.getType() == ChatMessageType.PRIVATECHAT || chatMessage.getType() == ChatMessageType.FRIENDSCHAT || chatMessage.getType() == ChatMessageType.MODCHAT || chatMessage.getType() == ChatMessageType.MODPRIVATECHAT || chatMessage.getType() == ChatMessageType.CLAN_CHAT || chatMessage.getType() == ChatMessageType.CLAN_GUEST_CHAT) {
                this.sendNotification(this.config.notifyOnOwnName(), chatMessage);
            }
        }
        boolean matchesHighlight = false;
        String nodeValue = messageNode.getValue();
        for (Pattern pattern : this.highlightPatterns) {
            Matcher matcher2 = pattern.matcher(nodeValue);
            if (!matcher2.find()) continue;
            StringBuffer stringBuffer = new StringBuffer();
            do {
                int end = matcher2.end();
                String closeColor = (String)MoreObjects.firstNonNull((Object)ChatNotificationsPlugin.getLastColor(nodeValue.substring(0, end)), (Object)("<col" + String.valueOf((Object)ChatColorType.NORMAL) + ">"));
                String value = ChatNotificationsPlugin.stripColor(matcher2.group());
                matcher2.appendReplacement(stringBuffer, "<col" + String.valueOf((Object)ChatColorType.HIGHLIGHT) + ">" + value + closeColor);
                update = true;
                matchesHighlight = true;
            } while (matcher2.find());
            matcher2.appendTail(stringBuffer);
            nodeValue = stringBuffer.toString();
        }
        if (matchesHighlight) {
            messageNode.setValue(nodeValue);
            this.sendNotification(this.config.notifyOnHighlight(), chatMessage);
        }
        if (update) {
            messageNode.setRuneLiteFormatMessage(messageNode.getValue());
        }
    }

    private void sendNotification(Notification notification, ChatMessage message) {
        String name = Text.removeTags(message.getName());
        String sender = message.getSender();
        StringBuilder stringBuilder = new StringBuilder();
        if (!Strings.isNullOrEmpty((String)sender)) {
            stringBuilder.append('[').append(sender).append("] ");
        }
        if (!Strings.isNullOrEmpty((String)name)) {
            stringBuilder.append(name).append(": ");
        }
        stringBuilder.append(Text.removeTags(message.getMessage()));
        String m = stringBuilder.toString();
        this.notifier.notify(notification, m);
    }

    private String quoteAndIgnoreColor(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            stringBuilder.append(Pattern.quote(String.valueOf(c)));
            stringBuilder.append("(?:<col=[^>]*?>)?");
        }
        return stringBuilder.toString();
    }

    private static String getLastColor(String str) {
        int colIdx = str.lastIndexOf("<col=");
        int colEndIdx = str.lastIndexOf("</col>");
        if (colEndIdx > colIdx) {
            return "<col" + String.valueOf((Object)ChatColorType.NORMAL) + ">";
        }
        if (colIdx == -1) {
            return null;
        }
        int closeIdx = str.indexOf(62, colIdx);
        if (closeIdx == -1) {
            return null;
        }
        return str.substring(colIdx, closeIdx + 1);
    }

    @VisibleForTesting
    static String stripColor(String str) {
        return str.replaceAll("(<col=[0-9a-f]+>|</col>)", "");
    }
}

