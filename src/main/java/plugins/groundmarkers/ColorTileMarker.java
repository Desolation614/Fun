/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.groundmarkers;

import java.awt.Color;
import javax.annotation.Nullable;
import net.runelite.api.coords.WorldPoint;

final class ColorTileMarker {
    private final WorldPoint worldPoint;
    @Nullable
    private final Color color;
    @Nullable
    private final String label;

    public ColorTileMarker(WorldPoint worldPoint, @Nullable Color color, @Nullable String label) {
        this.worldPoint = worldPoint;
        this.color = color;
        this.label = label;
    }

    public WorldPoint getWorldPoint() {
        return this.worldPoint;
    }

    @Nullable
    public Color getColor() {
        return this.color;
    }

    @Nullable
    public String getLabel() {
        return this.label;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ColorTileMarker)) {
            return false;
        }
        ColorTileMarker other = (ColorTileMarker)o;
        WorldPoint this$worldPoint = this.getWorldPoint();
        WorldPoint other$worldPoint = other.getWorldPoint();
        if (this$worldPoint == null ? other$worldPoint != null : !this$worldPoint.equals(other$worldPoint)) {
            return false;
        }
        Color this$color = this.getColor();
        Color other$color = other.getColor();
        if (this$color == null ? other$color != null : !((Object)this$color).equals(other$color)) {
            return false;
        }
        String this$label = this.getLabel();
        String other$label = other.getLabel();
        return !(this$label == null ? other$label != null : !this$label.equals(other$label));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        WorldPoint $worldPoint = this.getWorldPoint();
        result = result * 59 + ($worldPoint == null ? 43 : $worldPoint.hashCode());
        Color $color = this.getColor();
        result = result * 59 + ($color == null ? 43 : ((Object)$color).hashCode());
        String $label = this.getLabel();
        result = result * 59 + ($label == null ? 43 : $label.hashCode());
        return result;
    }

    public String toString() {
        return "ColorTileMarker(worldPoint=" + String.valueOf(this.getWorldPoint()) + ", color=" + String.valueOf(this.getColor()) + ", label=" + this.getLabel() + ")";
    }
}

