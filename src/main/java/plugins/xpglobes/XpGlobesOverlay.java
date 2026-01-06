/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Point
 */
package net.runelite.client.plugins.xpglobes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.xpglobes.XpGlobe;
import net.runelite.client.plugins.xpglobes.XpGlobesConfig;
import net.runelite.client.plugins.xpglobes.XpGlobesPlugin;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.ui.SkillColor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ImageUtil;

public class XpGlobesOverlay
extends Overlay {
    private static final int MINIMUM_STEP = 10;
    private static final int PROGRESS_RADIUS_START = 90;
    private static final int PROGRESS_RADIUS_REMAINDER = 0;
    private static final int PROGRESS_BACKGROUND_SIZE = 5;
    private static final int TOOLTIP_RECT_SIZE_X = 150;
    private static final Color DARK_OVERLAY_COLOR = new Color(0, 0, 0, 180);
    private static final String FLIP_ACTION = "Flip";
    private static final double GLOBE_ICON_RATIO = 0.65;
    private final Client client;
    private final XpGlobesPlugin plugin;
    private final XpGlobesConfig config;
    private final XpTrackerService xpTrackerService;
    private final TooltipManager tooltipManager;
    private final SkillIconManager iconManager;
    private final Tooltip xpTooltip = new Tooltip(new PanelComponent());

    @Inject
    private XpGlobesOverlay(Client client, XpGlobesPlugin plugin, XpGlobesConfig config, XpTrackerService xpTrackerService, SkillIconManager iconManager, TooltipManager tooltipManager) {
        super(plugin);
        this.iconManager = iconManager;
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.xpTrackerService = xpTrackerService;
        this.tooltipManager = tooltipManager;
        this.xpTooltip.getComponent().setPreferredSize(new Dimension(150, 0));
        this.setPosition(OverlayPosition.TOP_CENTER);
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "XP Globes overlay");
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY, FLIP_ACTION, "XP Globes overlay", e -> config.setAlignOrbsVertically(!config.alignOrbsVertically()));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        int progressArcOffset;
        List<XpGlobe> xpGlobes = this.plugin.getXpGlobes();
        int queueSize = xpGlobes.size();
        if (queueSize == 0) {
            return null;
        }
        int curDrawPosition = progressArcOffset = (int)Math.ceil((double)Math.max(5, this.config.progressArcStrokeWidth()) / 2.0);
        for (XpGlobe xpGlobe : xpGlobes) {
            int startXp = this.xpTrackerService.getStartGoalXp(xpGlobe.getSkill());
            int goalXp = this.xpTrackerService.getEndGoalXp(xpGlobe.getSkill());
            if (this.config.alignOrbsVertically()) {
                this.renderProgressCircle(graphics, xpGlobe, startXp, goalXp, progressArcOffset, curDrawPosition, this.getBounds());
            } else {
                this.renderProgressCircle(graphics, xpGlobe, startXp, goalXp, curDrawPosition, progressArcOffset, this.getBounds());
            }
            curDrawPosition += 10 + this.config.xpOrbSize();
        }
        int markersLength = queueSize * (this.config.xpOrbSize() + progressArcOffset) + 10 * (queueSize - 1);
        if (this.config.alignOrbsVertically()) {
            return new Dimension(this.config.xpOrbSize() + progressArcOffset * 2, markersLength);
        }
        return new Dimension(markersLength, this.config.xpOrbSize() + progressArcOffset * 2);
    }

    private double getSkillProgress(int startXp, int currentXp, int goalXp) {
        double xpGained = currentXp - startXp;
        double xpGoal = goalXp - startXp;
        return xpGained / xpGoal * 100.0;
    }

    private double getSkillProgressRadius(int startXp, int currentXp, int goalXp) {
        return -(3.6 * this.getSkillProgress(startXp, currentXp, goalXp));
    }

    private void renderProgressCircle(Graphics2D graphics, XpGlobe skillToDraw, int startXp, int goalXp, int x, int y, Rectangle bounds) {
        double radiusCurrentXp = this.getSkillProgressRadius(startXp, skillToDraw.getCurrentXp(), goalXp);
        double radiusToGoalXp = 360.0;
        Ellipse2D backgroundCircle = this.drawEllipse(graphics, x, y);
        this.drawSkillImage(graphics, skillToDraw, x, y);
        Point mouse = this.client.getMouseCanvasPosition();
        int mouseX = mouse.getX() - bounds.x;
        int mouseY = mouse.getY() - bounds.y;
        if (backgroundCircle.contains(mouseX, mouseY)) {
            graphics.setColor(DARK_OVERLAY_COLOR);
            graphics.fill(backgroundCircle);
            this.drawProgressLabel(graphics, skillToDraw, startXp, goalXp, x, y);
            if (this.config.enableTooltips()) {
                this.drawTooltip(skillToDraw, goalXp);
            }
        }
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        this.drawProgressArc(graphics, x, y, this.config.xpOrbSize(), this.config.xpOrbSize(), 0.0, radiusToGoalXp, 5, this.config.progressOrbOutLineColor());
        this.drawProgressArc(graphics, x, y, this.config.xpOrbSize(), this.config.xpOrbSize(), 90.0, radiusCurrentXp, this.config.progressArcStrokeWidth(), this.config.enableCustomArcColor() ? this.config.progressArcColor() : SkillColor.find(skillToDraw.getSkill()).getColor());
    }

    private void drawProgressLabel(Graphics2D graphics, XpGlobe globe, int startXp, int goalXp, int x, int y) {
        if (goalXp <= globe.getCurrentXp()) {
            return;
        }
        String progress = (int)this.getSkillProgress(startXp, globe.getCurrentXp(), goalXp) + "%";
        FontMetrics metrics = graphics.getFontMetrics();
        int drawX = x + this.config.xpOrbSize() / 2 - metrics.stringWidth(progress) / 2;
        int drawY = y + this.config.xpOrbSize() / 2 + metrics.getHeight() / 2;
        OverlayUtil.renderTextLocation(graphics, new Point(drawX, drawY), progress, Color.WHITE);
    }

    private void drawProgressArc(Graphics2D graphics, int x, int y, int w, int h, double radiusStart, double radiusEnd, int strokeWidth, Color color) {
        Stroke stroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(strokeWidth, 0, 2));
        graphics.setColor(color);
        graphics.draw(new Arc2D.Double(x, y, w, h, radiusStart, radiusEnd, 0));
        graphics.setStroke(stroke);
    }

    private Ellipse2D drawEllipse(Graphics2D graphics, int x, int y) {
        graphics.setColor(this.config.progressOrbBackgroundColor());
        Ellipse2D.Double ellipse = new Ellipse2D.Double(x, y, this.config.xpOrbSize(), this.config.xpOrbSize());
        graphics.fill(ellipse);
        graphics.draw(ellipse);
        return ellipse;
    }

    private void drawSkillImage(Graphics2D graphics, XpGlobe xpGlobe, int x, int y) {
        int orbSize = this.config.xpOrbSize();
        BufferedImage skillImage = this.getScaledSkillIcon(xpGlobe, orbSize);
        if (skillImage == null) {
            return;
        }
        graphics.drawImage((Image)skillImage, x + orbSize / 2 - skillImage.getWidth() / 2, y + orbSize / 2 - skillImage.getHeight() / 2, null);
    }

    private BufferedImage getScaledSkillIcon(XpGlobe xpGlobe, int orbSize) {
        if (xpGlobe.getSkillIcon() != null && xpGlobe.getSize() == orbSize) {
            return xpGlobe.getSkillIcon();
        }
        BufferedImage icon = this.iconManager.getSkillImage(xpGlobe.getSkill());
        if (icon == null) {
            return null;
        }
        int size = orbSize - this.config.progressArcStrokeWidth();
        int scaledIconSize = (int)((double)size * 0.65);
        if (scaledIconSize <= 0) {
            return null;
        }
        icon = ImageUtil.resizeImage(icon, scaledIconSize, scaledIconSize, true);
        xpGlobe.setSkillIcon(icon);
        xpGlobe.setSize(orbSize);
        return icon;
    }

    private void drawTooltip(XpGlobe mouseOverSkill, int goalXp) {
        mouseOverSkill.setTime(Instant.now());
        String skillName = mouseOverSkill.getSkill().getName();
        String skillLevel = Integer.toString(mouseOverSkill.getCurrentLevel());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        String skillCurrentXp = decimalFormat.format(mouseOverSkill.getCurrentXp());
        PanelComponent xpTooltip = (PanelComponent)this.xpTooltip.getComponent();
        xpTooltip.getChildren().clear();
        xpTooltip.getChildren().add(LineComponent.builder().left(skillName).right(skillLevel).build());
        xpTooltip.getChildren().add(LineComponent.builder().left("Current XP:").leftColor(Color.ORANGE).right(skillCurrentXp).build());
        if (goalXp > mouseOverSkill.getCurrentXp()) {
            int xpHr;
            int actionsLeft;
            if (this.config.showActionsLeft() && (actionsLeft = this.xpTrackerService.getActionsLeft(mouseOverSkill.getSkill())) != Integer.MAX_VALUE) {
                String actionsLeftString = decimalFormat.format(actionsLeft);
                xpTooltip.getChildren().add(LineComponent.builder().left("Actions left:").leftColor(Color.ORANGE).right(actionsLeftString).build());
            }
            if (this.config.showXpLeft()) {
                int xpLeft = goalXp - mouseOverSkill.getCurrentXp();
                String skillXpToLvl = decimalFormat.format(xpLeft);
                xpTooltip.getChildren().add(LineComponent.builder().left("XP left:").leftColor(Color.ORANGE).right(skillXpToLvl).build());
            }
            if (this.config.showXpHour() && (xpHr = this.xpTrackerService.getXpHr(mouseOverSkill.getSkill())) != 0) {
                String xpHrString = decimalFormat.format(xpHr);
                xpTooltip.getChildren().add(LineComponent.builder().left("XP per hour:").leftColor(Color.ORANGE).right(xpHrString).build());
            }
            if (this.config.showTimeTilGoal()) {
                String timeLeft = this.xpTrackerService.getTimeTilGoal(mouseOverSkill.getSkill());
                xpTooltip.getChildren().add(LineComponent.builder().left("Time left:").leftColor(Color.ORANGE).right(timeLeft).build());
            }
        }
        this.tooltipManager.add(this.xpTooltip);
    }
}

