/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.ui.overlay.components;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

public class ImageComponent
implements LayoutableRenderableEntity {
    private final BufferedImage image;
    private final Rectangle bounds = new Rectangle();
    private Point preferredLocation = new Point();

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.image == null) {
            return null;
        }
        graphics.drawImage((Image)this.image, this.preferredLocation.x, this.preferredLocation.y, null);
        Dimension dimension = new Dimension(this.image.getWidth(), this.image.getHeight());
        this.bounds.setLocation(this.preferredLocation);
        this.bounds.setSize(dimension);
        return dimension;
    }

    @Override
    public void setPreferredSize(Dimension dimension) {
    }

    public ImageComponent(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void setPreferredLocation(Point preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    @Override
    public Rectangle getBounds() {
        return this.bounds;
    }
}

