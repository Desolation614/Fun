/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class ThinProgressBar
extends JPanel {
    private int maximumValue = 1;
    private int value;

    public ThinProgressBar() {
        this.setForeground(Color.GREEN);
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        this.setMinimumSize(new Dimension(0, 4));
        this.setPreferredSize(new Dimension(0, 4));
        this.setSize(new Dimension(0, 4));
        this.setOpaque(true);
    }

    public double getPercentage() {
        return this.value * 100 / this.maximumValue;
    }

    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        this.setBackground(color.darker().darker());
    }

    public void setMaximumValue(int maximumValue) {
        if (maximumValue < 1) {
            maximumValue = 1;
        }
        this.maximumValue = maximumValue;
        this.repaint();
    }

    public void setValue(int value) {
        this.value = value;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = this.getWidth();
        int h = this.getHeight();
        int div = this.value * w / this.maximumValue;
        g.setColor(this.getBackground());
        g.fillRect(div, 0, w, h);
        g.setColor(this.getForeground());
        g.fillRect(0, 0, div, h);
    }

    public int getMaximumValue() {
        return this.maximumValue;
    }

    public int getValue() {
        return this.value;
    }
}

