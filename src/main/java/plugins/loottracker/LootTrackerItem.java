/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.loottracker;

class LootTrackerItem {
    private final int id;
    private final String name;
    private final int quantity;
    private final int gePrice;
    private final int haPrice;
    private boolean ignored;

    long getTotalGePrice() {
        return (long)this.gePrice * (long)this.quantity;
    }

    long getTotalHaPrice() {
        return (long)this.haPrice * (long)this.quantity;
    }

    public LootTrackerItem(int id, String name, int quantity, int gePrice, int haPrice, boolean ignored) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.gePrice = gePrice;
        this.haPrice = haPrice;
        this.ignored = ignored;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getGePrice() {
        return this.gePrice;
    }

    public int getHaPrice() {
        return this.haPrice;
    }

    public boolean isIgnored() {
        return this.ignored;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LootTrackerItem)) {
            return false;
        }
        LootTrackerItem other = (LootTrackerItem)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        if (this.getQuantity() != other.getQuantity()) {
            return false;
        }
        if (this.getGePrice() != other.getGePrice()) {
            return false;
        }
        if (this.getHaPrice() != other.getHaPrice()) {
            return false;
        }
        if (this.isIgnored() != other.isIgnored()) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        return !(this$name == null ? other$name != null : !this$name.equals(other$name));
    }

    protected boolean canEqual(Object other) {
        return other instanceof LootTrackerItem;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getId();
        result = result * 59 + this.getQuantity();
        result = result * 59 + this.getGePrice();
        result = result * 59 + this.getHaPrice();
        result = result * 59 + (this.isIgnored() ? 79 : 97);
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }
}

