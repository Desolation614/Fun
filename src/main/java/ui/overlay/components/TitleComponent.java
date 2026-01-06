/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.overlay.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;

public class TitleComponent
implements LayoutableRenderableEntity {
    private String text;
    private Color color;
    private Point preferredLocation;
    private Dimension preferredSize;
    private final Rectangle bounds;

    @Override
    public Dimension render(Graphics2D graphics) {
        int baseX = this.preferredLocation.x;
        int baseY = this.preferredLocation.y;
        FontMetrics metrics = graphics.getFontMetrics();
        TextComponent titleComponent = new TextComponent();
        titleComponent.setText(this.text);
        titleComponent.setColor(this.color);
        titleComponent.setPosition(new Point(baseX + (this.preferredSize.width - metrics.stringWidth(this.text)) / 2, baseY + metrics.getHeight()));
        Dimension rendered = titleComponent.render(graphics);
        Dimension dimension = new Dimension(this.preferredSize.width, rendered.height);
        this.bounds.setLocation(this.preferredLocation);
        this.bounds.setSize(dimension);
        return dimension;
    }

    private static Color $default$color() {
        return Color.WHITE;
    }

    private static Point $default$preferredLocation() {
        return new Point();
    }

    private static Dimension $default$preferredSize() {
        return new Dimension(129, 0);
    }

    private static Rectangle $default$bounds() {
        return new Rectangle();
    }

    TitleComponent(String text, Color color, Point preferredLocation, Dimension preferredSize, Rectangle bounds) {
        this.text = text;
        this.color = color;
        this.preferredLocation = preferredLocation;
        this.preferredSize = preferredSize;
        this.bounds = bounds;
    }

    public static TitleComponentBuilder builder() {
        return new TitleComponentBuilder();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void setPreferredLocation(Point preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    @Override
    public Rectangle getBounds() {
        return this.bounds;
    }

    public static class TitleComponentBuilder {
        private String text;
        private boolean color$set;
        private Color color$value;
        private boolean preferredLocation$set;
        private Point preferredLocation$value;
        private boolean preferredSize$set;
        private Dimension preferredSize$value;
        private boolean bounds$set;
        private Rectangle bounds$value;

        TitleComponentBuilder() {
        }

        public TitleComponentBuilder text(String text) {
            this.text = text;
            return this;
        }

        public TitleComponentBuilder color(Color color) {
            this.color$value = color;
            this.color$set = true;
            return this;
        }

        public TitleComponentBuilder preferredLocation(Point preferredLocation) {
            this.preferredLocation$value = preferredLocation;
            this.preferredLocation$set = true;
            return this;
        }

        public TitleComponentBuilder preferredSize(Dimension preferredSize) {
            this.preferredSize$value = preferredSize;
            this.preferredSize$set = true;
            return this;
        }

        public TitleComponentBuilder bounds(Rectangle bounds) {
            this.bounds$value = bounds;
            this.bounds$set = true;
            return this;
        }

        public TitleComponent build() {
            Color color$value = this.color$value;
            if (!this.color$set) {
                color$value = TitleComponent.$default$color();
            }
            Point preferredLocation$value = this.preferredLocation$value;
            if (!this.preferredLocation$set) {
                preferredLocation$value = TitleComponent.$default$preferredLocation();
            }
            Dimension preferredSize$value = this.preferredSize$value;
            if (!this.preferredSize$set) {
                preferredSize$value = TitleComponent.$default$preferredSize();
            }
            Rectangle bounds$value = this.bounds$value;
            if (!this.bounds$set) {
                bounds$value = TitleComponent.$default$bounds();
            }
            return new TitleComponent(this.text, color$value, preferredLocation$value, preferredSize$value, bounds$value);
        }

        public String toString() {
            return "TitleComponent.TitleComponentBuilder(text=" + this.text + ", color$value=" + String.valueOf(this.color$value) + ", preferredLocation$value=" + String.valueOf(this.preferredLocation$value) + ", preferredSize$value=" + String.valueOf(this.preferredSize$value) + ", bounds$value=" + String.valueOf(this.bounds$value) + ")";
        }
    }
}

