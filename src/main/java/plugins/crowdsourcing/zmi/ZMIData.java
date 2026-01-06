/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multiset
 */
package net.runelite.client.plugins.crowdsourcing.zmi;

import com.google.common.collect.Multiset;

public class ZMIData {
    private final int tickDelta;
    private final boolean ardougneMedium;
    private final int level;
    private final int xpGained;
    private final Multiset<Integer> itemsReceived;
    private final Multiset<Integer> itemsRemoved;

    public int getTickDelta() {
        return this.tickDelta;
    }

    public boolean isArdougneMedium() {
        return this.ardougneMedium;
    }

    public int getLevel() {
        return this.level;
    }

    public int getXpGained() {
        return this.xpGained;
    }

    public Multiset<Integer> getItemsReceived() {
        return this.itemsReceived;
    }

    public Multiset<Integer> getItemsRemoved() {
        return this.itemsRemoved;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ZMIData)) {
            return false;
        }
        ZMIData other = (ZMIData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getTickDelta() != other.getTickDelta()) {
            return false;
        }
        if (this.isArdougneMedium() != other.isArdougneMedium()) {
            return false;
        }
        if (this.getLevel() != other.getLevel()) {
            return false;
        }
        if (this.getXpGained() != other.getXpGained()) {
            return false;
        }
        Multiset<Integer> this$itemsReceived = this.getItemsReceived();
        Multiset<Integer> other$itemsReceived = other.getItemsReceived();
        if (this$itemsReceived == null ? other$itemsReceived != null : !this$itemsReceived.equals(other$itemsReceived)) {
            return false;
        }
        Multiset<Integer> this$itemsRemoved = this.getItemsRemoved();
        Multiset<Integer> other$itemsRemoved = other.getItemsRemoved();
        return !(this$itemsRemoved == null ? other$itemsRemoved != null : !this$itemsRemoved.equals(other$itemsRemoved));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ZMIData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getTickDelta();
        result = result * 59 + (this.isArdougneMedium() ? 79 : 97);
        result = result * 59 + this.getLevel();
        result = result * 59 + this.getXpGained();
        Multiset<Integer> $itemsReceived = this.getItemsReceived();
        result = result * 59 + ($itemsReceived == null ? 43 : $itemsReceived.hashCode());
        Multiset<Integer> $itemsRemoved = this.getItemsRemoved();
        result = result * 59 + ($itemsRemoved == null ? 43 : $itemsRemoved.hashCode());
        return result;
    }

    public String toString() {
        return "ZMIData(tickDelta=" + this.getTickDelta() + ", ardougneMedium=" + this.isArdougneMedium() + ", level=" + this.getLevel() + ", xpGained=" + this.getXpGained() + ", itemsReceived=" + String.valueOf(this.getItemsReceived()) + ", itemsRemoved=" + String.valueOf(this.getItemsRemoved()) + ")";
    }

    public ZMIData(int tickDelta, boolean ardougneMedium, int level, int xpGained, Multiset<Integer> itemsReceived, Multiset<Integer> itemsRemoved) {
        this.tickDelta = tickDelta;
        this.ardougneMedium = ardougneMedium;
        this.level = level;
        this.xpGained = xpGained;
        this.itemsReceived = itemsReceived;
        this.itemsRemoved = itemsRemoved;
    }
}

