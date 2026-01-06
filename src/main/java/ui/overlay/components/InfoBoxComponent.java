/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 */
package net.runelite.client.ui.overlay.components;

import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.components.BackgroundComponent;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class InfoBoxComponent
implements LayoutableRenderableEntity {
    private static final int SEPARATOR = 3;
    private static final int DEFAULT_SIZE = 32;
    private String tooltip;
    private final Rectangle bounds = new Rectangle();
    private Point preferredLocation = new Point();
    private Dimension preferredSize = new Dimension(32, 32);
    private String text;
    private Color color = Color.WHITE;
    private Font font;
    private boolean outline;
    private Color backgroundColor = ComponentConstants.STANDARD_BACKGROUND_COLOR;
    private BufferedImage image;
    private InfoBox infoBox;

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.image == null) {
            return new Dimension();
        }
        graphics.setFont(this.getSize() < 32 ? FontManager.getRunescapeSmallFont() : this.font);
        int baseX = this.preferredLocation.x;
        int baseY = this.preferredLocation.y;
        FontMetrics metrics = graphics.getFontMetrics();
        int size = this.getSize();
        Rectangle bounds = new Rectangle(baseX, baseY, size, size);
        BackgroundComponent backgroundComponent = new BackgroundComponent();
        backgroundComponent.setBackgroundColor(this.backgroundColor);
        backgroundComponent.setRectangle(bounds);
        backgroundComponent.render(graphics);
        graphics.drawImage((Image)this.image, baseX + (size - this.image.getWidth(null)) / 2, baseY + (size - this.image.getHeight(null)) / 2, null);
        if (!Strings.isNullOrEmpty((String)this.text)) {
            TextComponent textComponent = new TextComponent();
            textComponent.setColor(this.color);
            textComponent.setOutline(this.outline);
            textComponent.setText(this.text);
            textComponent.setPosition(new Point(baseX + (size - metrics.stringWidth(this.text)) / 2, baseY + size - 3));
            textComponent.render(graphics);
        }
        this.bounds.setBounds(bounds);
        return bounds.getSize();
    }

    private int getSize() {
        return Math.max(this.preferredSize.width, this.preferredSize.height);
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public void setPreferredLocation(Point preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setInfoBox(InfoBox infoBox) {
        this.infoBox = infoBox;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    @Override
    public Rectangle getBounds() {
        return this.bounds;
    }

    public InfoBox getInfoBox() {
        return this.infoBox;
    }
}

