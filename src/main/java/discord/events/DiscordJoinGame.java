/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.discord.events;

public final class DiscordJoinGame {
    private final String joinSecret;

    public DiscordJoinGame(String joinSecret) {
        this.joinSecret = joinSecret;
    }

    public String getJoinSecret() {
        return this.joinSecret;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DiscordJoinGame)) {
            return false;
        }
        DiscordJoinGame other = (DiscordJoinGame)o;
        String this$joinSecret = this.getJoinSecret();
        String other$joinSecret = other.getJoinSecret();
        return !(this$joinSecret == null ? other$joinSecret != null : !this$joinSecret.equals(other$joinSecret));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $joinSecret = this.getJoinSecret();
        result = result * 59 + ($joinSecret == null ? 43 : $joinSecret.hashCode());
        return result;
    }

    public String toString() {
        return "DiscordJoinGame(joinSecret=" + this.getJoinSecret() + ")";
    }
}

