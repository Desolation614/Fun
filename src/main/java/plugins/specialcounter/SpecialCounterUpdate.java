/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.specialcounter;

import net.runelite.client.party.messages.PartyMemberMessage;
import net.runelite.client.plugins.specialcounter.SpecialWeapon;

public final class SpecialCounterUpdate
extends PartyMemberMessage {
    private final int npcIndex;
    private final SpecialWeapon weapon;
    private final int hit;
    private final int world;
    private final int playerId;

    public SpecialCounterUpdate(int npcIndex, SpecialWeapon weapon, int hit, int world, int playerId) {
        this.npcIndex = npcIndex;
        this.weapon = weapon;
        this.hit = hit;
        this.world = world;
        this.playerId = playerId;
    }

    public int getNpcIndex() {
        return this.npcIndex;
    }

    public SpecialWeapon getWeapon() {
        return this.weapon;
    }

    public int getHit() {
        return this.hit;
    }

    public int getWorld() {
        return this.world;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public String toString() {
        return "SpecialCounterUpdate(npcIndex=" + this.getNpcIndex() + ", weapon=" + String.valueOf((Object)this.getWeapon()) + ", hit=" + this.getHit() + ", world=" + this.getWorld() + ", playerId=" + this.getPlayerId() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SpecialCounterUpdate)) {
            return false;
        }
        SpecialCounterUpdate other = (SpecialCounterUpdate)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (this.getNpcIndex() != other.getNpcIndex()) {
            return false;
        }
        if (this.getHit() != other.getHit()) {
            return false;
        }
        if (this.getWorld() != other.getWorld()) {
            return false;
        }
        if (this.getPlayerId() != other.getPlayerId()) {
            return false;
        }
        SpecialWeapon this$weapon = this.getWeapon();
        SpecialWeapon other$weapon = other.getWeapon();
        return !(this$weapon == null ? other$weapon != null : !((Object)((Object)this$weapon)).equals((Object)other$weapon));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SpecialCounterUpdate;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + this.getNpcIndex();
        result = result * 59 + this.getHit();
        result = result * 59 + this.getWorld();
        result = result * 59 + this.getPlayerId();
        SpecialWeapon $weapon = this.getWeapon();
        result = result * 59 + ($weapon == null ? 43 : ((Object)((Object)$weapon)).hashCode());
        return result;
    }
}

