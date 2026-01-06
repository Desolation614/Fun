/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.Skill
 *  net.runelite.api.WorldType
 *  okhttp3.HttpUrl$Builder
 */
package net.runelite.client.plugins.xptracker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.xptracker.XpInfoBox;
import net.runelite.client.plugins.xptracker.XpSnapshotSingle;
import net.runelite.client.plugins.xptracker.XpTrackerConfig;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.DragAndDropReorderPane;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import okhttp3.HttpUrl;

class XpPanel
extends PluginPanel {
    private final Map<Skill, XpInfoBox> infoBoxes = new HashMap<Skill, XpInfoBox>();
    private final JLabel overallExpGained = new JLabel(XpInfoBox.htmlLabel("Gained: ", 0));
    private final JLabel overallExpHour = new JLabel(XpInfoBox.htmlLabel("Per hour: ", 0));
    private final JPanel overallPanel = new JPanel();
    private final PluginErrorPanel errorPanel = new PluginErrorPanel();

    XpPanel(XpTrackerPlugin xpTrackerPlugin, final XpTrackerConfig xpTrackerConfig, Client client, SkillIconManager iconManager) {
        this.setBorder(new EmptyBorder(6, 6, 6, 6));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.setLayout(new BorderLayout());
        JPanel layoutPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(layoutPanel, 1);
        layoutPanel.setLayout(boxLayout);
        this.add((Component)layoutPanel, "North");
        this.overallPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.overallPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.overallPanel.setLayout(new BorderLayout());
        this.overallPanel.setVisible(false);
        final JMenuItem openXpTracker = new JMenuItem("Open Wise Old Man");
        openXpTracker.addActionListener(e -> LinkBrowser.browse(XpPanel.buildXpTrackerUrl(client.getWorldType(), (Actor)client.getLocalPlayer(), null)));
        JMenuItem reset = new JMenuItem("Reset All");
        reset.addActionListener(e -> xpTrackerPlugin.resetAndInitState());
        JMenuItem resetPerHour = new JMenuItem("Reset All/hr");
        resetPerHour.addActionListener(e -> xpTrackerPlugin.resetAllSkillsPerHourState());
        JMenuItem pauseAll = new JMenuItem("Pause All");
        pauseAll.addActionListener(e -> xpTrackerPlugin.pauseAllSkills(true));
        JMenuItem unpauseAll = new JMenuItem("Unpause All");
        unpauseAll.addActionListener(e -> xpTrackerPlugin.pauseAllSkills(false));
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        popupMenu.add(openXpTracker);
        popupMenu.add(reset);
        popupMenu.add(resetPerHour);
        popupMenu.add(pauseAll);
        popupMenu.add(unpauseAll);
        popupMenu.addPopupMenuListener(new PopupMenuListener(){

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                openXpTracker.setVisible(xpTrackerConfig.wiseOldManOpenOption());
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
            }
        });
        this.overallPanel.setComponentPopupMenu(popupMenu);
        JLabel overallIcon = new JLabel(new ImageIcon(ImageUtil.loadImageResource(this.getClass(), "/skill_icons/overall.png")));
        JPanel overallInfo = new JPanel();
        overallInfo.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        overallInfo.setLayout(new GridLayout(2, 1));
        overallInfo.setBorder(new EmptyBorder(0, 10, 0, 0));
        this.overallExpGained.setFont(FontManager.getRunescapeSmallFont());
        this.overallExpHour.setFont(FontManager.getRunescapeSmallFont());
        overallInfo.add(this.overallExpGained);
        overallInfo.add(this.overallExpHour);
        this.overallPanel.add((Component)overallIcon, "West");
        this.overallPanel.add((Component)overallInfo, "Center");
        DragAndDropReorderPane infoBoxPanel = new DragAndDropReorderPane();
        infoBoxPanel.addDragListener(component -> {
            XpInfoBox c = (XpInfoBox)component;
            xpTrackerPlugin.updateSkillOrderState(c.getSkill(), infoBoxPanel.getPosition(component));
        });
        layoutPanel.add(this.overallPanel);
        layoutPanel.add(infoBoxPanel);
        for (Skill skill : Skill.values()) {
            this.infoBoxes.put(skill, new XpInfoBox(xpTrackerPlugin, xpTrackerConfig, client, infoBoxPanel, skill, iconManager));
        }
        this.errorPanel.setContent("Exp trackers", "You have not gained experience yet.");
        this.add(this.errorPanel);
    }

    static String buildXpTrackerUrl(Set<WorldType> worldTypes, Actor player, Skill skill) {
        if (player == null) {
            return "";
        }
        return new HttpUrl.Builder().scheme("https").host(worldTypes.contains(WorldType.SEASONAL) ? "league.wiseoldman.net" : "wiseoldman.net").addPathSegment("players").addPathSegment(player.getName()).addPathSegment("gained").addQueryParameter("metric", skill == null ? "overall" : skill.getName().toLowerCase()).addQueryParameter("period", "week").build().toString();
    }

    void resetAllInfoBoxes() {
        this.infoBoxes.forEach((skill, xpInfoBox) -> xpInfoBox.reset());
    }

    void resetSkill(Skill skill) {
        XpInfoBox xpInfoBox = this.infoBoxes.get(skill);
        xpInfoBox.reset();
    }

    void updateSkillExperience(boolean updated, boolean paused, Skill skill, XpSnapshotSingle xpSnapshotSingle) {
        XpInfoBox xpInfoBox = this.infoBoxes.get(skill);
        xpInfoBox.update(updated, paused, xpSnapshotSingle);
    }

    void updateTotal(XpSnapshotSingle xpSnapshotTotal) {
        if (xpSnapshotTotal.getXpGainedInSession() > 0 && !this.overallPanel.isVisible()) {
            this.overallPanel.setVisible(true);
            this.remove(this.errorPanel);
        } else if (xpSnapshotTotal.getXpGainedInSession() == 0 && this.overallPanel.isVisible()) {
            this.overallPanel.setVisible(false);
            this.add(this.errorPanel);
        }
        SwingUtilities.invokeLater(() -> this.rebuildAsync(xpSnapshotTotal));
    }

    private void rebuildAsync(XpSnapshotSingle xpSnapshotTotal) {
        this.overallExpGained.setText(XpInfoBox.htmlLabel("Gained: ", xpSnapshotTotal.getXpGainedInSession()));
        this.overallExpHour.setText(XpInfoBox.htmlLabel("Per hour: ", xpSnapshotTotal.getXpPerHour()));
    }
}

