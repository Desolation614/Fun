/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.grounditems;

import net.runelite.client.plugins.grounditems.GroundItem;

final class NamedQuantity {
    private final String name;
    private final int quantity;

    NamedQuantity(GroundItem groundItem) {
        this(groundItem.getName(), groundItem.getQuantity());
    }

    public String getName() {
        return this.name;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NamedQuantity)) {
            return false;
        }
        NamedQuantity other = (NamedQuantity)o;
        if (this.getQuantity() != other.getQuantity()) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        return !(this$name == null ? other$name != null : !this$name.equals(other$name));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getQuantity();
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    public String toString() {
        return "NamedQuantity(name=" + this.getName() + ", quantity=" + this.getQuantity() + ")";
    }

    public NamedQuantity(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
}

