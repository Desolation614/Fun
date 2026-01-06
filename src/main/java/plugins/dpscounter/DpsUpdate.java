/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.dpscounter;

import net.runelite.client.party.messages.PartyMemberMessage;

public final class DpsUpdate
extends PartyMemberMessage {
    private final int hit;
    private final boolean isBoss;

    public DpsUpdate(int hit, boolean isBoss) {
        this.hit = hit;
        this.isBoss = isBoss;
    }

    public int getHit() {
        return this.hit;
    }

    public boolean isBoss() {
        return this.isBoss;
    }

    public String toString() {
        return "DpsUpdate(hit=" + this.getHit() + ", isBoss=" + this.isBoss() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DpsUpdate)) {
            return false;
        }
        DpsUpdate other = (DpsUpdate)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (this.getHit() != other.getHit()) {
            return false;
        }
        return this.isBoss() == other.isBoss();
    }

    protected boolean canEqual(Object other) {
        return other instanceof DpsUpdate;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + this.getHit();
        result = result * 59 + (this.isBoss() ? 79 : 97);
        return result;
    }
}

