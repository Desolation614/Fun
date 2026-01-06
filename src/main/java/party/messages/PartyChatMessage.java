/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.party.messages;

import net.runelite.client.party.messages.PartyMemberMessage;

public final class PartyChatMessage
extends PartyMemberMessage {
    private final String value;

    public PartyChatMessage(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PartyChatMessage)) {
            return false;
        }
        PartyChatMessage other = (PartyChatMessage)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$value = this.getValue();
        String other$value = other.getValue();
        return !(this$value == null ? other$value != null : !this$value.equals(other$value));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PartyChatMessage;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : $value.hashCode());
        return result;
    }

    public String toString() {
        return "PartyChatMessage(value=" + this.getValue() + ")";
    }
}

