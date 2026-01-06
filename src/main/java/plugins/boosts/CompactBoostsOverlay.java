/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.boosts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.boosts.BoostsConfig;
import net.runelite.client.plugins.boosts.BoostsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.ImageUtil;

class CompactBoostsOverlay
extends Overlay {
    private static final int H_PADDING = 2;
    private static final int V_PADDING = 1;
    private static final int TEXT_WIDTH = 22;
    private static final BufferedImage BUFFED = ImageUtil.loadImageResource(CompactBoostsOverlay.class, "buffedsmall.png");
    private static final BufferedImage DEBUFFED = ImageUtil.loadImageResource(CompactBoostsOverlay.class, "debuffedsmall.png");
    private final Client client;
    private final BoostsConfig config;
    private final BoostsPlugin plugin;
    private final SkillIconManager skillIconManager;
    private int curY;
    private int maxX;

    @Inject
    private CompactBoostsOverlay(Client client, BoostsConfig config, BoostsPlugin plugin, SkillIconManager skillIconManager) {
        super(plugin);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.skillIconManager = skillIconManager;
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setPriority(0.5f);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Set<Skill> boostedSkills = this.plugin.getSkillsToDisplay();
        if (!this.config.compactDisplay()) {
            return null;
        }
        this.maxX = 0;
        this.curY = 0;
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int fontHeight = fontMetrics.getHeight();
        for (Skill skill : boostedSkills) {
            int base;
            int boosted = this.client.getBoostedSkillLevel(skill);
            int boost = boosted - (base = this.client.getRealSkillLevel(skill));
            if (boost == 0) continue;
            this.drawBoost(graphics, fontMetrics, fontHeight, this.skillIconManager.getSkillImage(skill, true), this.getTextColor(boost), this.getBoostText(boost, base, boosted));
        }
        int time = this.plugin.getChangeUpTicks();
        if (time != -1) {
            this.drawBoost(graphics, fontMetrics, fontHeight, DEBUFFED, time < 10 ? Color.RED.brighter() : Color.WHITE, Integer.toString(this.plugin.getChangeTime(time)));
        }
        if ((time = this.plugin.getChangeDownTicks()) != -1) {
            this.drawBoost(graphics, fontMetrics, fontHeight, BUFFED, time < 10 ? Color.RED.brighter() : Color.WHITE, Integer.toString(this.plugin.getChangeTime(time)));
        }
        return new Dimension(this.maxX, this.curY);
    }

    private void drawBoost(Graphics2D graphics, FontMetrics fontMetrics, int fontHeight, BufferedImage image, Color color, String text) {
        graphics.drawImage((Image)image, 0, this.curY, null);
        int stringWidth = fontMetrics.stringWidth(text);
        TextComponent textComponent = new TextComponent();
        textComponent.setColor(color);
        textComponent.setText(text);
        textComponent.setOutline(true);
        textComponent.setPosition(new Point(image.getWidth() + 2 + (22 - stringWidth), this.curY + fontHeight));
        textComponent.render(graphics);
        this.curY += Math.max(image.getHeight(), fontHeight) + 1;
        this.maxX = Math.max(this.maxX, image.getWidth() + 2 + 22);
    }

    private String getBoostText(int boost, int base, int boosted) {
        if (this.config.useRelativeBoost()) {
            return boost > 0 ? "+" + boost : Integer.toString(boost);
        }
        return Integer.toString(boosted);
    }

    private Color getTextColor(int boost) {
        if (boost < 0) {
            return new Color(238, 51, 51);
        }
        return boost <= this.config.boostThreshold() ? Color.YELLOW : Color.GREEN;
    }
}

