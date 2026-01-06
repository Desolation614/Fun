/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.party;

import java.awt.image.BufferedImage;

public class PartyMember {
    private final long memberId;
    private String displayName = "<unknown>";
    private boolean loggedIn;
    private BufferedImage avatar;

    public PartyMember(long memberId) {
        this.memberId = memberId;
    }

    public long getMemberId() {
        return this.memberId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public BufferedImage getAvatar() {
        return this.avatar;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setAvatar(BufferedImage avatar) {
        this.avatar = avatar;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PartyMember)) {
            return false;
        }
        PartyMember other = (PartyMember)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getMemberId() != other.getMemberId()) {
            return false;
        }
        if (this.isLoggedIn() != other.isLoggedIn()) {
            return false;
        }
        String this$displayName = this.getDisplayName();
        String other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) {
            return false;
        }
        BufferedImage this$avatar = this.getAvatar();
        BufferedImage other$avatar = other.getAvatar();
        return !(this$avatar == null ? other$avatar != null : !this$avatar.equals(other$avatar));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PartyMember;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $memberId = this.getMemberId();
        result = result * 59 + (int)($memberId >>> 32 ^ $memberId);
        result = result * 59 + (this.isLoggedIn() ? 79 : 97);
        String $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        BufferedImage $avatar = this.getAvatar();
        result = result * 59 + ($avatar == null ? 43 : $avatar.hashCode());
        return result;
    }

    public String toString() {
        return "PartyMember(memberId=" + this.getMemberId() + ", displayName=" + this.getDisplayName() + ", loggedIn=" + this.isLoggedIn() + ", avatar=" + String.valueOf(this.getAvatar()) + ")";
    }
}

