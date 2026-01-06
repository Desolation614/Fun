/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.statusbars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.function.Supplier;
import net.runelite.client.plugins.statusbars.StatusBarsConfig;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.components.TextComponent;

class BarRenderer {
    private static final Color BACKGROUND = new Color(0, 0, 0, 150);
    private static final Color OVERHEAL_COLOR = new Color(216, 255, 139, 150);
    private static final int SKILL_ICON_HEIGHT = 35;
    private static final int COUNTER_ICON_HEIGHT = 18;
    private static final int BORDER_SIZE = 1;
    private static final int MIN_ICON_AND_COUNTER_WIDTH = 16;
    static final int DEFAULT_WIDTH = 20;
    static final int MIN_WIDTH = 3;
    static final int MAX_WIDTH = 40;
    private final Supplier<Integer> maxValueSupplier;
    private final Supplier<Integer> currentValueSupplier;
    private final Supplier<Integer> healSupplier;
    private final Supplier<Color> colorSupplier;
    private final Supplier<Color> healColorSupplier;
    private final Supplier<Image> iconSupplier;
    private int maxValue;
    private int currentValue;

    private void refreshSkills() {
        this.maxValue = this.maxValueSupplier.get();
        this.currentValue = this.currentValueSupplier.get();
    }

    void renderBar(StatusBarsConfig config, Graphics2D graphics, int x, int y, int width, int height) {
        int filledHeight = BarRenderer.getBarHeight(this.maxValue, this.currentValue, height);
        Color fill = this.colorSupplier.get();
        this.refreshSkills();
        graphics.setColor(BACKGROUND);
        graphics.drawRect(x, y, width - 1, height - 1);
        graphics.fillRect(x, y, width, height);
        graphics.setColor(fill);
        graphics.fillRect(x + 1, y + 1 + (height - filledHeight), width - 2, filledHeight - 2);
        if (config.enableRestorationBars()) {
            this.renderRestore(graphics, x, y, width, height);
        }
        if (config.enableSkillIcon() || config.enableCounter()) {
            this.renderIconsAndCounters(config, graphics, x, y, width);
        }
    }

    private void renderIconsAndCounters(StatusBarsConfig config, Graphics2D graphics, int x, int y, int width) {
        Image icon;
        if (width < 16) {
            return;
        }
        boolean skillIconEnabled = config.enableSkillIcon();
        if (skillIconEnabled && (icon = this.iconSupplier.get()) != null) {
            int xDraw = x + width / 2 - icon.getWidth(null) / 2;
            graphics.drawImage(icon, xDraw, y + 4, null);
        }
        if (config.enableCounter()) {
            graphics.setFont(FontManager.getRunescapeSmallFont());
            String counterText = Integer.toString(this.currentValue);
            int widthOfCounter = graphics.getFontMetrics().stringWidth(counterText);
            int centerText = width / 2 - widthOfCounter / 2;
            int yOffset = skillIconEnabled ? 35 : 18;
            TextComponent textComponent = new TextComponent();
            textComponent.setText(counterText);
            textComponent.setPosition(new Point(x + centerText, y + yOffset));
            textComponent.render(graphics);
        }
    }

    private void renderRestore(Graphics2D graphics, int x, int y, int width, int height) {
        int fillHeight;
        int fillY;
        int heal = this.healSupplier.get();
        if (heal <= 0) {
            return;
        }
        int filledCurrentHeight = BarRenderer.getBarHeight(this.maxValue, this.currentValue, height);
        int filledHealHeight = BarRenderer.getBarHeight(this.maxValue, heal, height);
        Color color = this.healColorSupplier.get();
        graphics.setColor(color);
        if (filledHealHeight + filledCurrentHeight > height) {
            graphics.setColor(OVERHEAL_COLOR);
            fillY = y + 1;
            fillHeight = height - filledCurrentHeight - 1;
        } else {
            fillY = y + 1 + height - (filledCurrentHeight + filledHealHeight);
            fillHeight = filledHealHeight;
        }
        graphics.fillRect(x + 1, fillY, width - 2, fillHeight);
    }

    private static int getBarHeight(int base, int current, int size) {
        double ratio = (double)current / (double)base;
        if (ratio >= 1.0) {
            return size;
        }
        return (int)Math.round(ratio * (double)size);
    }

    public BarRenderer(Supplier<Integer> maxValueSupplier, Supplier<Integer> currentValueSupplier, Supplier<Integer> healSupplier, Supplier<Color> colorSupplier, Supplier<Color> healColorSupplier, Supplier<Image> iconSupplier) {
        this.maxValueSupplier = maxValueSupplier;
        this.currentValueSupplier = currentValueSupplier;
        this.healSupplier = healSupplier;
        this.colorSupplier = colorSupplier;
        this.healColorSupplier = healColorSupplier;
        this.iconSupplier = iconSupplier;
    }
}

