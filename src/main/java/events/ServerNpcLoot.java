/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.NPCComposition
 */
package net.runelite.client.events;

import java.util.Collection;
import net.runelite.api.NPCComposition;
import net.runelite.client.game.ItemStack;

public final class ServerNpcLoot {
    private final NPCComposition composition;
    private final Collection<ItemStack> items;

    public ServerNpcLoot(NPCComposition composition, Collection<ItemStack> items) {
        this.composition = composition;
        this.items = items;
    }

    public NPCComposition getComposition() {
        return this.composition;
    }

    public Collection<ItemStack> getItems() {
        return this.items;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServerNpcLoot)) {
            return false;
        }
        ServerNpcLoot other = (ServerNpcLoot)o;
        NPCComposition this$composition = this.getComposition();
        NPCComposition other$composition = other.getComposition();
        if (this$composition == null ? other$composition != null : !this$composition.equals(other$composition)) {
            return false;
        }
        Collection<ItemStack> this$items = this.getItems();
        Collection<ItemStack> other$items = other.getItems();
        return !(this$items == null ? other$items != null : !((Object)this$items).equals(other$items));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        NPCComposition $composition = this.getComposition();
        result = result * 59 + ($composition == null ? 43 : $composition.hashCode());
        Collection<ItemStack> $items = this.getItems();
        result = result * 59 + ($items == null ? 43 : ((Object)$items).hashCode());
        return result;
    }

    public String toString() {
        return "ServerNpcLoot(composition=" + String.valueOf(this.getComposition()) + ", items=" + String.valueOf(this.getItems()) + ")";
    }
}

