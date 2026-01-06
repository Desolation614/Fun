/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.discord.events;

public final class DiscordSpectateGame {
    private final String spectateSecret;

    public DiscordSpectateGame(String spectateSecret) {
        this.spectateSecret = spectateSecret;
    }

    public String getSpectateSecret() {
        return this.spectateSecret;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DiscordSpectateGame)) {
            return false;
        }
        DiscordSpectateGame other = (DiscordSpectateGame)o;
        String this$spectateSecret = this.getSpectateSecret();
        String other$spectateSecret = other.getSpectateSecret();
        return !(this$spectateSecret == null ? other$spectateSecret != null : !this$spectateSecret.equals(other$spectateSecret));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $spectateSecret = this.getSpectateSecret();
        result = result * 59 + ($spectateSecret == null ? 43 : $spectateSecret.hashCode());
        return result;
    }

    public String toString() {
        return "DiscordSpectateGame(spectateSecret=" + this.getSpectateSecret() + ")";
    }
}

