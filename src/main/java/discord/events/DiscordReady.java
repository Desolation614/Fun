/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.discord.events;

public final class DiscordReady {
    private final String userId;
    private final String username;
    private final String discriminator;
    private final String avatar;

    public DiscordReady(String userId, String username, String discriminator, String avatar) {
        this.userId = userId;
        this.username = username;
        this.discriminator = discriminator;
        this.avatar = avatar;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getDiscriminator() {
        return this.discriminator;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DiscordReady)) {
            return false;
        }
        DiscordReady other = (DiscordReady)o;
        String this$userId = this.getUserId();
        String other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$discriminator = this.getDiscriminator();
        String other$discriminator = other.getDiscriminator();
        if (this$discriminator == null ? other$discriminator != null : !this$discriminator.equals(other$discriminator)) {
            return false;
        }
        String this$avatar = this.getAvatar();
        String other$avatar = other.getAvatar();
        return !(this$avatar == null ? other$avatar != null : !this$avatar.equals(other$avatar));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $discriminator = this.getDiscriminator();
        result = result * 59 + ($discriminator == null ? 43 : $discriminator.hashCode());
        String $avatar = this.getAvatar();
        result = result * 59 + ($avatar == null ? 43 : $avatar.hashCode());
        return result;
    }

    public String toString() {
        return "DiscordReady(userId=" + this.getUserId() + ", username=" + this.getUsername() + ", discriminator=" + this.getDiscriminator() + ", avatar=" + this.getAvatar() + ")";
    }
}

