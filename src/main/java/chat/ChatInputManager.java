/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.events.CommandExecuted
 *  net.runelite.api.events.ScriptCallbackEvent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.chat;

import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ChatboxInput;
import net.runelite.client.events.PrivateMessageInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class ChatInputManager {
    private static final Logger log = LoggerFactory.getLogger(ChatInputManager.class);
    private static final String RUNELITE_COMMAND = "runeliteCommand";
    private static final String CHATBOX_INPUT = "chatboxInput";
    private static final String PRIVATE_MESSAGE = "privateMessage";
    private final Client client;
    private final EventBus eventBus;
    private final ClientThread clientThread;
    private boolean sending;

    @Inject
    private ChatInputManager(Client client, EventBus eventBus, ClientThread clientThread) {
        this.client = client;
        this.eventBus = eventBus;
        this.clientThread = clientThread;
        eventBus.register(this);
    }

    @Subscribe
    private void onScriptCallbackEvent(ScriptCallbackEvent event) {
        if (this.sending) {
            return;
        }
        switch (event.getEventName()) {
            case "runeliteCommand": {
                this.runCommand();
                break;
            }
            case "chatboxInput": {
                this.handleInput(event);
                break;
            }
            case "privateMessage": {
                this.handlePrivateMessage(event);
            }
        }
    }

    private void runCommand() {
        String typedText = this.client.getVarcStrValue(335).substring(2);
        log.debug("Command: {}", (Object)typedText);
        String[] split = typedText.split(" ");
        if (split.length == 0) {
            return;
        }
        String command = split[0];
        String[] args = Arrays.copyOfRange(split, 1, split.length);
        CommandExecuted commandExecuted = new CommandExecuted(command, args);
        this.eventBus.post(commandExecuted);
    }

    private void handleInput(ScriptCallbackEvent event) {
        Object[] objectStack = this.client.getObjectStack();
        int[] intStack = this.client.getIntStack();
        int objectStackCount = this.client.getObjectStackSize();
        int intStackCount = this.client.getIntStackSize();
        String typedText = (String)objectStack[objectStackCount - 1];
        int chatType = intStack[intStackCount - 2];
        int clanTarget = intStack[intStackCount - 1];
        ChatboxInput chatboxInput = new ChatboxInput(typedText, chatType, () -> this.clientThread.invokeLater(() -> this.sendChatboxInput(typedText, chatType, clanTarget)));
        this.eventBus.post(chatboxInput);
        if (chatboxInput.isConsumed()) {
            objectStack[objectStackCount - 1] = "";
        }
    }

    private void handlePrivateMessage(ScriptCallbackEvent event) {
        Object[] objectStack = this.client.getObjectStack();
        int[] intStack = this.client.getIntStack();
        int objectStackCount = this.client.getObjectStackSize();
        int intStackCount = this.client.getIntStackSize();
        String target = (String)objectStack[objectStackCount - 2];
        String message = (String)objectStack[objectStackCount - 1];
        PrivateMessageInput privateMessageInput = new PrivateMessageInput(target, message, () -> this.clientThread.invokeLater(() -> this.sendPrivmsg(target, message)));
        this.eventBus.post(privateMessageInput);
        if (privateMessageInput.isConsumed()) {
            intStack[intStackCount - 1] = 1;
            this.client.setObjectStackSize(objectStackCount - 2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendChatboxInput(String input, int chatType, int clanTarget) {
        this.sending = true;
        try {
            this.client.runScript(new Object[]{5517, input, chatType, clanTarget, 0, -1});
        }
        finally {
            this.sending = false;
        }
    }

    private void sendPrivmsg(String target, String message) {
        this.client.runScript(new Object[]{15004, target, message});
    }
}

