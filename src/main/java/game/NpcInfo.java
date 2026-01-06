/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.game;

public class NpcInfo {
    private int hitpoints;

    public int getHitpoints() {
        return this.hitpoints;
    }

    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NpcInfo)) {
            return false;
        }
        NpcInfo other = (NpcInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return this.getHitpoints() == other.getHitpoints();
    }

    protected boolean canEqual(Object other) {
        return other instanceof NpcInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getHitpoints();
        return result;
    }

    public String toString() {
        return "NpcInfo(hitpoints=" + this.getHitpoints() + ")";
    }
}

