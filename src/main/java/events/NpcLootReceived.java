/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.NPC
 */
package net.runelite.client.events;

import java.util.Collection;
import net.runelite.api.NPC;
import net.runelite.client.game.ItemStack;

public final class NpcLootReceived {
    private final NPC npc;
    private final Collection<ItemStack> items;

    public NpcLootReceived(NPC npc, Collection<ItemStack> items) {
        this.npc = npc;
        this.items = items;
    }

    public NPC getNpc() {
        return this.npc;
    }

    public Collection<ItemStack> getItems() {
        return this.items;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NpcLootReceived)) {
            return false;
        }
        NpcLootReceived other = (NpcLootReceived)o;
        NPC this$npc = this.getNpc();
        NPC other$npc = other.getNpc();
        if (this$npc == null ? other$npc != null : !this$npc.equals(other$npc)) {
            return false;
        }
        Collection<ItemStack> this$items = this.getItems();
        Collection<ItemStack> other$items = other.getItems();
        return !(this$items == null ? other$items != null : !((Object)this$items).equals(other$items));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        NPC $npc = this.getNpc();
        result = result * 59 + ($npc == null ? 43 : $npc.hashCode());
        Collection<ItemStack> $items = this.getItems();
        result = result * 59 + ($items == null ? 43 : ((Object)$items).hashCode());
        return result;
    }

    public String toString() {
        return "NpcLootReceived(npc=" + String.valueOf(this.getNpc()) + ", items=" + String.valueOf(this.getItems()) + ")";
    }
}

