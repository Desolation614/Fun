/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.overlay.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.runelite.client.ui.overlay.components.BackgroundComponent;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

public class PanelComponent
implements LayoutableRenderableEntity {
    private final Rectangle bounds = new Rectangle();
    private Point preferredLocation = new Point();
    private Dimension preferredSize = new Dimension(129, 0);
    private Color backgroundColor = ComponentConstants.STANDARD_BACKGROUND_COLOR;
    private final List<LayoutableRenderableEntity> children = new ArrayList<LayoutableRenderableEntity>();
    private ComponentOrientation orientation = ComponentOrientation.VERTICAL;
    private boolean wrap = false;
    private Rectangle border = new Rectangle(4, 4, 4, 4);
    private Point gap = new Point(0, 0);
    private final Dimension childDimensions = new Dimension();

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.children.isEmpty()) {
            return null;
        }
        Dimension dimension = new Dimension(this.border.x + this.childDimensions.width + this.border.width, this.border.y + this.childDimensions.height + this.border.height);
        if (this.backgroundColor != null) {
            BackgroundComponent backgroundComponent = new BackgroundComponent();
            backgroundComponent.setRectangle(new Rectangle(this.preferredLocation, dimension));
            backgroundComponent.setBackgroundColor(this.backgroundColor);
            backgroundComponent.render(graphics);
        }
        int baseX = this.preferredLocation.x + this.border.x;
        int baseY = this.preferredLocation.y + this.border.y;
        int width = 0;
        int height = 0;
        int x = baseX;
        int y = baseY;
        Dimension childPreferredSize = new Dimension(this.preferredSize.width - this.border.x - this.border.width, this.preferredSize.height - this.border.y - this.border.height);
        int totalHeight = 0;
        int totalWidth = 0;
        for (LayoutableRenderableEntity child : this.children) {
            if (!this.wrap) {
                switch (this.orientation) {
                    case VERTICAL: {
                        child.setPreferredSize(new Dimension(childPreferredSize.width, 0));
                        break;
                    }
                    case HORIZONTAL: {
                        child.setPreferredSize(new Dimension(0, childPreferredSize.height));
                    }
                }
            }
            child.setPreferredLocation(new Point(x, y));
            Dimension childDimension = child.render(graphics);
            switch (this.orientation) {
                case VERTICAL: {
                    y = baseY + (height += childDimension.height + this.gap.y);
                    width = Math.max(width, childDimension.width);
                    break;
                }
                case HORIZONTAL: {
                    x = baseX + (width += childDimension.width + this.gap.x);
                    height = Math.max(height, childDimension.height);
                }
            }
            totalWidth = Math.max(totalWidth, width);
            totalHeight = Math.max(totalHeight, height);
            if (!this.wrap) continue;
            switch (this.orientation) {
                case VERTICAL: {
                    if (childPreferredSize.height <= 0 || height < childPreferredSize.height) break;
                    height = 0;
                    y = baseY;
                    int diff = childDimension.width + this.gap.x;
                    x += diff;
                    width += diff;
                    break;
                }
                case HORIZONTAL: {
                    if (childPreferredSize.width <= 0 || width < childPreferredSize.width) break;
                    width = 0;
                    x = baseX;
                    int diff = childDimension.height + this.gap.y;
                    y += diff;
                    height += diff;
                }
            }
        }
        if (this.orientation == ComponentOrientation.HORIZONTAL) {
            totalWidth -= this.gap.x;
        } else {
            totalHeight -= this.gap.y;
        }
        this.childDimensions.setSize(totalWidth, totalHeight);
        this.bounds.setLocation(this.preferredLocation);
        this.bounds.setSize(dimension);
        return dimension;
    }

    @Override
    public Rectangle getBounds() {
        return this.bounds;
    }

    @Override
    public void setPreferredLocation(Point preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    public Dimension getPreferredSize() {
        return this.preferredSize;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public List<LayoutableRenderableEntity> getChildren() {
        return this.children;
    }

    public void setOrientation(ComponentOrientation orientation) {
        this.orientation = orientation;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public void setBorder(Rectangle border) {
        this.border = border;
    }

    public void setGap(Point gap) {
        this.gap = gap;
    }
}

