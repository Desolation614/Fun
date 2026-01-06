/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.xptracker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.client.plugins.xptracker.XpSnapshotSingle;
import net.runelite.client.plugins.xptracker.XpTrackerConfig;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.SkillColor;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.ProgressBarComponent;
import net.runelite.client.ui.overlay.components.SplitComponent;

class XpInfoBoxOverlay
extends OverlayPanel {
    private static final int BORDER_SIZE = 2;
    private static final int XP_AND_PROGRESS_BAR_GAP = 2;
    private static final int XP_AND_ICON_GAP = 4;
    private static final Rectangle XP_AND_ICON_COMPONENT_BORDER = new Rectangle(2, 1, 4, 0);
    private final PanelComponent iconXpSplitPanel = new PanelComponent();
    private final XpTrackerPlugin plugin;
    private final XpTrackerConfig config;
    private final Skill skill;
    private final BufferedImage icon;

    XpInfoBoxOverlay(XpTrackerPlugin plugin, XpTrackerConfig config, Skill skill, BufferedImage icon) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.skill = skill;
        this.icon = icon;
        this.panelComponent.setBorder(new Rectangle(2, 2, 2, 2));
        this.panelComponent.setGap(new Point(0, 2));
        this.iconXpSplitPanel.setBorder(XP_AND_ICON_COMPONENT_BORDER);
        this.iconXpSplitPanel.setBackgroundColor(null);
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "XP Tracker overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        this.iconXpSplitPanel.getChildren().clear();
        graphics.setFont(FontManager.getRunescapeSmallFont());
        XpSnapshotSingle snapshot = this.plugin.getSkillSnapshot(this.skill);
        String leftStr = this.config.onScreenDisplayMode().getKey();
        String rightNum = this.config.onScreenDisplayMode().getValueFunc().apply(snapshot);
        LineComponent xpLine = LineComponent.builder().left(leftStr + ":").right(rightNum).build();
        String bottomLeftStr = this.config.onScreenDisplayModeBottom().getKey();
        String bottomRightNum = this.config.onScreenDisplayModeBottom().getValueFunc().apply(snapshot);
        LineComponent xpLineBottom = LineComponent.builder().left(bottomLeftStr + ":").right(bottomRightNum).build();
        SplitComponent xpSplit = SplitComponent.builder().first(xpLine).second(xpLineBottom).orientation(ComponentOrientation.VERTICAL).build();
        ImageComponent imageComponent = new ImageComponent(this.icon);
        SplitComponent iconXpSplit = SplitComponent.builder().first(imageComponent).second(xpSplit).orientation(ComponentOrientation.HORIZONTAL).gap(new Point(4, 0)).build();
        this.iconXpSplitPanel.getChildren().add(iconXpSplit);
        ProgressBarComponent progressBarComponent = new ProgressBarComponent();
        progressBarComponent.setBackgroundColor(new Color(61, 56, 49));
        progressBarComponent.setForegroundColor(SkillColor.find(this.skill).getColor());
        progressBarComponent.setLeftLabel(String.valueOf(snapshot.getStartLevel()));
        progressBarComponent.setRightLabel(snapshot.getEndGoalXp() == 200000000 ? "200M" : String.valueOf(snapshot.getEndLevel()));
        progressBarComponent.setValue(snapshot.getSkillProgressToGoal());
        this.panelComponent.getChildren().add(this.iconXpSplitPanel);
        this.panelComponent.getChildren().add(progressBarComponent);
        return super.render(graphics);
    }

    @Override
    public String getName() {
        return super.getName() + this.skill.getName();
    }

    Skill getSkill() {
        return this.skill;
    }
}

