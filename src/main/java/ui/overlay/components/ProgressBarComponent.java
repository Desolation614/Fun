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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;

public class ProgressBarComponent
implements LayoutableRenderableEntity {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");
    private static final DecimalFormat DECIMAL_FORMAT_ABS = new DecimalFormat("#0");
    private static final int SIDE_LABEL_OFFSET = 4;
    private long minimum;
    private long maximum = 100L;
    private double value;
    private LabelDisplayMode labelDisplayMode = LabelDisplayMode.PERCENTAGE;
    private String centerLabel;
    private String leftLabel;
    private String rightLabel;
    private Color foregroundColor = new Color(82, 161, 82);
    private Color backgroundColor = new Color(255, 255, 255, 127);
    private Color fontColor = Color.WHITE;
    private Point preferredLocation = new Point();
    private Dimension preferredSize = new Dimension(129, 16);
    private final Rectangle bounds = new Rectangle();

    @Override
    public Dimension render(Graphics2D graphics) {
        TextComponent leftTextComponent;
        Object textToWrite;
        FontMetrics metrics = graphics.getFontMetrics();
        int barX = this.preferredLocation.x;
        int barY = this.preferredLocation.y;
        long span = this.maximum - this.minimum;
        double currentValue = this.value - (double)this.minimum;
        double pc = currentValue / (double)span;
        switch (this.labelDisplayMode) {
            case TEXT_ONLY: {
                textToWrite = "";
                break;
            }
            case PERCENTAGE: {
                textToWrite = ProgressBarComponent.formatPercentageProgress(pc);
                break;
            }
            case BOTH: {
                textToWrite = ProgressBarComponent.formatFullProgress(currentValue, this.maximum) + " (" + ProgressBarComponent.formatPercentageProgress(pc) + ")";
                break;
            }
            default: {
                textToWrite = ProgressBarComponent.formatFullProgress(currentValue, this.maximum);
            }
        }
        if (!Strings.isNullOrEmpty((String)this.centerLabel)) {
            if (!((String)textToWrite).isEmpty()) {
                textToWrite = (String)textToWrite + " ";
            }
            textToWrite = (String)textToWrite + this.centerLabel;
        }
        int width = this.preferredSize.width;
        int height = Math.max(this.preferredSize.height, 16);
        int progressTextX = barX + (width - metrics.stringWidth((String)textToWrite)) / 2;
        int progressTextY = barY + (height - metrics.getHeight()) / 2 + metrics.getHeight();
        int progressFill = (int)((double)width * Math.min(1.0, pc));
        graphics.setColor(this.backgroundColor);
        graphics.fillRect(barX + progressFill, barY, width - progressFill, height);
        graphics.setColor(this.foregroundColor);
        graphics.fillRect(barX, barY, progressFill, height);
        TextComponent textComponent = new TextComponent();
        textComponent.setPosition(new Point(progressTextX, progressTextY));
        textComponent.setColor(this.fontColor);
        textComponent.setText((String)textToWrite);
        textComponent.render(graphics);
        if (this.leftLabel != null) {
            leftTextComponent = new TextComponent();
            leftTextComponent.setPosition(new Point(barX + 4, progressTextY));
            leftTextComponent.setColor(this.fontColor);
            leftTextComponent.setText(this.leftLabel);
            leftTextComponent.render(graphics);
        }
        if (this.rightLabel != null) {
            leftTextComponent = new TextComponent();
            leftTextComponent.setPosition(new Point(barX + width - metrics.stringWidth(this.rightLabel) - 4, progressTextY));
            leftTextComponent.setColor(this.fontColor);
            leftTextComponent.setText(this.rightLabel);
            leftTextComponent.render(graphics);
        }
        Dimension dimension = new Dimension(width, height);
        this.bounds.setLocation(this.preferredLocation);
        this.bounds.setSize(dimension);
        return dimension;
    }

    private static String formatFullProgress(double current, long maximum) {
        return DECIMAL_FORMAT_ABS.format(Math.floor(current)) + "/" + maximum;
    }

    private static String formatPercentageProgress(double ratio) {
        return DECIMAL_FORMAT.format(ratio * 100.0) + "%";
    }

    public void setMinimum(long minimum) {
        this.minimum = minimum;
    }

    public void setMaximum(long maximum) {
        this.maximum = maximum;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setLabelDisplayMode(LabelDisplayMode labelDisplayMode) {
        this.labelDisplayMode = labelDisplayMode;
    }

    public void setCenterLabel(String centerLabel) {
        this.centerLabel = centerLabel;
    }

    public void setLeftLabel(String leftLabel) {
        this.leftLabel = leftLabel;
    }

    public void setRightLabel(String rightLabel) {
        this.rightLabel = rightLabel;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
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

    public static enum LabelDisplayMode {
        PERCENTAGE,
        FULL,
        TEXT_ONLY,
        BOTH;

    }
}

