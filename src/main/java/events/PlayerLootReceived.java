/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Player
 */
package net.runelite.client.events;

import java.util.Collection;
import net.runelite.api.Player;
import net.runelite.client.game.ItemStack;

public final class PlayerLootReceived {
    private final Player player;
    private final Collection<ItemStack> items;

    public PlayerLootReceived(Player player, Collection<ItemStack> items) {
        this.player = player;
        this.items = items;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Collection<ItemStack> getItems() {
        return this.items;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerLootReceived)) {
            return false;
        }
        PlayerLootReceived other = (PlayerLootReceived)o;
        Player this$player = this.getPlayer();
        Player other$player = other.getPlayer();
        if (this$player == null ? other$player != null : !this$player.equals(other$player)) {
            return false;
        }
        Collection<ItemStack> this$items = this.getItems();
        Collection<ItemStack> other$items = other.getItems();
        return !(this$items == null ? other$items != null : !((Object)this$items).equals(other$items));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Player $player = this.getPlayer();
        result = result * 59 + ($player == null ? 43 : $player.hashCode());
        Collection<ItemStack> $items = this.getItems();
        result = result * 59 + ($items == null ? 43 : ((Object)$items).hashCode());
        return result;
    }

    public String toString() {
        return "PlayerLootReceived(player=" + String.valueOf(this.getPlayer()) + ", items=" + String.valueOf(this.getItems()) + ")";
    }
}

