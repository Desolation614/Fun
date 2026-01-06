/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.Experience
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.xptracker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.xptracker.XpPanel;
import net.runelite.client.plugins.xptracker.XpPanelLabel;
import net.runelite.client.plugins.xptracker.XpProgressBarLabel;
import net.runelite.client.plugins.xptracker.XpSnapshotSingle;
import net.runelite.client.plugins.xptracker.XpTrackerConfig;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.SkillColor;
import net.runelite.client.ui.components.MouseDragEventForwarder;
import net.runelite.client.ui.components.ProgressBar;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.QuantityFormatter;

class XpInfoBox
extends JPanel {
    static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String PROGRESS_BAR_TOOLTIP = "<html>%s Actions done<br/>%s Actions/hr<br/>%s %s</html>";
    private static final String PROGRESS_BAR_TOOLTIP_NO_ACTIONS = "<html>%s %s</html>";
    private static final String HTML_LABEL_TEMPLATE = "<html><body style='color:%s'>%s<span style='color:white'>%s</span></body></html>";
    private static final String REMOVE_STATE = "Remove from canvas";
    private static final String ADD_STATE = "Add to canvas";
    private static final EmptyBorder DEFAULT_PROGRESS_WRAPPER_BORDER;
    private static final EmptyBorder COMPACT_PROGRESS_WRAPPER_BORDER;
    private final JComponent panel;
    private final Skill skill;
    private final JPanel container = new JPanel();
    private final JPanel headerPanel = new JPanel();
    private final JPanel statsPanel = new JPanel();
    private final JPanel progressWrapper = new JPanel();
    private final JLabel compactSkillIcon;
    private final ProgressBar progressBar = new ProgressBar();
    private final JLabel topLeftStat = new JLabel();
    private final JLabel bottomLeftStat = new JLabel();
    private final JLabel topRightStat = new JLabel();
    private final JLabel bottomRightStat = new JLabel();
    private final JMenuItem pauseSkill = new JMenuItem("Pause");
    private final JMenuItem canvasItem = new JMenuItem("Add to canvas");
    private final XpTrackerConfig xpTrackerConfig;
    private boolean paused = false;

    XpInfoBox(final XpTrackerPlugin xpTrackerPlugin, final XpTrackerConfig xpTrackerConfig, Client client, JComponent panel, final Skill skill, SkillIconManager iconManager) {
        this.xpTrackerConfig = xpTrackerConfig;
        this.panel = panel;
        this.skill = skill;
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(5, 0, 0, 0));
        this.container.setLayout(new BorderLayout());
        this.container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        final JMenuItem openXpTracker = new JMenuItem("Open Wise Old Man");
        openXpTracker.addActionListener(e -> LinkBrowser.browse(XpPanel.buildXpTrackerUrl(client.getWorldType(), (Actor)client.getLocalPlayer(), skill)));
        JMenuItem reset = new JMenuItem("Reset");
        reset.addActionListener(e -> xpTrackerPlugin.resetSkillState(skill));
        JMenuItem resetOthers = new JMenuItem("Reset others");
        resetOthers.addActionListener(e -> xpTrackerPlugin.resetOtherSkillState(skill));
        JMenuItem resetPerHour = new JMenuItem("Reset/hr");
        resetPerHour.addActionListener(e -> xpTrackerPlugin.resetSkillPerHourState(skill));
        this.pauseSkill.addActionListener(e -> xpTrackerPlugin.pauseSkill(skill, !this.paused));
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        popupMenu.add(openXpTracker);
        popupMenu.add(reset);
        popupMenu.add(resetOthers);
        popupMenu.add(resetPerHour);
        popupMenu.add(this.pauseSkill);
        popupMenu.add(this.canvasItem);
        popupMenu.addPopupMenuListener(new PopupMenuListener(){

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                openXpTracker.setVisible(xpTrackerConfig.wiseOldManOpenOption());
                XpInfoBox.this.canvasItem.setText(xpTrackerPlugin.hasOverlay(skill) ? XpInfoBox.REMOVE_STATE : XpInfoBox.ADD_STATE);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
            }
        });
        this.canvasItem.addActionListener(e -> {
            if (this.canvasItem.getText().equals(REMOVE_STATE)) {
                xpTrackerPlugin.removeOverlay(skill);
            } else {
                xpTrackerPlugin.addOverlay(skill);
            }
        });
        this.headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.headerPanel.setLayout(new BorderLayout());
        this.statsPanel.setLayout(new DynamicGridLayout(2, 2));
        this.statsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.statsPanel.setBorder(new EmptyBorder(9, 2, 9, 2));
        this.topLeftStat.setFont(FontManager.getRunescapeSmallFont());
        this.bottomLeftStat.setFont(FontManager.getRunescapeSmallFont());
        this.topRightStat.setFont(FontManager.getRunescapeSmallFont());
        this.bottomRightStat.setFont(FontManager.getRunescapeSmallFont());
        this.statsPanel.add(this.topLeftStat);
        this.statsPanel.add(this.topRightStat);
        this.statsPanel.add(this.bottomLeftStat);
        this.statsPanel.add(this.bottomRightStat);
        JLabel headerSkillIcon = XpInfoBox.getSkillIcon(iconManager, skill, 35, 35, false);
        this.headerPanel.add((Component)headerSkillIcon, "West");
        this.headerPanel.add((Component)this.statsPanel, "Center");
        this.progressWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.progressWrapper.setLayout(new BorderLayout());
        this.progressWrapper.setBorder(DEFAULT_PROGRESS_WRAPPER_BORDER);
        this.progressBar.setMaximumValue(100);
        this.progressBar.setBackground(new Color(61, 56, 49));
        this.progressBar.setForeground(SkillColor.find(skill).getColor());
        this.progressBar.setDimmedText("Paused");
        this.compactSkillIcon = XpInfoBox.getSkillIcon(iconManager, skill, 25, 16, true);
        this.compactSkillIcon.setVisible(false);
        this.progressWrapper.add((Component)this.compactSkillIcon, "West");
        this.progressWrapper.add((Component)this.progressBar, "Center");
        this.container.add((Component)this.headerPanel, "North");
        this.container.add((Component)this.progressWrapper, "South");
        this.container.setComponentPopupMenu(popupMenu);
        this.progressBar.setComponentPopupMenu(popupMenu);
        MouseDragEventForwarder mouseDragEventForwarder = new MouseDragEventForwarder(panel);
        this.container.addMouseListener(mouseDragEventForwarder);
        this.container.addMouseMotionListener(mouseDragEventForwarder);
        this.progressBar.addMouseListener(mouseDragEventForwarder);
        this.progressBar.addMouseMotionListener(mouseDragEventForwarder);
        MouseAdapter clickToggleCompact = new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    xpTrackerPlugin.setSkillCompactViewState(skill, XpInfoBox.this.toggleCompactView());
                }
            }
        };
        this.container.addMouseListener(clickToggleCompact);
        this.progressBar.addMouseListener(clickToggleCompact);
        this.add((Component)this.container, "North");
    }

    void reset() {
        this.canvasItem.setText(ADD_STATE);
        this.setCompactView(false);
        this.panel.remove(this);
        this.panel.revalidate();
    }

    void update(boolean updated, boolean paused, XpSnapshotSingle xpSnapshotSingle) {
        SwingUtilities.invokeLater(() -> this.rebuildAsync(updated, paused, xpSnapshotSingle));
    }

    private boolean toggleCompactView() {
        boolean isCompact = !this.headerPanel.isVisible();
        this.setCompactView(!isCompact);
        return !isCompact;
    }

    private void setCompactView(boolean compact) {
        this.progressWrapper.setBorder(compact ? COMPACT_PROGRESS_WRAPPER_BORDER : DEFAULT_PROGRESS_WRAPPER_BORDER);
        this.headerPanel.setVisible(!compact);
        this.compactSkillIcon.setVisible(compact);
    }

    private static JLabel getSkillIcon(SkillIconManager iconManager, Skill skill, int width, int height, boolean small) {
        JLabel skillIcon = new JLabel();
        skillIcon.setIcon(new ImageIcon(iconManager.getSkillImage(skill, small)));
        skillIcon.setPreferredSize(new Dimension(width, height));
        skillIcon.setHorizontalAlignment(0);
        skillIcon.setVerticalAlignment(0);
        return skillIcon;
    }

    private void rebuildAsync(boolean updated, boolean skillPaused, XpSnapshotSingle xpSnapshotSingle) {
        if (updated) {
            if (this.getParent() != this.panel) {
                this.panel.add(this);
                this.panel.revalidate();
            }
            if (this.xpTrackerConfig.prioritizeRecentXpSkills()) {
                this.panel.setComponentZOrder(this, 0);
            }
            this.paused = skillPaused;
            this.progressBar.setValue((int)xpSnapshotSingle.getSkillProgressToGoal());
            this.progressBar.setCenterLabel(this.xpTrackerConfig.progressBarLabel().getValueFunc().apply(xpSnapshotSingle));
            this.progressBar.setLeftLabel("Lvl. " + xpSnapshotSingle.getStartLevel());
            this.progressBar.setRightLabel((String)(xpSnapshotSingle.getEndGoalXp() == 200000000 ? "200M" : "Lvl. " + xpSnapshotSingle.getEndLevel()));
            if (this.xpTrackerConfig.showIntermediateLevels() && xpSnapshotSingle.getEndLevel() - xpSnapshotSingle.getStartLevel() > 1) {
                ArrayList<Integer> positions = new ArrayList<Integer>();
                for (int level = xpSnapshotSingle.getStartLevel() + 1; level <= xpSnapshotSingle.getEndLevel(); ++level) {
                    double relativeStartExperience = Experience.getXpForLevel((int)level) - xpSnapshotSingle.getStartGoalXp();
                    double relativeEndExperience = xpSnapshotSingle.getEndGoalXp() - xpSnapshotSingle.getStartGoalXp();
                    positions.add((int)(relativeStartExperience / relativeEndExperience * 100.0));
                }
                this.progressBar.setPositions(positions);
            } else {
                this.progressBar.setPositions(Collections.emptyList());
            }
            XpProgressBarLabel tooltipLabel = this.xpTrackerConfig.progressBarTooltipLabel();
            if (XpInfoBox.isCombatSkill(this.skill)) {
                this.progressBar.setToolTipText(String.format(PROGRESS_BAR_TOOLTIP_NO_ACTIONS, tooltipLabel.getValueFunc().apply(xpSnapshotSingle), tooltipLabel == XpProgressBarLabel.PERCENTAGE ? "of goal" : "till goal lvl"));
            } else {
                this.progressBar.setToolTipText(String.format(PROGRESS_BAR_TOOLTIP, xpSnapshotSingle.getActionsInSession(), xpSnapshotSingle.getActionsPerHour(), tooltipLabel.getValueFunc().apply(xpSnapshotSingle), tooltipLabel == XpProgressBarLabel.PERCENTAGE ? "of goal" : "till goal lvl"));
            }
            this.progressBar.setDimmed(skillPaused);
            this.setCompactView(xpSnapshotSingle.isCompactView());
        } else if (!this.paused && skillPaused) {
            this.progressBar.setDimmed(true);
            this.paused = true;
            this.pauseSkill.setText("Unpause");
        } else if (this.paused && !skillPaused) {
            this.progressBar.setDimmed(false);
            this.paused = false;
            this.pauseSkill.setText("Pause");
        }
        this.topLeftStat.setText(this.htmlLabel(this.xpTrackerConfig.xpPanelLabel1(), xpSnapshotSingle));
        this.topRightStat.setText(this.htmlLabel(this.xpTrackerConfig.xpPanelLabel2(), xpSnapshotSingle));
        this.bottomLeftStat.setText(this.htmlLabel(this.xpTrackerConfig.xpPanelLabel3(), xpSnapshotSingle));
        this.bottomRightStat.setText(this.htmlLabel(this.xpTrackerConfig.xpPanelLabel4(), xpSnapshotSingle));
    }

    private String htmlLabel(XpPanelLabel panelLabel, XpSnapshotSingle xpSnapshotSingle) {
        if (XpInfoBox.isActions(panelLabel) && XpInfoBox.isCombatSkill(this.skill)) {
            return "";
        }
        String key = panelLabel.getKey() + ": ";
        String value = panelLabel.getValueFunc().apply(xpSnapshotSingle);
        return XpInfoBox.htmlLabel(key, value);
    }

    private static boolean isActions(XpPanelLabel panelLabel) {
        return panelLabel == XpPanelLabel.ACTIONS_LEFT || panelLabel == XpPanelLabel.ACTIONS_HOUR || panelLabel == XpPanelLabel.ACTIONS_DONE;
    }

    private static boolean isCombatSkill(Skill skill) {
        return skill == Skill.ATTACK || skill == Skill.STRENGTH || skill == Skill.DEFENCE || skill == Skill.HITPOINTS || skill == Skill.RANGED;
    }

    static String htmlLabel(String key, int value) {
        String valueStr = QuantityFormatter.quantityToRSDecimalStack(value, true);
        return XpInfoBox.htmlLabel(key, valueStr);
    }

    static String htmlLabel(String key, String valueStr) {
        return String.format(HTML_LABEL_TEMPLATE, ColorUtil.toHexColor(ColorScheme.LIGHT_GRAY_COLOR), key, valueStr);
    }

    Skill getSkill() {
        return this.skill;
    }

    static {
        TWO_DECIMAL_FORMAT.setRoundingMode(RoundingMode.DOWN);
        DEFAULT_PROGRESS_WRAPPER_BORDER = new EmptyBorder(0, 7, 7, 7);
        COMPACT_PROGRESS_WRAPPER_BORDER = new EmptyBorder(5, 1, 5, 5);
    }
}

