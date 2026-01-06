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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.JPanel;

public class ColorPanel
extends JPanel {
    private static final int SELECTOR_RADIUS = 7;
    private final int size;
    private final BufferedImage image;
    private Point targetPosition;
    private int selectedY;
    private boolean forceRedraw;
    private Consumer<Color> onColorChange;

    ColorPanel(int size) {
        this.size = size;
        this.image = new BufferedImage(size, size, 1);
        this.targetPosition = new Point(size, 0);
        this.setPreferredSize(new Dimension(size, size));
        this.addMouseMotionListener(new MouseMotionAdapter(){

            @Override
            public void mouseDragged(MouseEvent me) {
                ColorPanel.this.moveTarget(me.getX(), me.getY(), true);
            }
        });
        this.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseReleased(MouseEvent me) {
                ColorPanel.this.moveTarget(me.getX(), me.getY(), true);
            }

            @Override
            public void mousePressed(MouseEvent me) {
                ColorPanel.this.moveTarget(me.getX(), me.getY(), true);
            }
        });
    }

    void setBaseColor(int selectedY) {
        if (this.selectedY == selectedY) {
            return;
        }
        this.selectedY = selectedY;
        this.redrawGradient();
        if (this.onColorChange != null) {
            this.onColorChange.accept(this.colorAt(this.targetPosition.x, this.targetPosition.y));
        }
        this.paintImmediately(0, 0, this.size, this.size);
    }

    void moveToClosestColor(int y, Color color) {
        Point closest = this.closestPointToColor(color);
        if (this.selectedY == y && closest.x == this.targetPosition.x && closest.y == this.targetPosition.y) {
            return;
        }
        this.selectedY = y;
        this.redrawGradient();
        this.moveTarget(closest.x, closest.y, false);
    }

    private Point closestPointToColor(Color target) {
        float[] hsb = Color.RGBtoHSB(target.getRed(), target.getGreen(), target.getBlue(), null);
        int offSize = this.size - 1;
        return new Point((int)(hsb[1] * (float)offSize), offSize - (int)(hsb[2] * (float)offSize));
    }

    private void moveTarget(int x, int y, boolean shouldUpdate) {
        if (this.targetPosition.x == x && this.targetPosition.y == y && !this.forceRedraw) {
            return;
        }
        x = Ints.constrainToRange((int)x, (int)0, (int)(this.size - 1));
        y = Ints.constrainToRange((int)y, (int)0, (int)(this.size - 1));
        this.targetPosition = new Point(x, y);
        this.paintImmediately(0, 0, this.size, this.size);
        if (this.onColorChange != null && shouldUpdate) {
            this.onColorChange.accept(this.colorAt(x, y));
        }
        this.forceRedraw = false;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(this.image, 0, 0, null);
        int targetX = this.targetPosition.x - 3;
        int targetY = this.targetPosition.y - 3;
        g.setColor(Color.WHITE);
        g.fillOval(targetX, targetY, 7, 7);
        g.setColor(Color.BLACK);
        g.drawOval(targetX, targetY, 7, 7);
    }

    private void redrawGradient() {
        Color primaryRight = Color.getHSBColor(1.0f - (float)this.selectedY / (float)(this.size - 1), 1.0f, 1.0f);
        Graphics2D g = this.image.createGraphics();
        GradientPaint primary = new GradientPaint(0.0f, 0.0f, Color.WHITE, this.size - 1, 0.0f, primaryRight);
        GradientPaint shade = new GradientPaint(0.0f, 0.0f, new Color(0, 0, 0, 0), 0.0f, this.size - 1, Color.BLACK);
        g.setPaint(primary);
        g.fillRect(0, 0, this.size, this.size);
        g.setPaint(shade);
        g.fillRect(0, 0, this.size, this.size);
        g.dispose();
        this.forceRedraw = true;
    }

    private Color colorAt(int x, int y) {
        x = Ints.constrainToRange((int)x, (int)0, (int)(this.size - 1));
        y = Ints.constrainToRange((int)y, (int)0, (int)(this.size - 1));
        return new Color(this.image.getRGB(x, y));
    }

    public void setOnColorChange(Consumer<Color> onColorChange) {
        this.onColorChange = onColorChange;
    }
}

