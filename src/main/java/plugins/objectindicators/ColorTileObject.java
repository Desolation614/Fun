/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.runelite.api.ObjectComposition
 *  net.runelite.api.TileObject
 */
package net.runelite.client.plugins.objectindicators;

import java.awt.Color;
import javax.annotation.Nullable;
import net.runelite.api.ObjectComposition;
import net.runelite.api.TileObject;

final class ColorTileObject {
    static final int HF_HULL = 1;
    static final int HF_OUTLINE = 2;
    static final int HF_CLICKBOX = 4;
    static final int HF_TILE = 8;
    private final TileObject tileObject;
    private final ObjectComposition composition;
    private final String name;
    @Nullable
    private final Color borderColor;
    @Nullable
    private final Color fillColor;
    private final byte highlightFlags;

    public TileObject getTileObject() {
        return this.tileObject;
    }

    public ObjectComposition getComposition() {
        return this.composition;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public Color getBorderColor() {
        return this.borderColor;
    }

    @Nullable
    public Color getFillColor() {
        return this.fillColor;
    }

    public byte getHighlightFlags() {
        return this.highlightFlags;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ColorTileObject)) {
            return false;
        }
        ColorTileObject other = (ColorTileObject)o;
        if (this.getHighlightFlags() != other.getHighlightFlags()) {
            return false;
        }
        TileObject this$tileObject = this.getTileObject();
        TileObject other$tileObject = other.getTileObject();
        if (this$tileObject == null ? other$tileObject != null : !this$tileObject.equals(other$tileObject)) {
            return false;
        }
        ObjectComposition this$composition = this.getComposition();
        ObjectComposition other$composition = other.getComposition();
        if (this$composition == null ? other$composition != null : !this$composition.equals(other$composition)) {
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

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getHighlightFlags();
        TileObject $tileObject = this.getTileObject();
        result = result * 59 + ($tileObject == null ? 43 : $tileObject.hashCode());
        ObjectComposition $composition = this.getComposition();
        result = result * 59 + ($composition == null ? 43 : $composition.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Color $borderColor = this.getBorderColor();
        result = result * 59 + ($borderColor == null ? 43 : ((Object)$borderColor).hashCode());
        Color $fillColor = this.getFillColor();
        result = result * 59 + ($fillColor == null ? 43 : ((Object)$fillColor).hashCode());
        return result;
    }

    public String toString() {
        return "ColorTileObject(tileObject=" + String.valueOf(this.getTileObject()) + ", composition=" + String.valueOf(this.getComposition()) + ", name=" + this.getName() + ", borderColor=" + String.valueOf(this.getBorderColor()) + ", fillColor=" + String.valueOf(this.getFillColor()) + ", highlightFlags=" + this.getHighlightFlags() + ")";
    }

    public ColorTileObject(TileObject tileObject, ObjectComposition composition, String name, @Nullable Color borderColor, @Nullable Color fillColor, byte highlightFlags) {
        this.tileObject = tileObject;
        this.composition = composition;
        this.name = name;
        this.borderColor = borderColor;
        this.fillColor = fillColor;
        this.highlightFlags = highlightFlags;
    }
}

