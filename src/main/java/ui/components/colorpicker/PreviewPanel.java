/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components.colorpicker;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

class PreviewPanel
extends JPanel {
    private static final int CHECKER_SIZE = 10;
    private Color color;

    PreviewPanel() {
    }

    void setColor(Color c) {
        this.color = c;
        this.paintImmediately(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (this.color.getAlpha() != 255) {
            for (int x = 0; x < this.getWidth(); x += 10) {
                for (int y = 0; y < this.getHeight(); y += 10) {
                    int val = (x / 10 + y / 10) % 2;
                    g.setColor(val == 0 ? Color.LIGHT_GRAY : Color.WHITE);
                    g.fillRect(x, y, 10, 10);
                }
            }
        }
        g.setColor(this.color);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    public Color getColor() {
        return this.color;
    }
}

