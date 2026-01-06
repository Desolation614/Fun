/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.runelite.http.api.item.ItemEquipmentStats
 *  net.runelite.http.api.item.ItemStats
 */
package net.runelite.client.game;

import com.google.gson.annotations.SerializedName;
import net.runelite.client.game.ItemEquipmentStats;

public final class ItemStats {
    private final boolean equipable;
    private final double weight;
    @SerializedName(value="ge_limit")
    private final int geLimit;
    private final ItemEquipmentStats equipment;

    net.runelite.http.api.item.ItemStats toHttpApiFormat() {
        net.runelite.http.api.item.ItemEquipmentStats equipment = this.equipment == null ? null : this.equipment.toHttpApiFormat();
        return new net.runelite.http.api.item.ItemStats(this.equipable, this.weight, this.geLimit, equipment);
    }

    public ItemStats(boolean equipable, double weight, int geLimit, ItemEquipmentStats equipment) {
        this.equipable = equipable;
        this.weight = weight;
        this.geLimit = geLimit;
        this.equipment = equipment;
    }

    public boolean isEquipable() {
        return this.equipable;
    }

    public double getWeight() {
        return this.weight;
    }

    public int getGeLimit() {
        return this.geLimit;
    }

    public ItemEquipmentStats getEquipment() {
        return this.equipment;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ItemStats)) {
            return false;
        }
        ItemStats other = (ItemStats)o;
        if (this.isEquipable() != other.isEquipable()) {
            return false;
        }
        if (Double.compare(this.getWeight(), other.getWeight()) != 0) {
            return false;
        }
        if (this.getGeLimit() != other.getGeLimit()) {
            return false;
        }
        ItemEquipmentStats this$equipment = this.getEquipment();
        ItemEquipmentStats other$equipment = other.getEquipment();
        return !(this$equipment == null ? other$equipment != null : !((Object)this$equipment).equals(other$equipment));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isEquipable() ? 79 : 97);
        long $weight = Double.doubleToLongBits(this.getWeight());
        result = result * 59 + (int)($weight >>> 32 ^ $weight);
        result = result * 59 + this.getGeLimit();
        ItemEquipmentStats $equipment = this.getEquipment();
        result = result * 59 + ($equipment == null ? 43 : ((Object)$equipment).hashCode());
        return result;
    }

    public String toString() {
        return "ItemStats(equipable=" + this.isEquipable() + ", weight=" + this.getWeight() + ", geLimit=" + this.getGeLimit() + ", equipment=" + String.valueOf(this.getEquipment()) + ")";
    }
}

