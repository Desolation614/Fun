/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.crowdsourcing.dialogue;

public class DoubleSpriteTextData {
    private String message;
    private int itemId1;
    private int itemId2;

    public String getMessage() {
        return this.message;
    }

    public int getItemId1() {
        return this.itemId1;
    }

    public int getItemId2() {
        return this.itemId2;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setItemId1(int itemId1) {
        this.itemId1 = itemId1;
    }

    public void setItemId2(int itemId2) {
        this.itemId2 = itemId2;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DoubleSpriteTextData)) {
            return false;
        }
        DoubleSpriteTextData other = (DoubleSpriteTextData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getItemId1() != other.getItemId1()) {
            return false;
        }
        if (this.getItemId2() != other.getItemId2()) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DoubleSpriteTextData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getItemId1();
        result = result * 59 + this.getItemId2();
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "DoubleSpriteTextData(message=" + this.getMessage() + ", itemId1=" + this.getItemId1() + ", itemId2=" + this.getItemId2() + ")";
    }

    public DoubleSpriteTextData(String message, int itemId1, int itemId2) {
        this.message = message;
        this.itemId1 = itemId1;
        this.itemId2 = itemId2;
    }
}

