/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.discord.events;

public final class DiscordDisconnected {
    private final int errorCode;
    private final String message;

    public DiscordDisconnected(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DiscordDisconnected)) {
            return false;
        }
        DiscordDisconnected other = (DiscordDisconnected)o;
        if (this.getErrorCode() != other.getErrorCode()) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getErrorCode();
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "DiscordDisconnected(errorCode=" + this.getErrorCode() + ", message=" + this.getMessage() + ")";
    }
}

