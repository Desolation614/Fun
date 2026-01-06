/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 */
package net.runelite.client.ui.components.colorpicker;

import com.google.common.primitives.Ints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Consumer;
import javax.swing.JPanel;

public class HuePanel
extends JPanel {
    private static final int PANEL_WIDTH = 15;
    private static final int KNOB_HEIGHT = 4;
    private final int height;
    private int selectedY;
    private Consumer<Integer> onColorChange;

    HuePanel(int height) {
        this.height = height;
        this.setPreferredSize(new Dimension(15, height));
        this.addMouseMotionListener(new MouseMotionAdapter(){

            @Override
            public void mouseDragged(MouseEvent me) {
                HuePanel.this.moveSelector(me.getY());
            }
        });
        this.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseReleased(MouseEvent me) {
                HuePanel.this.moveSelector(me.getY());
            }

            @Override
            public void mousePressed(MouseEvent me) {
                HuePanel.this.moveSelector(me.getY());
            }
        });
    }

    public void select(Color color) {
        this.selectedY = this.closestYToColor(color);
        this.paintImmediately(0, 0, 15, this.height);
    }

    private void moveSelector(int y) {
        if ((y = Ints.constrainToRange((int)y, (int)0, (int)(this.height - 1))) == this.selectedY) {
            return;
        }
        this.selectedY = y;
        this.paintImmediately(0, 0, 15, this.height);
        if (this.onColorChange != null) {
            this.onColorChange.accept(y);
        }
    }

    private int closestYToColor(Color target) {
        float[] hsb = Color.RGBtoHSB(target.getRed(), target.getGreen(), target.getBlue(), null);
        float hue = hsb[0];
        int offHeight = this.height - 1;
        return Math.round((float)offHeight - hue * (float)offHeight);
    }

    @Override
    public void paint(Graphics g) {
        for (int y = 0; y < this.height; ++y) {
            g.setColor(this.colorAt(y));
            g.fillRect(0, y, 15, 1);
        }
        int halfKnob = 2;
        g.setColor(Color.WHITE);
        g.fillRect(0, this.selectedY - 1, 15, 4);
        g.setColor(Color.BLACK);
        g.drawLine(0, this.selectedY - 2, 15, this.selectedY - 2);
        g.drawLine(0, this.selectedY + 2, 15, this.selectedY + 2);
    }

    private Color colorAt(int y) {
        return Color.getHSBColor(1.0f - (float)y / (float)(this.height - 1), 1.0f, 1.0f);
    }

    public int getSelectedY() {
        return this.selectedY;
    }

    public void setOnColorChange(Consumer<Integer> onColorChange) {
        this.onColorChange = onColorChange;
    }
}

