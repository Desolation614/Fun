/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.groundmarkers;

import java.awt.Color;
import javax.annotation.Nullable;

final class GroundMarkerPoint {
    private final int regionId;
    private final int regionX;
    private final int regionY;
    private final int z;
    @Nullable
    private final Color color;
    @Nullable
    private final String label;

    public GroundMarkerPoint(int regionId, int regionX, int regionY, int z, @Nullable Color color, @Nullable String label) {
        this.regionId = regionId;
        this.regionX = regionX;
        this.regionY = regionY;
        this.z = z;
        this.color = color;
        this.label = label;
    }

    public int getRegionId() {
        return this.regionId;
    }

    public int getRegionX() {
        return this.regionX;
    }

    public int getRegionY() {
        return this.regionY;
    }

    public int getZ() {
        return this.z;
    }

    @Nullable
    public Color getColor() {
        return this.color;
    }

    @Nullable
    public String getLabel() {
        return this.label;
    }

    public String toString() {
        return "GroundMarkerPoint(regionId=" + this.getRegionId() + ", regionX=" + this.getRegionX() + ", regionY=" + this.getRegionY() + ", z=" + this.getZ() + ", color=" + String.valueOf(this.getColor()) + ", label=" + this.getLabel() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GroundMarkerPoint)) {
            return false;
        }
        GroundMarkerPoint other = (GroundMarkerPoint)o;
        if (this.getRegionId() != other.getRegionId()) {
            return false;
        }
        if (this.getRegionX() != other.getRegionX()) {
            return false;
        }
        if (this.getRegionY() != other.getRegionY()) {
            return false;
        }
        return this.getZ() == other.getZ();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getRegionId();
        result = result * 59 + this.getRegionX();
        result = result * 59 + this.getRegionY();
        result = result * 59 + this.getZ();
        return result;
    }
}

