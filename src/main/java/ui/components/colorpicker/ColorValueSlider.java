/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 */
package net.runelite.client.ui.components.colorpicker;

import com.google.common.primitives.Ints;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Consumer;
import javax.swing.JPanel;

public class ColorValueSlider
extends JPanel {
    static final int KNOB_WIDTH = 4;
    private static final int KNOB_HEIGHT = 14;
    private static final Color TRACK_COLOR = new Color(20, 20, 20);
    private static final Color KNOB_COLOR = new Color(150, 150, 150);
    private int value = 259;
    private Consumer<Integer> onValueChanged;

    ColorValueSlider() {
        this.addMouseMotionListener(new MouseMotionAdapter(){

            @Override
            public void mouseDragged(MouseEvent me) {
                ColorValueSlider.this.moveTarget(me.getX(), true);
            }
        });
        this.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseReleased(MouseEvent me) {
                ColorValueSlider.this.moveTarget(me.getX(), true);
            }

            @Override
            public void mousePressed(MouseEvent me) {
                ColorValueSlider.this.moveTarget(me.getX(), true);
            }
        });
    }

    public void setValue(int x) {
        this.moveTarget(x + 4, false);
    }

    private void moveTarget(int x, boolean shouldUpdate) {
        this.value = Ints.constrainToRange((int)x, (int)4, (int)259);
        this.paintImmediately(0, 0, this.getWidth(), this.getHeight());
        if (shouldUpdate && this.onValueChanged != null) {
            this.onValueChanged.accept(this.getValue());
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(TRACK_COLOR);
        g.fillRect(0, this.getHeight() / 2 - 2, 263, 5);
        g.setColor(KNOB_COLOR);
        g.fillRect(this.value - 2, this.getHeight() / 2 - 7, 4, 14);
    }

    int getValue() {
        return this.value - 4;
    }

    public void setOnValueChanged(Consumer<Integer> onValueChanged) {
        this.onValueChanged = onValueChanged;
    }
}

