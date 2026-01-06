/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.components;

import java.awt.Color;
import javax.swing.JPanel;

public class DimmableJPanel
extends JPanel {
    private boolean dimmed = false;
    private Color dimmedForeground = null;
    private Color dimmedBackground = null;
    private Color undimmedForeground = null;
    private Color undimmedBackground = null;

    @Override
    public void setForeground(Color color) {
        this.undimmedForeground = color;
        this.dimmedForeground = color.darker();
        super.setForeground(color);
    }

    @Override
    public void setBackground(Color color) {
        this.undimmedBackground = color;
        this.dimmedBackground = color.darker();
        super.setBackground(color);
    }

    @Override
    public Color getForeground() {
        return this.dimmed ? this.dimmedForeground : this.undimmedForeground;
    }

    @Override
    public Color getBackground() {
        return this.dimmed ? this.dimmedBackground : this.undimmedBackground;
    }

    public void setDimmed(boolean dimmed) {
        this.dimmed = dimmed;
        if (dimmed) {
            super.setBackground(this.dimmedBackground);
            super.setForeground(this.dimmedForeground);
        } else {
            super.setBackground(this.undimmedBackground);
            super.setForeground(this.undimmedForeground);
        }
    }

    public boolean isDimmed() {
        return this.dimmed;
    }
}

