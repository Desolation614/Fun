/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.google.inject.name.Named
 *  javax.annotation.Nullable
 */
package net.runelite.client.plugins.timetracking;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.OverviewTabPanel;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.TabContentPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.clocks.ClockManager;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import net.runelite.client.plugins.timetracking.farming.FarmingNextTickPanel;
import net.runelite.client.plugins.timetracking.farming.FarmingTracker;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseTracker;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.AsyncBufferedImage;

class TimeTrackingPanel
extends PluginPanel {
    private final ItemManager itemManager;
    private final TimeTrackingConfig config;
    private final JPanel display = new JPanel();
    private final Map<Tab, MaterialTab> uiTabs = new HashMap<Tab, MaterialTab>();
    private final MaterialTabGroup tabGroup = new MaterialTabGroup(this.display);
    private boolean active;
    @Nullable
    private TabContentPanel activeTabPanel = null;

    @Inject
    TimeTrackingPanel(ItemManager itemManager, TimeTrackingConfig config, FarmingTracker farmingTracker, BirdHouseTracker birdHouseTracker, ClockManager clockManager, FarmingContractManager farmingContractManager, ConfigManager configManager, @Named(value="developerMode") boolean developerMode) {
        super(false);
        this.itemManager = itemManager;
        this.config = config;
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.display.setBorder(new EmptyBorder(10, 10, 8, 10));
        this.tabGroup.setLayout(new GridLayout(0, 6, 7, 7));
        this.tabGroup.setBorder(new EmptyBorder(10, 10, 0, 10));
        this.add((Component)this.tabGroup, "North");
        this.add((Component)this.display, "Center");
        this.addTab(Tab.OVERVIEW, new OverviewTabPanel(itemManager, config, this, farmingTracker, birdHouseTracker, clockManager, farmingContractManager));
        this.addTab(Tab.CLOCK, clockManager.getClockTabPanel());
        this.addTab(Tab.BIRD_HOUSE, birdHouseTracker.createBirdHouseTabPanel());
        for (Tab tab : Tab.FARMING_TABS) {
            this.addTab(tab, farmingTracker.createTabPanel(tab, farmingContractManager));
        }
        if (developerMode) {
            this.addTab(Tab.TIME_OFFSET, new FarmingNextTickPanel(farmingTracker, config, configManager));
        }
    }

    private void addTab(Tab tab, TabContentPanel tabContentPanel) {
        JPanel wrapped = new JPanel(new BorderLayout());
        wrapped.add((Component)tabContentPanel, "North");
        wrapped.setBackground(ColorScheme.DARK_GRAY_COLOR);
        JScrollPane scroller = new JScrollPane(wrapped);
        scroller.setHorizontalScrollBarPolicy(31);
        scroller.getVerticalScrollBar().setPreferredSize(new Dimension(16, 0));
        scroller.getVerticalScrollBar().setBorder(new EmptyBorder(0, 9, 0, 0));
        scroller.setBackground(ColorScheme.DARK_GRAY_COLOR);
        MaterialTab materialTab = new MaterialTab(new ImageIcon(), this.tabGroup, (JComponent)scroller);
        materialTab.setPreferredSize(new Dimension(30, 27));
        materialTab.setName(tab.getName());
        materialTab.setToolTipText(tab.getName());
        AsyncBufferedImage icon = this.itemManager.getImage(tab.getItemID());
        icon.onLoaded(() -> {
            BufferedImage subIcon = icon.getSubimage(0, 0, 32, 32);
            materialTab.setIcon(new ImageIcon(subIcon.getScaledInstance(24, 24, 4)));
        });
        materialTab.setOnSelectEvent(() -> {
            this.config.setActiveTab(tab);
            this.activeTabPanel = tabContentPanel;
            tabContentPanel.update();
            return true;
        });
        this.uiTabs.put(tab, materialTab);
        this.tabGroup.addTab(materialTab);
        if (this.config.activeTab() == tab) {
            this.tabGroup.select(materialTab);
        }
    }

    void switchTab(Tab tab) {
        this.tabGroup.select(this.uiTabs.get((Object)tab));
    }

    int getUpdateInterval() {
        return this.activeTabPanel == null ? Integer.MAX_VALUE : this.activeTabPanel.getUpdateInterval();
    }

    void update() {
        if (!this.active || this.activeTabPanel == null) {
            return;
        }
        SwingUtilities.invokeLater(this.activeTabPanel::update);
    }

    @Override
    public void onActivate() {
        this.active = true;
        this.update();
    }

    @Override
    public void onDeactivate() {
        this.active = false;
    }
}

