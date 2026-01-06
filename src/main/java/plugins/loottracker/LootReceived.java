/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.http.api.loottracker.LootRecordType
 */
package net.runelite.client.plugins.loottracker;

import java.util.Collection;
import net.runelite.client.game.ItemStack;
import net.runelite.http.api.loottracker.LootRecordType;

public class LootReceived {
    private String name;
    private int combatLevel;
    private LootRecordType type;
    private Collection<ItemStack> items;
    private int amount;

    public String getName() {
        return this.name;
    }

    public int getCombatLevel() {
        return this.combatLevel;
    }

    public LootRecordType getType() {
        return this.type;
    }

    public Collection<ItemStack> getItems() {
        return this.items;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCombatLevel(int combatLevel) {
        this.combatLevel = combatLevel;
    }

    public void setType(LootRecordType type) {
        this.type = type;
    }

    public void setItems(Collection<ItemStack> items) {
        this.items = items;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LootReceived)) {
            return false;
        }
        LootReceived other = (LootReceived)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getCombatLevel() != other.getCombatLevel()) {
            return false;
        }
        if (this.getAmount() != other.getAmount()) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        LootRecordType this$type = this.getType();
        LootRecordType other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        Collection<ItemStack> this$items = this.getItems();
        Collection<ItemStack> other$items = other.getItems();
        return !(this$items == null ? other$items != null : !((Object)this$items).equals(other$items));
    }

    protected boolean canEqual(Object other) {
        return other instanceof LootReceived;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCombatLevel();
        result = result * 59 + this.getAmount();
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        LootRecordType $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Collection<ItemStack> $items = this.getItems();
        result = result * 59 + ($items == null ? 43 : ((Object)$items).hashCode());
        return result;
    }

    public String toString() {
        return "LootReceived(name=" + this.getName() + ", combatLevel=" + this.getCombatLevel() + ", type=" + String.valueOf(this.getType()) + ", items=" + String.valueOf(this.getItems()) + ", amount=" + this.getAmount() + ")";
    }

    public LootReceived(String name, int combatLevel, LootRecordType type, Collection<ItemStack> items, int amount) {
        this.name = name;
        this.combatLevel = combatLevel;
        this.type = type;
        this.items = items;
        this.amount = amount;
    }
}

