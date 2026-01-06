/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  net.runelite.api.NPC
 */
package net.runelite.client.game.npcoverlay;

import java.awt.Color;
import java.util.function.Predicate;
import lombok.NonNull;
import net.runelite.api.NPC;

public final class HighlightedNpc {
    @NonNull
    private final NPC npc;
    @NonNull
    private final Color highlightColor;
    private final Color fillColor;
    private final boolean hull;
    private final boolean tile;
    private final boolean trueTile;
    private final boolean swTile;
    private final boolean swTrueTile;
    private final boolean outline;
    private final boolean name;
    private final boolean nameOnMinimap;
    private final float borderWidth;
    private final int outlineFeather;
    private final Predicate<NPC> render;

    private static Color $default$fillColor() {
        return new Color(0, 0, 0, 50);
    }

    private static float $default$borderWidth() {
        return 2.0f;
    }

    HighlightedNpc(@NonNull NPC npc, @NonNull Color highlightColor, Color fillColor, boolean hull, boolean tile, boolean trueTile, boolean swTile, boolean swTrueTile, boolean outline, boolean name, boolean nameOnMinimap, float borderWidth, int outlineFeather, Predicate<NPC> render) {
        if (npc == null) {
            throw new NullPointerException("npc is marked non-null but is null");
        }
        if (highlightColor == null) {
            throw new NullPointerException("highlightColor is marked non-null but is null");
        }
        this.npc = npc;
        this.highlightColor = highlightColor;
        this.fillColor = fillColor;
        this.hull = hull;
        this.tile = tile;
        this.trueTile = trueTile;
        this.swTile = swTile;
        this.swTrueTile = swTrueTile;
        this.outline = outline;
        this.name = name;
        this.nameOnMinimap = nameOnMinimap;
        this.borderWidth = borderWidth;
        this.outlineFeather = outlineFeather;
        this.render = render;
    }

    public static HighlightedNpcBuilder builder() {
        return new HighlightedNpcBuilder();
    }

    @NonNull
    public NPC getNpc() {
        return this.npc;
    }

    @NonNull
    public Color getHighlightColor() {
        return this.highlightColor;
    }

    public Color getFillColor() {
        return this.fillColor;
    }

    public boolean isHull() {
        return this.hull;
    }

    public boolean isTile() {
        return this.tile;
    }

    public boolean isTrueTile() {
        return this.trueTile;
    }

    public boolean isSwTile() {
        return this.swTile;
    }

    public boolean isSwTrueTile() {
        return this.swTrueTile;
    }

    public boolean isOutline() {
        return this.outline;
    }

    public boolean isName() {
        return this.name;
    }

    public boolean isNameOnMinimap() {
        return this.nameOnMinimap;
    }

    public float getBorderWidth() {
        return this.borderWidth;
    }

    public int getOutlineFeather() {
        return this.outlineFeather;
    }

    public Predicate<NPC> getRender() {
        return this.render;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HighlightedNpc)) {
            return false;
        }
        HighlightedNpc other = (HighlightedNpc)o;
        if (this.isHull() != other.isHull()) {
            return false;
        }
        if (this.isTile() != other.isTile()) {
            return false;
        }
        if (this.isTrueTile() != other.isTrueTile()) {
            return false;
        }
        if (this.isSwTile() != other.isSwTile()) {
            return false;
        }
        if (this.isSwTrueTile() != other.isSwTrueTile()) {
            return false;
        }
        if (this.isOutline() != other.isOutline()) {
            return false;
        }
        if (this.isName() != other.isName()) {
            return false;
        }
        if (this.isNameOnMinimap() != other.isNameOnMinimap()) {
            return false;
        }
        if (Float.compare(this.getBorderWidth(), other.getBorderWidth()) != 0) {
            return false;
        }
        if (this.getOutlineFeather() != other.getOutlineFeather()) {
            return false;
        }
        NPC this$npc = this.getNpc();
        NPC other$npc = other.getNpc();
        if (this$npc == null ? other$npc != null : !this$npc.equals(other$npc)) {
            return false;
        }
        Color this$highlightColor = this.getHighlightColor();
        Color other$highlightColor = other.getHighlightColor();
        if (this$highlightColor == null ? other$highlightColor != null : !((Object)this$highlightColor).equals(other$highlightColor)) {
            return false;
        }
        Color this$fillColor = this.getFillColor();
        Color other$fillColor = other.getFillColor();
        if (this$fillColor == null ? other$fillColor != null : !((Object)this$fillColor).equals(other$fillColor)) {
            return false;
        }
        Predicate<NPC> this$render = this.getRender();
        Predicate<NPC> other$render = other.getRender();
        return !(this$render == null ? other$render != null : !this$render.equals(other$render));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isHull() ? 79 : 97);
        result = result * 59 + (this.isTile() ? 79 : 97);
        result = result * 59 + (this.isTrueTile() ? 79 : 97);
        result = result * 59 + (this.isSwTile() ? 79 : 97);
        result = result * 59 + (this.isSwTrueTile() ? 79 : 97);
        result = result * 59 + (this.isOutline() ? 79 : 97);
        result = result * 59 + (this.isName() ? 79 : 97);
        result = result * 59 + (this.isNameOnMinimap() ? 79 : 97);
        result = result * 59 + Float.floatToIntBits(this.getBorderWidth());
        result = result * 59 + this.getOutlineFeather();
        NPC $npc = this.getNpc();
        result = result * 59 + ($npc == null ? 43 : $npc.hashCode());
        Color $highlightColor = this.getHighlightColor();
        result = result * 59 + ($highlightColor == null ? 43 : ((Object)$highlightColor).hashCode());
        Color $fillColor = this.getFillColor();
        result = result * 59 + ($fillColor == null ? 43 : ((Object)$fillColor).hashCode());
        Predicate<NPC> $render = this.getRender();
        result = result * 59 + ($render == null ? 43 : $render.hashCode());
        return result;
    }

    public String toString() {
        return "HighlightedNpc(npc=" + String.valueOf(this.getNpc()) + ", highlightColor=" + String.valueOf(this.getHighlightColor()) + ", fillColor=" + String.valueOf(this.getFillColor()) + ", hull=" + this.isHull() + ", tile=" + this.isTile() + ", trueTile=" + this.isTrueTile() + ", swTile=" + this.isSwTile() + ", swTrueTile=" + this.isSwTrueTile() + ", outline=" + this.isOutline() + ", name=" + this.isName() + ", nameOnMinimap=" + this.isNameOnMinimap() + ", borderWidth=" + this.getBorderWidth() + ", outlineFeather=" + this.getOutlineFeather() + ", render=" + String.valueOf(this.getRender()) + ")";
    }

    public static class HighlightedNpcBuilder {
        private NPC npc;
        private Color highlightColor;
        private boolean fillColor$set;
        private Color fillColor$value;
        private boolean hull;
        private boolean tile;
        private boolean trueTile;
        private boolean swTile;
        private boolean swTrueTile;
        private boolean outline;
        private boolean name;
        private boolean nameOnMinimap;
        private boolean borderWidth$set;
        private float borderWidth$value;
        private int outlineFeather;
        private Predicate<NPC> render;

        HighlightedNpcBuilder() {
        }

        public HighlightedNpcBuilder npc(@NonNull NPC npc) {
            if (npc == null) {
                throw new NullPointerException("npc is marked non-null but is null");
            }
            this.npc = npc;
            return this;
        }

        public HighlightedNpcBuilder highlightColor(@NonNull Color highlightColor) {
            if (highlightColor == null) {
                throw new NullPointerException("highlightColor is marked non-null but is null");
            }
            this.highlightColor = highlightColor;
            return this;
        }

        public HighlightedNpcBuilder fillColor(Color fillColor) {
            this.fillColor$value = fillColor;
            this.fillColor$set = true;
            return this;
        }

        public HighlightedNpcBuilder hull(boolean hull) {
            this.hull = hull;
            return this;
        }

        public HighlightedNpcBuilder tile(boolean tile) {
            this.tile = tile;
            return this;
        }

        public HighlightedNpcBuilder trueTile(boolean trueTile) {
            this.trueTile = trueTile;
            return this;
        }

        public HighlightedNpcBuilder swTile(boolean swTile) {
            this.swTile = swTile;
            return this;
        }

        public HighlightedNpcBuilder swTrueTile(boolean swTrueTile) {
            this.swTrueTile = swTrueTile;
            return this;
        }

        public HighlightedNpcBuilder outline(boolean outline) {
            this.outline = outline;
            return this;
        }

        public HighlightedNpcBuilder name(boolean name) {
            this.name = name;
            return this;
        }

        public HighlightedNpcBuilder nameOnMinimap(boolean nameOnMinimap) {
            this.nameOnMinimap = nameOnMinimap;
            return this;
        }

        public HighlightedNpcBuilder borderWidth(float borderWidth) {
            this.borderWidth$value = borderWidth;
            this.borderWidth$set = true;
            return this;
        }

        public HighlightedNpcBuilder outlineFeather(int outlineFeather) {
            this.outlineFeather = outlineFeather;
            return this;
        }

        public HighlightedNpcBuilder render(Predicate<NPC> render) {
            this.render = render;
            return this;
        }

        public HighlightedNpc build() {
            Color fillColor$value = this.fillColor$value;
            if (!this.fillColor$set) {
                fillColor$value = HighlightedNpc.$default$fillColor();
            }
            float borderWidth$value = this.borderWidth$value;
            if (!this.borderWidth$set) {
                borderWidth$value = HighlightedNpc.$default$borderWidth();
            }
            return new HighlightedNpc(this.npc, this.highlightColor, fillColor$value, this.hull, this.tile, this.trueTile, this.swTile, this.swTrueTile, this.outline, this.name, this.nameOnMinimap, borderWidth$value, this.outlineFeather, this.render);
        }

        public String toString() {
            return "HighlightedNpc.HighlightedNpcBuilder(npc=" + String.valueOf(this.npc) + ", highlightColor=" + String.valueOf(this.highlightColor) + ", fillColor$value=" + String.valueOf(this.fillColor$value) + ", hull=" + this.hull + ", tile=" + this.tile + ", trueTile=" + this.trueTile + ", swTile=" + this.swTile + ", swTrueTile=" + this.swTrueTile + ", outline=" + this.outline + ", name=" + this.name + ", nameOnMinimap=" + this.nameOnMinimap + ", borderWidth$value=" + this.borderWidth$value + ", outlineFeather=" + this.outlineFeather + ", render=" + String.valueOf(this.render) + ")";
        }
    }
}

