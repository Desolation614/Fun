/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.EvictingQueue
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatLineBuffer
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.MessageNode
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.vars.InputType
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetUtil
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.chathistory;

import com.google.common.collect.EvictingQueue;
import com.google.inject.Provides;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import javax.inject.Inject;
import net.runelite.api.ChatLineBuffer;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.vars.InputType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.chathistory.ChatHistoryConfig;
import net.runelite.client.plugins.chathistory.ChatboxTab;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Chat History", description="Retain your chat history when logging in/out or world hopping", tags={"chat", "history", "retain", "cycle", "pm"})
public class ChatHistoryPlugin
extends Plugin
implements KeyListener {
    private static final Logger log = LoggerFactory.getLogger(ChatHistoryPlugin.class);
    private static final String WELCOME_MESSAGE = "Welcome to Ferox";
    private static final String CLEAR_HISTORY = "Clear history";
    private static final String COPY_TO_CLIPBOARD = "Copy to clipboard";
    private static final String REPORT = "Report";
    private static final int CYCLE_HOTKEY = 9;
    private static final int FRIENDS_MAX_SIZE = 5;
    private Queue<MessageNode> messageQueue;
    private Deque<String> friends;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ChatHistoryConfig config;
    @Inject
    private KeyManager keyManager;

    @Provides
    ChatHistoryConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ChatHistoryConfig.class);
    }

    @Override
    protected void startUp() {
        this.messageQueue = EvictingQueue.create((int)100);
        this.friends = new ArrayDeque<String>(6);
        this.keyManager.registerKeyListener(this);
    }

    @Override
    protected void shutDown() {
        this.messageQueue.clear();
        this.messageQueue = null;
        this.friends.clear();
        this.friends = null;
        this.keyManager.unregisterKeyListener(this);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        ChatMessageType chatMessageType = chatMessage.getType();
        if (chatMessageType == ChatMessageType.WELCOME && StringUtils.startsWithIgnoreCase((CharSequence)chatMessage.getMessage(), (CharSequence)WELCOME_MESSAGE)) {
            if (!this.config.retainChatHistory()) {
                return;
            }
            for (MessageNode queuedMessage : this.messageQueue) {
                MessageNode node = this.client.addChatMessage(queuedMessage.getType(), queuedMessage.getName(), queuedMessage.getValue(), queuedMessage.getSender(), false);
                node.setRuneLiteFormatMessage(queuedMessage.getRuneLiteFormatMessage());
                node.setTimestamp(queuedMessage.getTimestamp());
            }
            return;
        }
        switch (chatMessageType) {
            case PRIVATECHATOUT: 
            case PRIVATECHAT: 
            case MODPRIVATECHAT: {
                String name = Text.removeTags(chatMessage.getName());
                if (!this.friends.remove(name) && this.friends.size() >= 5) {
                    this.friends.remove();
                }
                this.friends.add(name);
            }
            case PUBLICCHAT: 
            case MODCHAT: 
            case FRIENDSCHAT: 
            case CLAN_GUEST_CHAT: 
            case CLAN_GUEST_MESSAGE: 
            case CLAN_CHAT: 
            case CLAN_MESSAGE: 
            case CLAN_GIM_CHAT: 
            case CLAN_GIM_MESSAGE: 
            case CONSOLE: {
                this.messageQueue.offer(chatMessage.getMessageNode());
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        String menuOption = event.getMenuOption();
        if (menuOption.endsWith(CLEAR_HISTORY)) {
            this.clearChatboxHistory(ChatboxTab.of(event.getParam1()));
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded entry) {
        if (entry.getType() != MenuAction.CC_OP.getId() && entry.getType() != MenuAction.CC_OP_LOW_PRIORITY.getId()) {
            return;
        }
        ChatboxTab tab = ChatboxTab.of(entry.getActionParam1());
        if (tab != null && tab.getAfter() != null && this.config.clearHistory() && entry.getOption().endsWith(tab.getAfter())) {
            String option;
            int idx;
            MenuEntry clearEntry = this.client.createMenuEntry(-2).setType(MenuAction.RUNELITE_HIGH_PRIORITY);
            clearEntry.setParam1(entry.getActionParam1());
            StringBuilder optionBuilder = new StringBuilder();
            if (tab != ChatboxTab.ALL && (idx = (option = entry.getOption()).indexOf(58)) != -1) {
                optionBuilder.append(option, 0, idx).append(":</col> ");
            }
            optionBuilder.append(CLEAR_HISTORY);
            clearEntry.setOption(optionBuilder.toString());
            return;
        }
        if (entry.getOption().equals(REPORT) && this.config.copyToClipboard()) {
            int groupId = WidgetUtil.componentToInterface((int)entry.getActionParam1());
            int childId = WidgetUtil.componentToId((int)entry.getActionParam1());
            if (groupId != 162) {
                return;
            }
            Widget widget = this.client.getWidget(groupId, childId);
            Widget parent = widget.getParent();
            if (10616889 != parent.getId()) {
                return;
            }
            int first = WidgetUtil.componentToId((int)10616890);
            int dynamicChildId = (childId - first) * 4 + 1;
            Widget messageContents = parent.getChild(dynamicChildId);
            if (messageContents == null) {
                return;
            }
            String currentMessage = messageContents.getText();
            this.client.createMenuEntry(1).setOption(COPY_TO_CLIPBOARD).setTarget(entry.getTarget()).setType(MenuAction.RUNELITE).onClick(e -> {
                StringSelection stringSelection = new StringSelection(Text.removeTags(currentMessage));
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            });
        }
    }

    private void clearMessageQueue(ChatboxTab tab) {
        if (tab == ChatboxTab.ALL || tab == ChatboxTab.PRIVATE) {
            this.friends.clear();
        }
        this.messageQueue.removeIf(e -> ArrayUtils.contains((Object[])tab.getMessageTypes(), (Object)e.getType()));
    }

    private void clearChatboxHistory(ChatboxTab tab) {
        if (tab == null) {
            return;
        }
        log.debug("Clearing chatbox history for tab {}", (Object)tab);
        this.clearMessageQueue(tab);
        if (tab.getAfter() == null) {
            return;
        }
        boolean removed = false;
        for (ChatMessageType msgType : tab.getMessageTypes()) {
            MessageNode[] lines;
            ChatLineBuffer lineBuffer = (ChatLineBuffer)this.client.getChatLineMap().get(msgType.getType());
            if (lineBuffer == null) continue;
            for (MessageNode line : lines = (MessageNode[])lineBuffer.getLines().clone()) {
                if (line == null) continue;
                lineBuffer.removeMessageNode(line);
                removed = true;
            }
        }
        if (removed) {
            this.clientThread.invoke(() -> this.client.runScript(new Object[]{83}));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != 9 || !this.config.pmTargetCycling()) {
            return;
        }
        if (this.client.getVarcIntValue(5) != InputType.PRIVATE_MESSAGE.getType()) {
            return;
        }
        this.clientThread.invoke(() -> {
            String target = this.findPreviousFriend();
            if (target == null) {
                return;
            }
            String currentMessage = this.client.getVarcStrValue(359);
            this.client.runScript(new Object[]{107, target});
            this.client.setVarcStrValue(359, currentMessage);
            this.client.runScript(new Object[]{222, ""});
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private String findPreviousFriend() {
        String currentTarget = this.client.getVarcStrValue(360);
        if (currentTarget == null || this.friends.isEmpty()) {
            return null;
        }
        Iterator<String> it = this.friends.descendingIterator();
        while (it.hasNext()) {
            String friend = it.next();
            if (!friend.equals(currentTarget)) continue;
            return it.hasNext() ? it.next() : this.friends.getLast();
        }
        return this.friends.getLast();
    }
}

