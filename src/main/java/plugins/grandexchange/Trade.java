/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package net.runelite.client.plugins.grandexchange;

import com.google.gson.annotations.SerializedName;
import java.time.Instant;

class Trade {
    @SerializedName(value="b")
    boolean buy;
    @SerializedName(value="i")
    int itemId;
    @SerializedName(value="q")
    int quantity;
    @SerializedName(value="p")
    int price;
    @SerializedName(value="t")
    Instant time;

    public boolean isBuy() {
        return this.buy;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getPrice() {
        return this.price;
    }

    public Instant getTime() {
        return this.time;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Trade)) {
            return false;
        }
        Trade other = (Trade)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isBuy() != other.isBuy()) {
            return false;
        }
        if (this.getItemId() != other.getItemId()) {
            return false;
        }
        if (this.getQuantity() != other.getQuantity()) {
            return false;
        }
        if (this.getPrice() != other.getPrice()) {
            return false;
        }
        Instant this$time = this.getTime();
        Instant other$time = other.getTime();
        return !(this$time == null ? other$time != null : !((Object)this$time).equals(other$time));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Trade;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isBuy() ? 79 : 97);
        result = result * 59 + this.getItemId();
        result = result * 59 + this.getQuantity();
        result = result * 59 + this.getPrice();
        Instant $time = this.getTime();
        result = result * 59 + ($time == null ? 43 : ((Object)$time).hashCode());
        return result;
    }

    public String toString() {
        return "Trade(buy=" + this.isBuy() + ", itemId=" + this.getItemId() + ", quantity=" + this.getQuantity() + ", price=" + this.getPrice() + ", time=" + String.valueOf(this.getTime()) + ")";
    }
}

