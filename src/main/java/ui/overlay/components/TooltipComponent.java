/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.runelite.api.IndexedSprite
 */
package net.runelite.client.ui.overlay.components;

import com.google.common.annotations.VisibleForTesting;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.regex.Pattern;
import net.runelite.api.IndexedSprite;
import net.runelite.client.ui.overlay.components.BackgroundComponent;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;

public class TooltipComponent
implements LayoutableRenderableEntity {
    private static final Pattern BR = Pattern.compile("</?br>");
    private static final int OFFSET = 4;
    private static final int MOD_ICON_WIDTH = 13;
    private String text;
    private Color backgroundColor = ComponentConstants.STANDARD_BACKGROUND_COLOR;
    private Point position = new Point();
    private IndexedSprite[] modIcons;

    @Override
    public Dimension render(Graphics2D graphics) {
        String[] lines;
        FontMetrics metrics = graphics.getFontMetrics();
        int textDescent = metrics.getDescent();
        int textHeight = metrics.getHeight();
        int tooltipWidth = 0;
        int tooltipHeight = 0;
        for (String line : lines = BR.split(this.text)) {
            int textWidth = TooltipComponent.calculateTextWidth(metrics, line);
            if (textWidth > tooltipWidth) {
                tooltipWidth = textWidth;
            }
            tooltipHeight += textHeight;
        }
        int x = this.position.x;
        int y = this.position.y;
        Rectangle tooltipBackground = new Rectangle(x, y, tooltipWidth + 8, tooltipHeight + 8);
        BackgroundComponent backgroundComponent = new BackgroundComponent();
        backgroundComponent.setBackgroundColor(this.backgroundColor);
        backgroundComponent.setRectangle(tooltipBackground);
        backgroundComponent.render(graphics);
        graphics.setColor(Color.WHITE);
        int textX = x + 4;
        int textY = y + 4;
        Color nextColor = Color.WHITE;
        Color nextShadowColor = Color.BLACK;
        for (int i = 0; i < lines.length; ++i) {
            int lineX = textX;
            String line = lines[i];
            char[] chars = line.toCharArray();
            int begin = 0;
            boolean inTag = false;
            for (int j = 0; j < chars.length; ++j) {
                String argument;
                if (chars[j] == '<') {
                    TextComponent textComponent = new TextComponent();
                    textComponent.setColor(nextColor);
                    textComponent.setShadowColor(nextShadowColor);
                    String text = line.substring(begin, j);
                    textComponent.setText(text);
                    textComponent.setPosition(new Point(lineX, textY + (i + 1) * textHeight - textDescent));
                    textComponent.render(graphics);
                    lineX += metrics.stringWidth(text);
                    begin = j;
                    inTag = true;
                    continue;
                }
                if (chars[j] != '>' || !inTag) continue;
                String subLine = line.substring(begin + 1, j);
                if (subLine.startsWith("col=")) {
                    argument = subLine.substring(4);
                    nextColor = Color.decode("#" + argument);
                } else if (subLine.equals("/col")) {
                    nextColor = Color.WHITE;
                } else if (subLine.startsWith("shad=")) {
                    argument = subLine.substring(5);
                    nextShadowColor = Color.decode("#" + argument);
                } else if (subLine.equals("/shad")) {
                    nextShadowColor = Color.BLACK;
                } else if (subLine.startsWith("img=")) {
                    if (this.modIcons != null) {
                        argument = subLine.substring(4);
                        int iconId = Integer.parseInt(argument);
                        IndexedSprite modIcon = this.modIcons[iconId];
                        this.renderModIcon(graphics, lineX, textY + i * textHeight - textDescent, modIcon);
                        lineX += modIcon.getWidth();
                    }
                } else if (!subLine.startsWith("title") && !subLine.startsWith("/title")) {
                    TextComponent textComponent = new TextComponent();
                    textComponent.setColor(nextColor);
                    String text = line.substring(begin, j + 1);
                    textComponent.setText(text);
                    textComponent.setPosition(new Point(lineX, textY + (i + 1) * textHeight - textDescent));
                    textComponent.render(graphics);
                    lineX += metrics.stringWidth(text);
                }
                begin = j + 1;
                inTag = false;
            }
            TextComponent textComponent = new TextComponent();
            textComponent.setColor(nextColor);
            textComponent.setText(line.substring(begin));
            textComponent.setPosition(new Point(lineX, textY + (i + 1) * textHeight - textDescent));
            textComponent.render(graphics);
        }
        return new Dimension(tooltipWidth + 8, tooltipHeight + 8);
    }

    @VisibleForTesting
    static int calculateTextWidth(FontMetrics metrics, String line) {
        char[] chars = line.toCharArray();
        int textWidth = 0;
        int begin = 0;
        boolean inTag = false;
        for (int j = 0; j < chars.length; ++j) {
            if (chars[j] == '<') {
                textWidth += metrics.stringWidth(line.substring(begin, j));
                begin = j;
                inTag = true;
                continue;
            }
            if (chars[j] != '>' || !inTag) continue;
            String subLine = line.substring(begin + 1, j);
            if (subLine.startsWith("img=")) {
                textWidth += 13;
            } else if (!(subLine.startsWith("col=") || subLine.startsWith("/col") || subLine.startsWith("shad=") || subLine.startsWith("/shad") || subLine.startsWith("title") || subLine.startsWith("/title"))) {
                textWidth += metrics.stringWidth(line.substring(begin, j + 1));
            }
            begin = j + 1;
            inTag = false;
        }
        return textWidth += metrics.stringWidth(line.substring(begin));
    }

    private void renderModIcon(Graphics2D graphics, int x, int y, IndexedSprite modIcon) {
        int sourceOffset = 0;
        for (int y2 = 0; y2 < modIcon.getHeight(); ++y2) {
            for (int x2 = 0; x2 < modIcon.getWidth(); ++x2) {
                int index = modIcon.getPixels()[sourceOffset++] & 0xFF;
                if (index == 0) continue;
                graphics.setColor(new Color(modIcon.getPalette()[index]));
                graphics.drawLine(x + x2, y + y2, x + x2, y + y2);
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    @Override
    public void setPreferredLocation(Point position) {
        this.position = position;
    }

    @Override
    public void setPreferredSize(Dimension dimension) {
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setModIcons(IndexedSprite[] modIcons) {
        this.modIcons = modIcons;
    }
}

