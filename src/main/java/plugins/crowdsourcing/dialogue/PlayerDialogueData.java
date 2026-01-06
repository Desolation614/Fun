/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.crowdsourcing.dialogue;

public class PlayerDialogueData {
    private final String message;

    public String getMessage() {
        return this.message;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerDialogueData)) {
            return false;
        }
        PlayerDialogueData other = (PlayerDialogueData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlayerDialogueData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "PlayerDialogueData(message=" + this.getMessage() + ")";
    }

    public PlayerDialogueData(String message) {
        this.message = message;
    }
}

