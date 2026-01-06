/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Point
 */
package net.runelite.client.ui.overlay.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.RenderableEntity;

public class ProgressPieComponent
implements RenderableEntity {
    private int diameter = 25;
    private Color borderColor = Color.WHITE;
    private Color fill = Color.WHITE;
    private Stroke stroke = new BasicStroke(1.0f);
    private double progress;
    private Point position;

    @Override
    public Dimension render(Graphics2D graphics) {
        Arc2D.Float arc = new Arc2D.Float(2);
        arc.setAngleStart(90.0);
        arc.setAngleExtent(this.progress * 360.0);
        arc.setFrame(this.position.getX() - this.diameter / 2, this.position.getY() - this.diameter / 2, this.diameter, this.diameter);
        graphics.setColor(this.fill);
        graphics.fill(arc);
        graphics.setStroke(this.stroke);
        graphics.setColor(this.borderColor);
        graphics.drawOval(this.position.getX() - this.diameter / 2, this.position.getY() - this.diameter / 2, this.diameter, this.diameter);
        return new Dimension(this.diameter, this.diameter);
    }

    public void setBorder(Color border, int size) {
        this.borderColor = border;
        this.stroke = new BasicStroke(size);
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}

