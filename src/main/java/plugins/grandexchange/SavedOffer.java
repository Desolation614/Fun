/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.GrandExchangeOfferState
 */
package net.runelite.client.plugins.grandexchange;

import net.runelite.api.GrandExchangeOfferState;

class SavedOffer {
    private int itemId;
    private int quantitySold;
    private int totalQuantity;
    private int price;
    private int spent;
    private GrandExchangeOfferState state;

    public int getItemId() {
        return this.itemId;
    }

    public int getQuantitySold() {
        return this.quantitySold;
    }

    public int getTotalQuantity() {
        return this.totalQuantity;
    }

    public int getPrice() {
        return this.price;
    }

    public int getSpent() {
        return this.spent;
    }

    public GrandExchangeOfferState getState() {
        return this.state;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSpent(int spent) {
        this.spent = spent;
    }

    public void setState(GrandExchangeOfferState state) {
        this.state = state;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SavedOffer)) {
            return false;
        }
        SavedOffer other = (SavedOffer)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getItemId() != other.getItemId()) {
            return false;
        }
        if (this.getQuantitySold() != other.getQuantitySold()) {
            return false;
        }
        if (this.getTotalQuantity() != other.getTotalQuantity()) {
            return false;
        }
        if (this.getPrice() != other.getPrice()) {
            return false;
        }
        if (this.getSpent() != other.getSpent()) {
            return false;
        }
        GrandExchangeOfferState this$state = this.getState();
        GrandExchangeOfferState other$state = other.getState();
        return !(this$state == null ? other$state != null : !this$state.equals(other$state));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SavedOffer;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getItemId();
        result = result * 59 + this.getQuantitySold();
        result = result * 59 + this.getTotalQuantity();
        result = result * 59 + this.getPrice();
        result = result * 59 + this.getSpent();
        GrandExchangeOfferState $state = this.getState();
        result = result * 59 + ($state == null ? 43 : $state.hashCode());
        return result;
    }

    public String toString() {
        return "SavedOffer(itemId=" + this.getItemId() + ", quantitySold=" + this.getQuantitySold() + ", totalQuantity=" + this.getTotalQuantity() + ", price=" + this.getPrice() + ", spent=" + this.getSpent() + ", state=" + String.valueOf(this.getState()) + ")";
    }
}

