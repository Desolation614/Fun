/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.events.ChatMessage
 */
package net.runelite.client.chat;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.events.ChatInput;

class ChatCommand {
    private final String name;
    private boolean async;
    private final BiConsumer<ChatMessage, String> execute;
    private final BiPredicate<ChatInput, String> input;

    public ChatCommand(String name, boolean async, BiConsumer<ChatMessage, String> execute, BiPredicate<ChatInput, String> input) {
        this.name = name;
        this.async = async;
        this.execute = execute;
        this.input = input;
    }

    public String getName() {
        return this.name;
    }

    public boolean isAsync() {
        return this.async;
    }

    public BiConsumer<ChatMessage, String> getExecute() {
        return this.execute;
    }

    public BiPredicate<ChatInput, String> getInput() {
        return this.input;
    }
}

