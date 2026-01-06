/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.overlay.components;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

public class SplitComponent
implements LayoutableRenderableEntity {
    private LayoutableRenderableEntity first;
    private LayoutableRenderableEntity second;
    private Point preferredLocation;
    private Dimension preferredSize;
    private ComponentOrientation orientation;
    private Point gap;
    private final Rectangle bounds;

    @Override
    public Dimension render(Graphics2D graphics) {
        int totalHeight;
        int totalWidth;
        this.first.setPreferredLocation(this.preferredLocation);
        this.first.setPreferredSize(this.preferredSize);
        Dimension firstDimension = this.first.render(graphics);
        int x = 0;
        int y = 0;
        if (this.orientation == ComponentOrientation.VERTICAL) {
            y = firstDimension.height + this.gap.y;
        } else {
            x = firstDimension.width + this.gap.x;
        }
        this.second.setPreferredLocation(new Point(x + this.preferredLocation.x, y + this.preferredLocation.y));
        this.second.setPreferredSize(new Dimension(this.preferredSize.width - x, this.preferredSize.height - y));
        Dimension secondDimension = this.second.render(graphics);
        if (this.orientation == ComponentOrientation.VERTICAL) {
            totalWidth = Math.max(firstDimension.width, secondDimension.width);
            totalHeight = y + secondDimension.height;
        } else {
            totalHeight = Math.max(firstDimension.height, secondDimension.height);
            totalWidth = x + secondDimension.width;
        }
        Dimension dimension = new Dimension(totalWidth, totalHeight);
        this.bounds.setLocation(this.preferredLocation);
        this.bounds.setSize(dimension);
        return dimension;
    }

    private static Point $default$preferredLocation() {
        return new Point();
    }

    private static Dimension $default$preferredSize() {
        return new Dimension(129, 0);
    }

    private static ComponentOrientation $default$orientation() {
        return ComponentOrientation.VERTICAL;
    }

    private static Point $default$gap() {
        return new Point(0, 0);
    }

    private static Rectangle $default$bounds() {
        return new Rectangle();
    }

    SplitComponent(LayoutableRenderableEntity first, LayoutableRenderableEntity second, Point preferredLocation, Dimension preferredSize, ComponentOrientation orientation, Point gap, Rectangle bounds) {
        this.first = first;
        this.second = second;
        this.preferredLocation = preferredLocation;
        this.preferredSize = preferredSize;
        this.orientation = orientation;
        this.gap = gap;
        this.bounds = bounds;
    }

    public static SplitComponentBuilder builder() {
        return new SplitComponentBuilder();
    }

    public void setFirst(LayoutableRenderableEntity first) {
        this.first = first;
    }

    public void setSecond(LayoutableRenderableEntity second) {
        this.second = second;
    }

    @Override
    public void setPreferredLocation(Point preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    public void setOrientation(ComponentOrientation orientation) {
        this.orientation = orientation;
    }

    public void setGap(Point gap) {
        this.gap = gap;
    }

    @Override
    public Rectangle getBounds() {
        return this.bounds;
    }

    public static class SplitComponentBuilder {
        private LayoutableRenderableEntity first;
        private LayoutableRenderableEntity second;
        private boolean preferredLocation$set;
        private Point preferredLocation$value;
        private boolean preferredSize$set;
        private Dimension preferredSize$value;
        private boolean orientation$set;
        private ComponentOrientation orientation$value;
        private boolean gap$set;
        private Point gap$value;
        private boolean bounds$set;
        private Rectangle bounds$value;

        SplitComponentBuilder() {
        }

        public SplitComponentBuilder first(LayoutableRenderableEntity first) {
            this.first = first;
            return this;
        }

        public SplitComponentBuilder second(LayoutableRenderableEntity second) {
            this.second = second;
            return this;
        }

        public SplitComponentBuilder preferredLocation(Point preferredLocation) {
            this.preferredLocation$value = preferredLocation;
            this.preferredLocation$set = true;
            return this;
        }

        public SplitComponentBuilder preferredSize(Dimension preferredSize) {
            this.preferredSize$value = preferredSize;
            this.preferredSize$set = true;
            return this;
        }

        public SplitComponentBuilder orientation(ComponentOrientation orientation) {
            this.orientation$value = orientation;
            this.orientation$set = true;
            return this;
        }

        public SplitComponentBuilder gap(Point gap) {
            this.gap$value = gap;
            this.gap$set = true;
            return this;
        }

        public SplitComponentBuilder bounds(Rectangle bounds) {
            this.bounds$value = bounds;
            this.bounds$set = true;
            return this;
        }

        public SplitComponent build() {
            Point preferredLocation$value = this.preferredLocation$value;
            if (!this.preferredLocation$set) {
                preferredLocation$value = SplitComponent.$default$preferredLocation();
            }
            Dimension preferredSize$value = this.preferredSize$value;
            if (!this.preferredSize$set) {
                preferredSize$value = SplitComponent.$default$preferredSize();
            }
            ComponentOrientation orientation$value = this.orientation$value;
            if (!this.orientation$set) {
                orientation$value = SplitComponent.$default$orientation();
            }
            Point gap$value = this.gap$value;
            if (!this.gap$set) {
                gap$value = SplitComponent.$default$gap();
            }
            Rectangle bounds$value = this.bounds$value;
            if (!this.bounds$set) {
                bounds$value = SplitComponent.$default$bounds();
            }
            return new SplitComponent(this.first, this.second, preferredLocation$value, preferredSize$value, orientation$value, gap$value, bounds$value);
        }

        public String toString() {
            return "SplitComponent.SplitComponentBuilder(first=" + String.valueOf(this.first) + ", second=" + String.valueOf(this.second) + ", preferredLocation$value=" + String.valueOf(this.preferredLocation$value) + ", preferredSize$value=" + String.valueOf(this.preferredSize$value) + ", orientation$value=" + String.valueOf((Object)this.orientation$value) + ", gap$value=" + String.valueOf(this.gap$value) + ", bounds$value=" + String.valueOf(this.bounds$value) + ")";
        }
    }
}

