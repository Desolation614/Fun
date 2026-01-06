/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.overlay.tooltip;

import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

public class Tooltip {
    private String text;
    private LayoutableRenderableEntity component;

    public Tooltip(String text) {
        this.text = text;
    }

    public Tooltip(LayoutableRenderableEntity component) {
        this.component = component;
    }

    public String getText() {
        return this.text;
    }

    public LayoutableRenderableEntity getComponent() {
        return this.component;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setComponent(LayoutableRenderableEntity component) {
        this.component = component;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Tooltip)) {
            return false;
        }
        Tooltip other = (Tooltip)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$text = this.getText();
        String other$text = other.getText();
        if (this$text == null ? other$text != null : !this$text.equals(other$text)) {
            return false;
        }
        LayoutableRenderableEntity this$component = this.getComponent();
        LayoutableRenderableEntity other$component = other.getComponent();
        return !(this$component == null ? other$component != null : !this$component.equals(other$component));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Tooltip;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $text = this.getText();
        result = result * 59 + ($text == null ? 43 : $text.hashCode());
        LayoutableRenderableEntity $component = this.getComponent();
        result = result * 59 + ($component == null ? 43 : $component.hashCode());
        return result;
    }

    public String toString() {
        return "Tooltip(text=" + this.getText() + ", component=" + String.valueOf(this.getComponent()) + ")";
    }
}

