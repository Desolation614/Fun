/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.overlay.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import net.runelite.client.ui.overlay.RenderableEntity;
import net.runelite.client.ui.overlay.components.ComponentConstants;

public class BackgroundComponent
implements RenderableEntity {
    private static final int BORDER_OFFSET = 2;
    private static final float COLOR_OFFSET = 0.2f;
    private static final float OUTER_COLOR_OFFSET = 0.8f;
    private static final float INNER_COLOR_OFFSET = 1.2f;
    private static final float ALPHA_COLOR_OFFSET = 1.4f;
    private Color backgroundColor = ComponentConstants.STANDARD_BACKGROUND_COLOR;
    private Rectangle rectangle = new Rectangle();
    private boolean fill = true;

    @Override
    public Dimension render(Graphics2D graphics) {
        Color outsideStrokeColor = new Color((int)((float)this.backgroundColor.getRed() * 0.8f), (int)((float)this.backgroundColor.getGreen() * 0.8f), (int)((float)this.backgroundColor.getBlue() * 0.8f), Math.min(255, (int)((float)this.backgroundColor.getAlpha() * 1.4f)));
        Color insideStrokeColor = new Color(Math.min(255, (int)((float)this.backgroundColor.getRed() * 1.2f)), Math.min(255, (int)((float)this.backgroundColor.getGreen() * 1.2f)), Math.min(255, (int)((float)this.backgroundColor.getBlue() * 1.2f)), Math.min(255, (int)((float)this.backgroundColor.getAlpha() * 1.4f)));
        if (this.fill) {
            graphics.setColor(this.backgroundColor);
            graphics.fill(this.rectangle);
        }
        Rectangle outsideStroke = new Rectangle();
        outsideStroke.setLocation(this.rectangle.x, this.rectangle.y);
        outsideStroke.setSize(this.rectangle.width - 1, this.rectangle.height - 1);
        graphics.setColor(outsideStrokeColor);
        graphics.draw(outsideStroke);
        Rectangle insideStroke = new Rectangle();
        insideStroke.setLocation(this.rectangle.x + 1, this.rectangle.y + 1);
        insideStroke.setSize(this.rectangle.width - 2 - 1, this.rectangle.height - 2 - 1);
        graphics.setColor(insideStrokeColor);
        graphics.draw(insideStroke);
        return new Dimension(this.rectangle.getSize());
    }

    public BackgroundComponent() {
    }

    public BackgroundComponent(Color backgroundColor, Rectangle rectangle, boolean fill) {
        this.backgroundColor = backgroundColor;
        this.rectangle = rectangle;
        this.fill = fill;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }
}

