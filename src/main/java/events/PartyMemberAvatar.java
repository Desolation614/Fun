/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.events;

import java.awt.image.BufferedImage;

public final class PartyMemberAvatar {
    private final long memberId;
    private final BufferedImage image;

    public PartyMemberAvatar(long memberId, BufferedImage image) {
        this.memberId = memberId;
        this.image = image;
    }

    public long getMemberId() {
        return this.memberId;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PartyMemberAvatar)) {
            return false;
        }
        PartyMemberAvatar other = (PartyMemberAvatar)o;
        if (this.getMemberId() != other.getMemberId()) {
            return false;
        }
        BufferedImage this$image = this.getImage();
        BufferedImage other$image = other.getImage();
        return !(this$image == null ? other$image != null : !this$image.equals(other$image));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $memberId = this.getMemberId();
        result = result * 59 + (int)($memberId >>> 32 ^ $memberId);
        BufferedImage $image = this.getImage();
        result = result * 59 + ($image == null ? 43 : $image.hashCode());
        return result;
    }

    public String toString() {
        return "PartyMemberAvatar(memberId=" + this.getMemberId() + ", image=" + String.valueOf(this.getImage()) + ")";
    }
}

