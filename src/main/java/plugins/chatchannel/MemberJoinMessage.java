/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.MessageNode
 */
package net.runelite.client.plugins.chatchannel;

import net.runelite.api.MessageNode;

final class MemberJoinMessage {
    private final MessageNode messageNode;
    private final int getMessageId;
    private final int tick;

    public MemberJoinMessage(MessageNode messageNode, int getMessageId, int tick) {
        this.messageNode = messageNode;
        this.getMessageId = getMessageId;
        this.tick = tick;
    }

    public MessageNode getMessageNode() {
        return this.messageNode;
    }

    public int getGetMessageId() {
        return this.getMessageId;
    }

    public int getTick() {
        return this.tick;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MemberJoinMessage)) {
            return false;
        }
        MemberJoinMessage other = (MemberJoinMessage)o;
        if (this.getGetMessageId() != other.getGetMessageId()) {
            return false;
        }
        if (this.getTick() != other.getTick()) {
            return false;
        }
        MessageNode this$messageNode = this.getMessageNode();
        MessageNode other$messageNode = other.getMessageNode();
        return !(this$messageNode == null ? other$messageNode != null : !this$messageNode.equals(other$messageNode));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getGetMessageId();
        result = result * 59 + this.getTick();
        MessageNode $messageNode = this.getMessageNode();
        result = result * 59 + ($messageNode == null ? 43 : $messageNode.hashCode());
        return result;
    }

    public String toString() {
        return "MemberJoinMessage(messageNode=" + String.valueOf(this.getMessageNode()) + ", getMessageId=" + this.getGetMessageId() + ", tick=" + this.getTick() + ")";
    }
}

