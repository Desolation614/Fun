/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.objectindicators;

import com.google.gson.annotations.SerializedName;
import java.awt.Color;
import javax.annotation.Nullable;

class ObjectPoint {
    private int id = -1;
    private String name;
    private int regionId;
    private int regionX;
    private int regionY;
    private int z;
    @Nullable
    @SerializedName(value="color")
    private Color borderColor;
    @Nullable
    private Color fillColor;
    private Boolean hull;
    private Boolean outline;
    private Boolean clickbox;
    private Boolean tile;

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
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
    public Color getBorderColor() {
        return this.borderColor;
    }

    @Nullable
    public Color getFillColor() {
        return this.fillColor;
    }

    public Boolean getHull() {
        return this.hull;
    }

    public Boolean getOutline() {
        return this.outline;
    }

    public Boolean getClickbox() {
        return this.clickbox;
    }

    public Boolean getTile() {
        return this.tile;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public void setRegionX(int regionX) {
        this.regionX = regionX;
    }

    public void setRegionY(int regionY) {
        this.regionY = regionY;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setBorderColor(@Nullable Color borderColor) {
        this.borderColor = borderColor;
    }

    public void setFillColor(@Nullable Color fillColor) {
        this.fillColor = fillColor;
    }

    public void setHull(Boolean hull) {
        this.hull = hull;
    }

    public void setOutline(Boolean outline) {
        this.outline = outline;
    }

    public void setClickbox(Boolean clickbox) {
        this.clickbox = clickbox;
    }

    public void setTile(Boolean tile) {
        this.tile = tile;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ObjectPoint)) {
            return false;
        }
        ObjectPoint other = (ObjectPoint)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        if (this.getRegionId() != other.getRegionId()) {
            return false;
        }
        if (this.getRegionX() != other.getRegionX()) {
            return false;
        }
        if (this.getRegionY() != other.getRegionY()) {
            return false;
        }
        if (this.getZ() != other.getZ()) {
            return false;
        }
        Boolean this$hull = this.getHull();
        Boolean other$hull = other.getHull();
        if (this$hull == null ? other$hull != null : !((Object)this$hull).equals(other$hull)) {
            return false;
        }
        Boolean this$outline = this.getOutline();
        Boolean other$outline = other.getOutline();
        if (this$outline == null ? other$outline != null : !((Object)this$outline).equals(other$outline)) {
            return false;
        }
        Boolean this$clickbox = this.getClickbox();
        Boolean other$clickbox = other.getClickbox();
        if (this$clickbox == null ? other$clickbox != null : !((Object)this$clickbox).equals(other$clickbox)) {
            return false;
        }
        Boolean this$tile = this.getTile();
        Boolean other$tile = other.getTile();
        if (this$tile == null ? other$tile != null : !((Object)this$tile).equals(other$tile)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        Color this$borderColor = this.getBorderColor();
        Color other$borderColor = other.getBorderColor();
        if (this$borderColor == null ? other$borderColor != null : !((Object)this$borderColor).equals(other$borderColor)) {
            return false;
        }
        Color this$fillColor = this.getFillColor();
        Color other$fillColor = other.getFillColor();
        return !(this$fillColor == null ? other$fillColor != null : !((Object)this$fillColor).equals(other$fillColor));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ObjectPoint;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getId();
        result = result * 59 + this.getRegionId();
        result = result * 59 + this.getRegionX();
        result = result * 59 + this.getRegionY();
        result = result * 59 + this.getZ();
        Boolean $hull = this.getHull();
        result = result * 59 + ($hull == null ? 43 : ((Object)$hull).hashCode());
        Boolean $outline = this.getOutline();
        result = result * 59 + ($outline == null ? 43 : ((Object)$outline).hashCode());
        Boolean $clickbox = this.getClickbox();
        result = result * 59 + ($clickbox == null ? 43 : ((Object)$clickbox).hashCode());
        Boolean $tile = this.getTile();
        result = result * 59 + ($tile == null ? 43 : ((Object)$tile).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Color $borderColor = this.getBorderColor();
        result = result * 59 + ($borderColor == null ? 43 : ((Object)$borderColor).hashCode());
        Color $fillColor = this.getFillColor();
        result = result * 59 + ($fillColor == null ? 43 : ((Object)$fillColor).hashCode());
        return result;
    }

    public String toString() {
        return "ObjectPoint(id=" + this.getId() + ", name=" + this.getName() + ", regionId=" + this.getRegionId() + ", regionX=" + this.getRegionX() + ", regionY=" + this.getRegionY() + ", z=" + this.getZ() + ", borderColor=" + String.valueOf(this.getBorderColor()) + ", fillColor=" + String.valueOf(this.getFillColor()) + ", hull=" + this.getHull() + ", outline=" + this.getOutline() + ", clickbox=" + this.getClickbox() + ", tile=" + this.getTile() + ")";
    }

    public ObjectPoint() {
    }

    public ObjectPoint(int id, String name, int regionId, int regionX, int regionY, int z, @Nullable Color borderColor, @Nullable Color fillColor, Boolean hull, Boolean outline, Boolean clickbox, Boolean tile) {
        this.id = id;
        this.name = name;
        this.regionId = regionId;
        this.regionX = regionX;
        this.regionY = regionY;
        this.z = z;
        this.borderColor = borderColor;
        this.fillColor = fillColor;
        this.hull = hull;
        this.outline = outline;
        this.clickbox = clickbox;
        this.tile = tile;
    }
}

