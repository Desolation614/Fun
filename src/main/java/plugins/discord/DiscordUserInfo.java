/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.discord;

import net.runelite.client.party.messages.PartyMemberMessage;

final class DiscordUserInfo
extends PartyMemberMessage {
    private final String userId;
    private final String username;
    private final String discriminator;
    private final String avatarId;

    public DiscordUserInfo(String userId, String username, String discriminator, String avatarId) {
        this.userId = userId;
        this.username = username;
        this.discriminator = discriminator;
        this.avatarId = avatarId;
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

    public String getAvatarId() {
        return this.avatarId;
    }

    public String toString() {
        return "DiscordUserInfo(userId=" + this.getUserId() + ", username=" + this.getUsername() + ", discriminator=" + this.getDiscriminator() + ", avatarId=" + this.getAvatarId() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DiscordUserInfo)) {
            return false;
        }
        DiscordUserInfo other = (DiscordUserInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
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
        String this$avatarId = this.getAvatarId();
        String other$avatarId = other.getAvatarId();
        return !(this$avatarId == null ? other$avatarId != null : !this$avatarId.equals(other$avatarId));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DiscordUserInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $discriminator = this.getDiscriminator();
        result = result * 59 + ($discriminator == null ? 43 : $discriminator.hashCode());
        String $avatarId = this.getAvatarId();
        result = result * 59 + ($avatarId == null ? 43 : $avatarId.hashCode());
        return result;
    }
}

