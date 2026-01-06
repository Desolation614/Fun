/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.inventorytags;

import java.awt.Color;

class Tag {
    Color color;

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag other = (Tag)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Color this$color = this.getColor();
        Color other$color = other.getColor();
        return !(this$color == null ? other$color != null : !((Object)this$color).equals(other$color));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Tag;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Color $color = this.getColor();
        result = result * 59 + ($color == null ? 43 : ((Object)$color).hashCode());
        return result;
    }

    public String toString() {
        return "Tag(color=" + String.valueOf(this.getColor()) + ")";
    }
}

