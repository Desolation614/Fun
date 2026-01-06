/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.crowdsourcing.dialogue;

public class NpcDialogueData {
    private final String message;
    private final String name;

    public String getMessage() {
        return this.message;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NpcDialogueData)) {
            return false;
        }
        NpcDialogueData other = (NpcDialogueData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        return !(this$name == null ? other$name != null : !this$name.equals(other$name));
    }

    protected boolean canEqual(Object other) {
        return other instanceof NpcDialogueData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    public String toString() {
        return "NpcDialogueData(message=" + this.getMessage() + ", name=" + this.getName() + ")";
    }

    public NpcDialogueData(String message, String name) {
        this.message = message;
        this.name = name;
    }
}

