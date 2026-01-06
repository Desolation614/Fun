/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.screenmarkers;

import java.awt.Color;

public class ScreenMarker {
    private long id;
    private String name;
    private int borderThickness;
    private Color color;
    private Color fill;
    private boolean visible;
    private boolean labelled;

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getBorderThickness() {
        return this.borderThickness;
    }

    public Color getColor() {
        return this.color;
    }

    public Color getFill() {
        return this.fill;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean isLabelled() {
        return this.labelled;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBorderThickness(int borderThickness) {
        this.borderThickness = borderThickness;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setLabelled(boolean labelled) {
        this.labelled = labelled;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ScreenMarker)) {
            return false;
        }
        ScreenMarker other = (ScreenMarker)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        if (this.getBorderThickness() != other.getBorderThickness()) {
            return false;
        }
        if (this.isVisible() != other.isVisible()) {
            return false;
        }
        if (this.isLabelled() != other.isLabelled()) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        Color this$color = this.getColor();
        Color other$color = other.getColor();
        if (this$color == null ? other$color != null : !((Object)this$color).equals(other$color)) {
            return false;
        }
        Color this$fill = this.getFill();
        Color other$fill = other.getFill();
        return !(this$fill == null ? other$fill != null : !((Object)this$fill).equals(other$fill));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ScreenMarker;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $id = this.getId();
        result = result * 59 + (int)($id >>> 32 ^ $id);
        result = result * 59 + this.getBorderThickness();
        result = result * 59 + (this.isVisible() ? 79 : 97);
        result = result * 59 + (this.isLabelled() ? 79 : 97);
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Color $color = this.getColor();
        result = result * 59 + ($color == null ? 43 : ((Object)$color).hashCode());
        Color $fill = this.getFill();
        result = result * 59 + ($fill == null ? 43 : ((Object)$fill).hashCode());
        return result;
    }

    public String toString() {
        return "ScreenMarker(id=" + this.getId() + ", name=" + this.getName() + ", borderThickness=" + this.getBorderThickness() + ", color=" + String.valueOf(this.getColor()) + ", fill=" + String.valueOf(this.getFill()) + ", visible=" + this.isVisible() + ", labelled=" + this.isLabelled() + ")";
    }

    public ScreenMarker() {
    }

    public ScreenMarker(long id, String name, int borderThickness, Color color, Color fill, boolean visible, boolean labelled) {
        this.id = id;
        this.name = name;
        this.borderThickness = borderThickness;
        this.color = color;
        this.fill = fill;
        this.visible = visible;
        this.labelled = labelled;
    }
}

