/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.PanelComponent;

public abstract class OverlayPanel
extends Overlay {
    protected final PanelComponent panelComponent = new PanelComponent();
    private boolean clearChildren = true;
    private boolean dynamicFont = false;
    private Color preferredColor = null;

    protected OverlayPanel() {
        this.setResizable(true);
    }

    protected OverlayPanel(Plugin plugin) {
        super(plugin);
        this.setResizable(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Dimension render(Graphics2D graphics) {
        Dimension dimension;
        Dimension oldSize = this.panelComponent.getPreferredSize();
        if (this.getPreferredSize() != null) {
            this.panelComponent.setPreferredSize(this.getPreferredSize());
            if (this.dynamicFont) {
                if ((double)this.getPreferredSize().width >= 167.70000000000002) {
                    graphics.setFont(FontManager.getRunescapeBoldFont());
                } else if ((double)this.getPreferredSize().width <= 103.2) {
                    graphics.setFont(FontManager.getRunescapeSmallFont());
                }
            }
        }
        Color oldBackgroundColor = this.panelComponent.getBackgroundColor();
        if (this.getPreferredColor() != null && ComponentConstants.STANDARD_BACKGROUND_COLOR.equals(oldBackgroundColor)) {
            this.panelComponent.setBackgroundColor(this.getPreferredColor());
        }
        try {
            dimension = this.panelComponent.render(graphics);
        }
        finally {
            if (this.clearChildren) {
                this.panelComponent.getChildren().clear();
            }
        }
        this.panelComponent.setPreferredSize(oldSize);
        this.panelComponent.setBackgroundColor(oldBackgroundColor);
        return dimension;
    }

    public PanelComponent getPanelComponent() {
        return this.panelComponent;
    }

    public boolean isClearChildren() {
        return this.clearChildren;
    }

    public boolean isDynamicFont() {
        return this.dynamicFont;
    }

    public Color getPreferredColor() {
        return this.preferredColor;
    }

    public void setClearChildren(boolean clearChildren) {
        this.clearChildren = clearChildren;
    }

    public void setDynamicFont(boolean dynamicFont) {
        this.dynamicFont = dynamicFont;
    }

    public void setPreferredColor(Color preferredColor) {
        this.preferredColor = preferredColor;
    }
}

