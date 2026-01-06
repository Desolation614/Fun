/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  net.runelite.api.ChatMessageType
 */
package net.runelite.client.chat;

import lombok.NonNull;
import net.runelite.api.ChatMessageType;

public class QueuedMessage {
    @NonNull
    private final ChatMessageType type;
    private final String value;
    private String name;
    private String sender;
    private String runeLiteFormattedMessage;
    private int timestamp;

    QueuedMessage(@NonNull ChatMessageType type, String value, String name, String sender, String runeLiteFormattedMessage, int timestamp) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        this.type = type;
        this.value = value;
        this.name = name;
        this.sender = sender;
        this.runeLiteFormattedMessage = runeLiteFormattedMessage;
        this.timestamp = timestamp;
    }

    public static QueuedMessageBuilder builder() {
        return new QueuedMessageBuilder();
    }

    @NonNull
    public ChatMessageType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public String getSender() {
        return this.sender;
    }

    public String getRuneLiteFormattedMessage() {
        return this.runeLiteFormattedMessage;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRuneLiteFormattedMessage(String runeLiteFormattedMessage) {
        this.runeLiteFormattedMessage = runeLiteFormattedMessage;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueuedMessage)) {
            return false;
        }
        QueuedMessage other = (QueuedMessage)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getTimestamp() != other.getTimestamp()) {
            return false;
        }
        ChatMessageType this$type = this.getType();
        ChatMessageType other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        String this$value = this.getValue();
        String other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$sender = this.getSender();
        String other$sender = other.getSender();
        if (this$sender == null ? other$sender != null : !this$sender.equals(other$sender)) {
            return false;
        }
        String this$runeLiteFormattedMessage = this.getRuneLiteFormattedMessage();
        String other$runeLiteFormattedMessage = other.getRuneLiteFormattedMessage();
        return !(this$runeLiteFormattedMessage == null ? other$runeLiteFormattedMessage != null : !this$runeLiteFormattedMessage.equals(other$runeLiteFormattedMessage));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueuedMessage;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getTimestamp();
        ChatMessageType $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        String $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : $value.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $sender = this.getSender();
        result = result * 59 + ($sender == null ? 43 : $sender.hashCode());
        String $runeLiteFormattedMessage = this.getRuneLiteFormattedMessage();
        result = result * 59 + ($runeLiteFormattedMessage == null ? 43 : $runeLiteFormattedMessage.hashCode());
        return result;
    }

    public String toString() {
        return "QueuedMessage(type=" + String.valueOf(this.getType()) + ", value=" + this.getValue() + ", name=" + this.getName() + ", sender=" + this.getSender() + ", runeLiteFormattedMessage=" + this.getRuneLiteFormattedMessage() + ", timestamp=" + this.getTimestamp() + ")";
    }

    public static class QueuedMessageBuilder {
        private ChatMessageType type;
        private String value;
        private String name;
        private String sender;
        private String runeLiteFormattedMessage;
        private int timestamp;

        QueuedMessageBuilder() {
        }

        public QueuedMessageBuilder type(@NonNull ChatMessageType type) {
            if (type == null) {
                throw new NullPointerException("type is marked non-null but is null");
            }
            this.type = type;
            return this;
        }

        public QueuedMessageBuilder value(String value) {
            this.value = value;
            return this;
        }

        public QueuedMessageBuilder name(String name) {
            this.name = name;
            return this;
        }

        public QueuedMessageBuilder sender(String sender) {
            this.sender = sender;
            return this;
        }

        public QueuedMessageBuilder runeLiteFormattedMessage(String runeLiteFormattedMessage) {
            this.runeLiteFormattedMessage = runeLiteFormattedMessage;
            return this;
        }

        public QueuedMessageBuilder timestamp(int timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public QueuedMessage build() {
            return new QueuedMessage(this.type, this.value, this.name, this.sender, this.runeLiteFormattedMessage, this.timestamp);
        }

        public String toString() {
            return "QueuedMessage.QueuedMessageBuilder(type=" + String.valueOf(this.type) + ", value=" + this.value + ", name=" + this.name + ", sender=" + this.sender + ", runeLiteFormattedMessage=" + this.runeLiteFormattedMessage + ", timestamp=" + this.timestamp + ")";
        }
    }
}

