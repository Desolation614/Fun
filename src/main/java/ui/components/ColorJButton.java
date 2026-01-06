/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;

public class ColorJButton
extends JButton {
    private static final int ALPHA_TEXT_CUTOFF = 120;
    private static final int CHECKER_SIZE = 10;
    private Color color;

    public ColorJButton(String text, Color color) {
        super(text);
        this.setContentAreaFilled(false);
        this.setColor(color);
    }

    public void setColor(Color color) {
        this.color = color;
        double lum = (0.299 * (double)color.getRed() + 0.587 * (double)color.getGreen() + 0.114 * (double)color.getBlue()) / 255.0;
        Color textColor = lum > 0.5 || color.getAlpha() < 120 ? Color.BLACK : Color.WHITE;
        this.setForeground(textColor);
    }

    @Override
    public void paint(Graphics g) {
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
        super.paint(g);
    }

    public Color getColor() {
        return this.color;
    }
}

