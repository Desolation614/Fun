/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.ChatMessageType
 */
package net.runelite.client.plugins.crowdsourcing.dialogue;

import net.runelite.api.ChatMessageType;

public class ChatMessageData {
    private final String message;
    private final ChatMessageType type;

    public String getMessage() {
        return this.message;
    }

    public ChatMessageType getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChatMessageData)) {
            return false;
        }
        ChatMessageData other = (ChatMessageData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        ChatMessageType this$type = this.getType();
        ChatMessageType other$type = other.getType();
        return !(this$type == null ? other$type != null : !this$type.equals(other$type));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ChatMessageData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        ChatMessageType $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        return result;
    }

    public String toString() {
        return "ChatMessageData(message=" + this.getMessage() + ", type=" + String.valueOf(this.getType()) + ")";
    }

    public ChatMessageData(String message, ChatMessageType type) {
        this.message = message;
        this.type = type;
    }
}

