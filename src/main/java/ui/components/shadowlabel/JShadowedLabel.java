/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components.shadowlabel;

import java.awt.Color;
import java.awt.Point;
import javax.swing.JLabel;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabelUI;

public class JShadowedLabel
extends JLabel {
    private Color shadow = Color.BLACK;
    private Point shadowSize = new Point(1, 1);

    public JShadowedLabel() {
        this.setUI(new JShadowedLabelUI());
    }

    public JShadowedLabel(String str) {
        super(str);
        this.setUI(new JShadowedLabelUI());
    }

    public void setShadow(Color shadow) {
        this.shadow = shadow;
        this.repaint();
    }

    public void setShadowSize(Point newSize) {
        this.shadowSize = newSize;
        this.revalidate();
        this.repaint();
    }

    public Color getShadow() {
        return this.shadow;
    }

    public Point getShadowSize() {
        return this.shadowSize;
    }
}

