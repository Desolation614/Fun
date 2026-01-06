/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 */
package net.runelite.client.plugins.grounditems;

import com.google.common.base.Strings;

final class ItemThreshold {
    private final String itemName;
    private final int quantity;
    private final Inequality inequality;

    static ItemThreshold fromConfigEntry(String entry) {
        if (Strings.isNullOrEmpty((String)entry)) {
            return null;
        }
        Inequality operator = Inequality.MORE_THAN;
        int qty = 0;
        for (int i = entry.length() - 1; i >= 0; --i) {
            char c = entry.charAt(i);
            if (c >= '0' && c <= '9' || Character.isWhitespace(c)) continue;
            switch (c) {
                case '<': {
                    operator = Inequality.LESS_THAN;
                }
                case '>': {
                    if (i + 1 >= entry.length()) break;
                    try {
                        qty = Integer.parseInt(entry.substring(i + 1).trim());
                    }
                    catch (NumberFormatException e) {
                        qty = 0;
                        operator = Inequality.MORE_THAN;
                    }
                    entry = entry.substring(0, i);
                }
            }
            break;
        }
        return new ItemThreshold(entry.trim(), qty, operator);
    }

    boolean quantityHolds(int itemCount) {
        if (this.inequality == Inequality.LESS_THAN) {
            return itemCount < this.quantity;
        }
        return itemCount > this.quantity;
    }

    public ItemThreshold(String itemName, int quantity, Inequality inequality) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.inequality = inequality;
    }

    public String getItemName() {
        return this.itemName;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public Inequality getInequality() {
        return this.inequality;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ItemThreshold)) {
            return false;
        }
        ItemThreshold other = (ItemThreshold)o;
        if (this.getQuantity() != other.getQuantity()) {
            return false;
        }
        String this$itemName = this.getItemName();
        String other$itemName = other.getItemName();
        if (this$itemName == null ? other$itemName != null : !this$itemName.equals(other$itemName)) {
            return false;
        }
        Inequality this$inequality = this.getInequality();
        Inequality other$inequality = other.getInequality();
        return !(this$inequality == null ? other$inequality != null : !((Object)((Object)this$inequality)).equals((Object)other$inequality));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getQuantity();
        String $itemName = this.getItemName();
        result = result * 59 + ($itemName == null ? 43 : $itemName.hashCode());
        Inequality $inequality = this.getInequality();
        result = result * 59 + ($inequality == null ? 43 : ((Object)((Object)$inequality)).hashCode());
        return result;
    }

    public String toString() {
        return "ItemThreshold(itemName=" + this.getItemName() + ", quantity=" + this.getQuantity() + ", inequality=" + String.valueOf((Object)this.getInequality()) + ")";
    }

    static enum Inequality {
        LESS_THAN,
        MORE_THAN;

    }
}

