/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.events.ChatMessage
 */
package net.runelite.client.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.chat.ChatCommand;
import net.runelite.client.chat.ChatInputManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ChatInput;
import net.runelite.client.events.ChatboxInput;
import net.runelite.client.events.PrivateMessageInput;

@Singleton
public class ChatCommandManager {
    private final Map<String, ChatCommand> commands = new ConcurrentHashMap<String, ChatCommand>();
    private final ScheduledExecutorService scheduledExecutorService;

    @Inject
    private ChatCommandManager(EventBus eventBus, ChatInputManager chatInputManager, ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        eventBus.register(this);
    }

    public void registerCommand(String command, BiConsumer<ChatMessage, String> execute) {
        this.registerCommand(command, execute, null);
    }

    public void registerCommand(String command, BiConsumer<ChatMessage, String> execute, BiPredicate<ChatInput, String> input) {
        this.commands.put(command.toLowerCase(), new ChatCommand(command, false, execute, input));
    }

    public void registerCommandAsync(String command, BiConsumer<ChatMessage, String> execute) {
        this.registerCommandAsync(command, execute, null);
    }

    public void registerCommandAsync(String command, BiConsumer<ChatMessage, String> execute, BiPredicate<ChatInput, String> input) {
        this.commands.put(command.toLowerCase(), new ChatCommand(command, true, execute, input));
    }

    public void unregisterCommand(String command) {
        this.commands.remove(command.toLowerCase());
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        switch (chatMessage.getType()) {
            case PUBLICCHAT: 
            case MODCHAT: 
            case FRIENDSCHAT: 
            case PRIVATECHAT: 
            case MODPRIVATECHAT: 
            case PRIVATECHATOUT: 
            case CLAN_CHAT: 
            case CLAN_GUEST_CHAT: 
            case CLAN_GIM_CHAT: {
                break;
            }
            default: {
                return;
            }
        }
        String message = chatMessage.getMessage();
        String command = ChatCommandManager.extractCommand(message);
        ChatCommand chatCommand = this.commands.get(command.toLowerCase());
        if (chatCommand == null) {
            return;
        }
        if (chatCommand.isAsync()) {
            this.scheduledExecutorService.execute(() -> chatCommand.getExecute().accept(chatMessage, message));
        } else {
            chatCommand.getExecute().accept(chatMessage, message);
        }
    }

    @Subscribe
    public void onChatboxInput(ChatboxInput chatboxInput) {
        String message = chatboxInput.getValue();
        String command = ChatCommandManager.extractCommand(message);
        ChatCommand chatCommand = this.commands.get(command.toLowerCase());
        if (chatCommand == null) {
            return;
        }
        BiPredicate<ChatInput, String> input = chatCommand.getInput();
        if (input == null) {
            return;
        }
        if (input.test(chatboxInput, message)) {
            chatboxInput.consume();
        }
    }

    @Subscribe
    public void onPrivateMessageInput(PrivateMessageInput privateMessageInput) {
        String message = privateMessageInput.getMessage();
        String command = ChatCommandManager.extractCommand(message);
        ChatCommand chatCommand = this.commands.get(command.toLowerCase());
        if (chatCommand == null) {
            return;
        }
        BiPredicate<ChatInput, String> input = chatCommand.getInput();
        if (input == null) {
            return;
        }
        if (input.test(privateMessageInput, message)) {
            privateMessageInput.consume();
        }
    }

    private static String extractCommand(String message) {
        int idx = message.indexOf(32);
        if (idx == -1) {
            return message;
        }
        return message.substring(0, idx);
    }
}

