/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.screenmarkers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import net.runelite.client.ui.overlay.RenderableEntity;

class ScreenMarkerRenderable
implements RenderableEntity {
    private Dimension size;
    private int borderThickness;
    private Color color;
    private Color fill;
    private Stroke stroke;
    private String label;

    ScreenMarkerRenderable() {
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        int thickness = this.borderThickness;
        int width = this.size.width;
        int height = this.size.height;
        graphics.setColor(this.fill);
        graphics.fillRect(thickness, thickness, width - thickness * 2, height - thickness * 2);
        int offset = thickness / 2;
        graphics.setColor(this.color);
        graphics.setStroke(this.stroke);
        graphics.drawRect(offset, offset, width - thickness, height - thickness);
        if (!this.label.isEmpty()) {
            graphics.drawString(this.label, 0, 0);
        }
        return this.size;
    }

    Dimension getSize() {
        return this.size;
    }

    int getBorderThickness() {
        return this.borderThickness;
    }

    Color getColor() {
        return this.color;
    }

    Color getFill() {
        return this.fill;
    }

    Stroke getStroke() {
        return this.stroke;
    }

    String getLabel() {
        return this.label;
    }

    void setSize(Dimension size) {
        this.size = size;
    }

    void setBorderThickness(int borderThickness) {
        this.borderThickness = borderThickness;
    }

    void setColor(Color color) {
        this.color = color;
    }

    void setFill(Color fill) {
        this.fill = fill;
    }

    void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    void setLabel(String label) {
        this.label = label;
    }
}

