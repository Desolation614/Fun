/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.coords.LocalPoint
 */
package net.runelite.client.game;

import net.runelite.api.coords.LocalPoint;

public final class ItemStack {
    private final int id;
    private final int quantity;

    public ItemStack(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    @Deprecated
    public ItemStack(int id, int quantity, LocalPoint location) {
        this.id = id;
        this.quantity = quantity;
    }

    @Deprecated
    public LocalPoint getLocation() {
        return null;
    }

    public int getId() {
        return this.id;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ItemStack)) {
            return false;
        }
        ItemStack other = (ItemStack)o;
        if (this.getId() != other.getId()) {
            return false;
        }
        return this.getQuantity() == other.getQuantity();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getId();
        result = result * 59 + this.getQuantity();
        return result;
    }

    public String toString() {
        return "ItemStack(id=" + this.getId() + ", quantity=" + this.getQuantity() + ")";
    }
}

