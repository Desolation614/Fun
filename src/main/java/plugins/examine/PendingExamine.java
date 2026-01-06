/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.ChatMessageType
 */
package net.runelite.client.plugins.examine;

import net.runelite.api.ChatMessageType;

class PendingExamine {
    private ChatMessageType responseType;
    private int id;
    private int quantity;

    public ChatMessageType getResponseType() {
        return this.responseType;
    }

    public int getId() {
        return this.id;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setResponseType(ChatMessageType responseType) {
        this.responseType = responseType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PendingExamine)) {
            return false;
        }
        PendingExamine other = (PendingExamine)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        if (this.getQuantity() != other.getQuantity()) {
            return false;
        }
        ChatMessageType this$responseType = this.getResponseType();
        ChatMessageType other$responseType = other.getResponseType();
        return !(this$responseType == null ? other$responseType != null : !this$responseType.equals(other$responseType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PendingExamine;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getId();
        result = result * 59 + this.getQuantity();
        ChatMessageType $responseType = this.getResponseType();
        result = result * 59 + ($responseType == null ? 43 : $responseType.hashCode());
        return result;
    }

    public String toString() {
        return "PendingExamine(responseType=" + String.valueOf(this.getResponseType()) + ", id=" + this.getId() + ", quantity=" + this.getQuantity() + ")";
    }
}

