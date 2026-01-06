/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.grandexchange;

import net.runelite.client.util.AsyncBufferedImage;

final class GrandExchangeItems {
    private final AsyncBufferedImage icon;
    private final String name;
    private final int itemId;
    private final int gePrice;
    private final int haPrice;
    private final int geItemLimit;

    public GrandExchangeItems(AsyncBufferedImage icon, String name, int itemId, int gePrice, int haPrice, int geItemLimit) {
        this.icon = icon;
        this.name = name;
        this.itemId = itemId;
        this.gePrice = gePrice;
        this.haPrice = haPrice;
        this.geItemLimit = geItemLimit;
    }

    public AsyncBufferedImage getIcon() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getGePrice() {
        return this.gePrice;
    }

    public int getHaPrice() {
        return this.haPrice;
    }

    public int getGeItemLimit() {
        return this.geItemLimit;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GrandExchangeItems)) {
            return false;
        }
        GrandExchangeItems other = (GrandExchangeItems)o;
        if (this.getItemId() != other.getItemId()) {
            return false;
        }
        if (this.getGePrice() != other.getGePrice()) {
            return false;
        }
        if (this.getHaPrice() != other.getHaPrice()) {
            return false;
        }
        if (this.getGeItemLimit() != other.getGeItemLimit()) {
            return false;
        }
        AsyncBufferedImage this$icon = this.getIcon();
        AsyncBufferedImage other$icon = other.getIcon();
        if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        return !(this$name == null ? other$name != null : !this$name.equals(other$name));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getItemId();
        result = result * 59 + this.getGePrice();
        result = result * 59 + this.getHaPrice();
        result = result * 59 + this.getGeItemLimit();
        AsyncBufferedImage $icon = this.getIcon();
        result = result * 59 + ($icon == null ? 43 : $icon.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    public String toString() {
        return "GrandExchangeItems(icon=" + String.valueOf(this.getIcon()) + ", name=" + this.getName() + ", itemId=" + this.getItemId() + ", gePrice=" + this.getGePrice() + ", haPrice=" + this.getHaPrice() + ", geItemLimit=" + this.getGeItemLimit() + ")";
    }
}

