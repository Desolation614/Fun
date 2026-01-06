/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Provider
 *  javax.inject.Singleton
 */
package net.runelite.client.plugins.config;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.config.PluginHubPanel;
import net.runelite.client.plugins.config.PluginListPanel;
import net.runelite.client.plugins.config.ProfilePanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.ImageUtil;

@Singleton
class TopLevelConfigPanel
extends PluginPanel {
    private final MaterialTabGroup tabGroup;
    private final CardLayout layout;
    private final JPanel content;
    private final EventBus eventBus;
    private final PluginListPanel pluginListPanel;
    private final MaterialTab pluginListPanelTab;
    private boolean active = false;
    private PluginPanel current;
    private boolean removeOnTabChange;

    @Inject
    TopLevelConfigPanel(EventBus eventBus, PluginListPanel pluginListPanel, ProfilePanel profilePanel, Provider<PluginHubPanel> pluginHubPanelProvider) {
        super(false);
        this.eventBus = eventBus;
        this.tabGroup = new MaterialTabGroup();
        this.tabGroup.setLayout(new GridLayout(1, 0, 7, 7));
        this.tabGroup.setBorder(new EmptyBorder(10, 10, 0, 10));
        this.content = new JPanel();
        this.layout = new CardLayout();
        this.content.setLayout(this.layout);
        this.setLayout(new BorderLayout());
        this.add((Component)this.tabGroup, "North");
        this.add((Component)this.content, "Center");
        this.pluginListPanel = pluginListPanel;
        this.pluginListPanelTab = this.addTab(pluginListPanel.getMuxer(), "config_icon_lg.png", "Configuration");
        this.addTab(profilePanel, "profile_icon.png", "Profiles");
        this.addTab(pluginHubPanelProvider, "plugin_hub_icon.png", "Plugin Hub");
        this.tabGroup.select(this.pluginListPanelTab);
    }

    private MaterialTab addTab(PluginPanel panel, String image, String tooltip) {
        MaterialTab mt = new MaterialTab(new ImageIcon(ImageUtil.loadImageResource(TopLevelConfigPanel.class, image)), this.tabGroup, null);
        mt.setToolTipText(tooltip);
        this.tabGroup.addTab(mt);
        this.content.add(image, panel.getWrappedPanel());
        this.eventBus.register(panel);
        mt.setOnSelectEvent(() -> {
            this.switchTo(image, panel, false);
            return true;
        });
        return mt;
    }

    private MaterialTab addTab(Provider<? extends PluginPanel> panelProvider, String image, String tooltip) {
        MaterialTab mt = new MaterialTab(new ImageIcon(ImageUtil.loadImageResource(TopLevelConfigPanel.class, image)), this.tabGroup, null);
        mt.setToolTipText(tooltip);
        this.tabGroup.addTab(mt);
        mt.setOnSelectEvent(() -> {
            PluginPanel panel = (PluginPanel)panelProvider.get();
            this.content.add(image, panel.getWrappedPanel());
            this.eventBus.register(panel);
            this.switchTo(image, panel, true);
            return true;
        });
        return mt;
    }

    private void switchTo(String cardName, PluginPanel panel, boolean removeOnTabChange) {
        boolean doRemove = this.removeOnTabChange;
        PluginPanel prevPanel = this.current;
        if (this.active) {
            prevPanel.onDeactivate();
            panel.onActivate();
        }
        this.current = panel;
        this.removeOnTabChange = removeOnTabChange;
        this.layout.show(this.content, cardName);
        if (doRemove) {
            this.content.remove(prevPanel.getWrappedPanel());
            this.eventBus.unregister(prevPanel);
        }
        this.content.revalidate();
    }

    @Override
    public void onActivate() {
        this.active = true;
        this.current.onActivate();
    }

    @Override
    public void onDeactivate() {
        this.active = false;
        this.current.onDeactivate();
    }

    public void openConfigurationPanel(String name) {
        this.tabGroup.select(this.pluginListPanelTab);
        this.pluginListPanel.openConfigurationPanel(name);
    }

    public void openConfigurationPanel(Plugin plugin) {
        this.tabGroup.select(this.pluginListPanelTab);
        this.pluginListPanel.openConfigurationPanel(plugin);
    }

    public void openWithFilter(String filter) {
        this.tabGroup.select(this.pluginListPanelTab);
        this.pluginListPanel.openWithFilter(filter);
    }
}

