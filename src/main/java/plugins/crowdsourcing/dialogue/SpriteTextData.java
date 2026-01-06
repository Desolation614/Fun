/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.crowdsourcing.dialogue;

public class SpriteTextData {
    private String message;
    private int itemId1;

    public String getMessage() {
        return this.message;
    }

    public int getItemId1() {
        return this.itemId1;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setItemId1(int itemId1) {
        this.itemId1 = itemId1;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SpriteTextData)) {
            return false;
        }
        SpriteTextData other = (SpriteTextData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getItemId1() != other.getItemId1()) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SpriteTextData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getItemId1();
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "SpriteTextData(message=" + this.getMessage() + ", itemId1=" + this.getItemId1() + ")";
    }

    public SpriteTextData(String message, int itemId1) {
        this.message = message;
        this.itemId1 = itemId1;
    }
}

