/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.chat;

import java.awt.Color;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

public class ChatMessageBuilder {
    private final StringBuilder builder = new StringBuilder();

    public ChatMessageBuilder append(ChatColorType type) {
        this.builder.append("<col").append(type.name()).append('>');
        return this;
    }

    public ChatMessageBuilder append(Color color, String message) {
        this.builder.append(ColorUtil.wrapWithColorTag(message, color));
        return this;
    }

    public ChatMessageBuilder append(String message) {
        this.builder.append(Text.escapeJagex(message));
        return this;
    }

    public ChatMessageBuilder img(int imageId) {
        this.builder.append("<img=").append(imageId).append('>');
        return this;
    }

    public String build() {
        return this.builder.toString();
    }
}

