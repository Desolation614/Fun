/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Strings
 */
package net.runelite.client.ui.overlay.components;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.Text;

public class LineComponent
implements LayoutableRenderableEntity {
    private String left;
    private String right;
    private Color leftColor;
    private Color rightColor;
    private Font leftFont;
    private Font rightFont;
    private Point preferredLocation;
    private Dimension preferredSize;
    private final Rectangle bounds;

    @Override
    public Dimension render(Graphics2D graphics) {
        String left = (String)MoreObjects.firstNonNull((Object)this.left, (Object)"");
        String right = (String)MoreObjects.firstNonNull((Object)this.right, (Object)"");
        Font leftFont = (Font)MoreObjects.firstNonNull((Object)this.leftFont, (Object)graphics.getFont());
        Font rightFont = (Font)MoreObjects.firstNonNull((Object)this.rightFont, (Object)graphics.getFont());
        FontMetrics lfm = graphics.getFontMetrics(leftFont);
        FontMetrics rfm = graphics.getFontMetrics(rightFont);
        int fmHeight = Math.max(lfm.getHeight(), rfm.getHeight());
        int baseX = this.preferredLocation.x;
        int baseY = this.preferredLocation.y + fmHeight;
        int x = baseX;
        int y = baseY;
        int leftFullWidth = LineComponent.getLineWidth(left, lfm);
        int rightFullWidth = LineComponent.getLineWidth(right, rfm);
        TextComponent textComponent = new TextComponent();
        if (this.preferredSize.width < leftFullWidth + rightFullWidth) {
            int leftSmallWidth = this.preferredSize.width;
            int rightSmallWidth = 0;
            if (!Strings.isNullOrEmpty((String)right)) {
                rightSmallWidth = this.preferredSize.width / 3;
                leftSmallWidth -= rightSmallWidth;
            }
            String[] leftSplitLines = LineComponent.lineBreakText(left, leftSmallWidth, lfm);
            String[] rightSplitLines = LineComponent.lineBreakText(right, rightSmallWidth, rfm);
            int lineCount = Math.max(leftSplitLines.length, rightSplitLines.length);
            for (int i = 0; i < lineCount; ++i) {
                if (i < leftSplitLines.length) {
                    String leftText = leftSplitLines[i];
                    textComponent.setPosition(new Point(x, y));
                    textComponent.setText(leftText);
                    textComponent.setColor(this.leftColor);
                    textComponent.setFont(leftFont);
                    textComponent.render(graphics);
                }
                if (i < rightSplitLines.length) {
                    String rightText = rightSplitLines[i];
                    textComponent.setPosition(new Point(x + this.preferredSize.width - LineComponent.getLineWidth(rightText, rfm), y));
                    textComponent.setText(rightText);
                    textComponent.setColor(this.rightColor);
                    textComponent.setFont(rightFont);
                    textComponent.render(graphics);
                }
                y += fmHeight;
            }
            Dimension dimension = new Dimension(this.preferredSize.width, y - baseY);
            this.bounds.setLocation(this.preferredLocation);
            this.bounds.setSize(dimension);
            return dimension;
        }
        if (!left.isEmpty()) {
            textComponent.setPosition(new Point(x, y));
            textComponent.setText(left);
            textComponent.setColor(this.leftColor);
            textComponent.setFont(leftFont);
            textComponent.render(graphics);
        }
        if (!right.isEmpty()) {
            textComponent.setPosition(new Point(x + this.preferredSize.width - rightFullWidth, y));
            textComponent.setText(right);
            textComponent.setColor(this.rightColor);
            textComponent.setFont(rightFont);
            textComponent.render(graphics);
        }
        Dimension dimension = new Dimension(this.preferredSize.width, (y += fmHeight) - baseY);
        this.bounds.setLocation(this.preferredLocation);
        this.bounds.setSize(dimension);
        return dimension;
    }

    private static int getLineWidth(String line, FontMetrics metrics) {
        return metrics.stringWidth(Text.removeTags(line));
    }

    private static String[] lineBreakText(String text, int maxWidth, FontMetrics metrics) {
        String[] words = text.split(" ");
        if (words.length == 0) {
            return new String[0];
        }
        StringBuilder wrapped = new StringBuilder(words[0]);
        int spaceLeft = maxWidth - metrics.stringWidth(wrapped.toString());
        for (int i = 1; i < words.length; ++i) {
            int spaceWidth;
            String word = words[i];
            int wordLen = metrics.stringWidth(word);
            if (wordLen + (spaceWidth = metrics.stringWidth(" ")) > spaceLeft) {
                wrapped.append('\n').append(word);
                spaceLeft = maxWidth - wordLen;
                continue;
            }
            wrapped.append(' ').append(word);
            spaceLeft -= spaceWidth + wordLen;
        }
        return wrapped.toString().split("\n");
    }

    private static Color $default$leftColor() {
        return Color.WHITE;
    }

    private static Color $default$rightColor() {
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

    LineComponent(String left, String right, Color leftColor, Color rightColor, Font leftFont, Font rightFont, Point preferredLocation, Dimension preferredSize, Rectangle bounds) {
        this.left = left;
        this.right = right;
        this.leftColor = leftColor;
        this.rightColor = rightColor;
        this.leftFont = leftFont;
        this.rightFont = rightFont;
        this.preferredLocation = preferredLocation;
        this.preferredSize = preferredSize;
        this.bounds = bounds;
    }

    public static LineComponentBuilder builder() {
        return new LineComponentBuilder();
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public void setLeftColor(Color leftColor) {
        this.leftColor = leftColor;
    }

    public void setRightColor(Color rightColor) {
        this.rightColor = rightColor;
    }

    public void setLeftFont(Font leftFont) {
        this.leftFont = leftFont;
    }

    public void setRightFont(Font rightFont) {
        this.rightFont = rightFont;
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

    public static class LineComponentBuilder {
        private String left;
        private String right;
        private boolean leftColor$set;
        private Color leftColor$value;
        private boolean rightColor$set;
        private Color rightColor$value;
        private Font leftFont;
        private Font rightFont;
        private boolean preferredLocation$set;
        private Point preferredLocation$value;
        private boolean preferredSize$set;
        private Dimension preferredSize$value;
        private boolean bounds$set;
        private Rectangle bounds$value;

        LineComponentBuilder() {
        }

        public LineComponentBuilder left(String left) {
            this.left = left;
            return this;
        }

        public LineComponentBuilder right(String right) {
            this.right = right;
            return this;
        }

        public LineComponentBuilder leftColor(Color leftColor) {
            this.leftColor$value = leftColor;
            this.leftColor$set = true;
            return this;
        }

        public LineComponentBuilder rightColor(Color rightColor) {
            this.rightColor$value = rightColor;
            this.rightColor$set = true;
            return this;
        }

        public LineComponentBuilder leftFont(Font leftFont) {
            this.leftFont = leftFont;
            return this;
        }

        public LineComponentBuilder rightFont(Font rightFont) {
            this.rightFont = rightFont;
            return this;
        }

        public LineComponentBuilder preferredLocation(Point preferredLocation) {
            this.preferredLocation$value = preferredLocation;
            this.preferredLocation$set = true;
            return this;
        }

        public LineComponentBuilder preferredSize(Dimension preferredSize) {
            this.preferredSize$value = preferredSize;
            this.preferredSize$set = true;
            return this;
        }

        public LineComponentBuilder bounds(Rectangle bounds) {
            this.bounds$value = bounds;
            this.bounds$set = true;
            return this;
        }

        public LineComponent build() {
            Color leftColor$value = this.leftColor$value;
            if (!this.leftColor$set) {
                leftColor$value = LineComponent.$default$leftColor();
            }
            Color rightColor$value = this.rightColor$value;
            if (!this.rightColor$set) {
                rightColor$value = LineComponent.$default$rightColor();
            }
            Point preferredLocation$value = this.preferredLocation$value;
            if (!this.preferredLocation$set) {
                preferredLocation$value = LineComponent.$default$preferredLocation();
            }
            Dimension preferredSize$value = this.preferredSize$value;
            if (!this.preferredSize$set) {
                preferredSize$value = LineComponent.$default$preferredSize();
            }
            Rectangle bounds$value = this.bounds$value;
            if (!this.bounds$set) {
                bounds$value = LineComponent.$default$bounds();
            }
            return new LineComponent(this.left, this.right, leftColor$value, rightColor$value, this.leftFont, this.rightFont, preferredLocation$value, preferredSize$value, bounds$value);
        }

        public String toString() {
            return "LineComponent.LineComponentBuilder(left=" + this.left + ", right=" + this.right + ", leftColor$value=" + String.valueOf(this.leftColor$value) + ", rightColor$value=" + String.valueOf(this.rightColor$value) + ", leftFont=" + String.valueOf(this.leftFont) + ", rightFont=" + String.valueOf(this.rightFont) + ", preferredLocation$value=" + String.valueOf(this.preferredLocation$value) + ", preferredSize$value=" + String.valueOf(this.preferredSize$value) + ", bounds$value=" + String.valueOf(this.bounds$value) + ")";
        }
    }
}

