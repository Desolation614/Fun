/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.inject.Inject
 *  javax.inject.Provider
 *  javax.inject.Singleton
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.config;

import com.google.common.collect.ImmutableList;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigDescriptor;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ExternalPluginsChanged;
import net.runelite.client.events.PluginChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.config.ConfigPanel;
import net.runelite.client.plugins.config.FixedWidthPanel;
import net.runelite.client.plugins.config.PluginConfigurationDescriptor;
import net.runelite.client.plugins.config.PluginListItem;
import net.runelite.client.plugins.config.PluginSearch;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class PluginListPanel
extends PluginPanel {
    private static final Logger log = LoggerFactory.getLogger(PluginListPanel.class);
    private static final String RUNELITE_GROUP_NAME = RuneLiteConfig.class.getAnnotation(ConfigGroup.class).value();
    private static final String PINNED_PLUGINS_CONFIG_KEY = "pinnedPlugins";
    private static final ImmutableList<String> CATEGORY_TAGS = ImmutableList.of((Object)"Combat", (Object)"Chat", (Object)"Item", (Object)"Minigame", (Object)"Notification", (Object)"Plugin Hub", (Object)"Skilling", (Object)"XP");
    private final ConfigManager configManager;
    private final PluginManager pluginManager;
    private final Provider<ConfigPanel> configPanelProvider;
    private final List<PluginConfigurationDescriptor> fakePlugins = new ArrayList<PluginConfigurationDescriptor>();
    private final ExternalPluginManager externalPluginManager;
    private final MultiplexingPluginPanel muxer;
    private final IconTextField searchBar;
    private final JScrollPane scrollPane;
    private final FixedWidthPanel mainPanel;
    private List<PluginListItem> pluginList;

    @Inject
    public PluginListPanel(ConfigManager configManager, PluginManager pluginManager, ExternalPluginManager externalPluginManager, final EventBus eventBus, Provider<ConfigPanel> configPanelProvider) {
        super(false);
        this.configManager = configManager;
        this.pluginManager = pluginManager;
        this.externalPluginManager = externalPluginManager;
        this.configPanelProvider = configPanelProvider;
        this.muxer = new MultiplexingPluginPanel(this){

            @Override
            protected void onAdd(PluginPanel p) {
                eventBus.register(p);
            }

            @Override
            protected void onRemove(PluginPanel p) {
                eventBus.unregister(p);
            }
        };
        this.searchBar = new IconTextField();
        this.searchBar.setIcon(IconTextField.Icon.SEARCH);
        this.searchBar.setPreferredSize(new Dimension(205, 30));
        this.searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        this.searchBar.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent e) {
                PluginListPanel.this.onSearchBarChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                PluginListPanel.this.onSearchBarChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                PluginListPanel.this.onSearchBarChanged();
            }
        });
        CATEGORY_TAGS.forEach(this.searchBar.getSuggestionListModel()::addElement);
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new BorderLayout(0, 6));
        topPanel.add((Component)this.searchBar, "Center");
        this.add((Component)topPanel, "North");
        this.mainPanel = new FixedWidthPanel();
        this.mainPanel.setBorder(new EmptyBorder(8, 10, 10, 10));
        this.mainPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));
        this.mainPanel.setAlignmentX(0.0f);
        FixedWidthPanel northPanel = new FixedWidthPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add((Component)this.mainPanel, "North");
        this.scrollPane = new JScrollPane(northPanel);
        this.scrollPane.setHorizontalScrollBarPolicy(31);
        this.add((Component)this.scrollPane, "Center");
    }

    void rebuildPluginList() {
        List<String> pinnedPlugins = this.getPinnedPluginNames();
        this.pluginList = Stream.concat(this.fakePlugins.stream(), this.pluginManager.getPlugins().stream().filter(plugin -> !plugin.getClass().getAnnotation(PluginDescriptor.class).hidden()).map(plugin -> {
            PluginDescriptor descriptor = plugin.getClass().getAnnotation(PluginDescriptor.class);
            Config config = this.pluginManager.getPluginConfigProxy((Plugin)plugin);
            ConfigDescriptor configDescriptor = config == null ? null : this.configManager.getConfigDescriptor(config);
            List<String> conflicts = this.pluginManager.conflictsForPlugin((Plugin)plugin).stream().map(Plugin::getName).collect(Collectors.toList());
            return new PluginConfigurationDescriptor(descriptor.name(), descriptor.description(), descriptor.tags(), (Plugin)plugin, config, configDescriptor, conflicts);
        })).map(desc -> {
            PluginListItem listItem = new PluginListItem(this, (PluginConfigurationDescriptor)desc);
            listItem.setPinned(pinnedPlugins.contains(desc.getName()));
            return listItem;
        }).sorted(Comparator.comparing(p -> p.getPluginConfig().getName())).collect(Collectors.toList());
        this.mainPanel.removeAll();
        this.refresh();
    }

    void addFakePlugin(PluginConfigurationDescriptor ... descriptor) {
        Collections.addAll(this.fakePlugins, descriptor);
    }

    void refresh() {
        this.pluginList.forEach(listItem -> {
            Plugin plugin = listItem.getPluginConfig().getPlugin();
            if (plugin != null) {
                listItem.setPluginEnabled(this.pluginManager.isPluginEnabled(plugin));
            }
        });
        int scrollBarPosition = this.scrollPane.getVerticalScrollBar().getValue();
        this.onSearchBarChanged();
        this.searchBar.requestFocusInWindow();
        this.validate();
        this.scrollPane.getVerticalScrollBar().setValue(scrollBarPosition);
    }

    void openWithFilter(String filter) {
        this.searchBar.setText(filter);
        this.onSearchBarChanged();
        this.muxer.pushState(this);
    }

    private void onSearchBarChanged() {
        String text = this.searchBar.getText();
        this.pluginList.forEach(this.mainPanel::remove);
        PluginSearch.search(this.pluginList, text).forEach(this.mainPanel::add);
        this.revalidate();
    }

    void openConfigurationPanel(String configGroup) {
        for (PluginListItem pluginListItem : this.pluginList) {
            if (!pluginListItem.getPluginConfig().getName().equals(configGroup)) continue;
            this.openConfigurationPanel(pluginListItem.getPluginConfig());
            break;
        }
    }

    void openConfigurationPanel(Plugin plugin) {
        for (PluginListItem pluginListItem : this.pluginList) {
            if (pluginListItem.getPluginConfig().getPlugin() != plugin) continue;
            this.openConfigurationPanel(pluginListItem.getPluginConfig());
            break;
        }
    }

    void openConfigurationPanel(PluginConfigurationDescriptor plugin) {
        ConfigPanel panel = (ConfigPanel)this.configPanelProvider.get();
        panel.init(plugin);
        this.muxer.pushState(this);
        this.muxer.pushState(panel);
    }

    void startPlugin(Plugin plugin) {
        this.pluginManager.setPluginEnabled(plugin, true);
        try {
            this.pluginManager.startPlugin(plugin);
        }
        catch (PluginInstantiationException ex) {
            log.warn("Error when starting plugin {}", (Object)plugin.getClass().getSimpleName(), (Object)ex);
        }
    }

    void stopPlugin(Plugin plugin) {
        this.pluginManager.setPluginEnabled(plugin, false);
        try {
            this.pluginManager.stopPlugin(plugin);
        }
        catch (PluginInstantiationException ex) {
            log.warn("Error when stopping plugin {}", (Object)plugin.getClass().getSimpleName(), (Object)ex);
        }
    }

    private List<String> getPinnedPluginNames() {
        String config = this.configManager.getConfiguration(RUNELITE_GROUP_NAME, PINNED_PLUGINS_CONFIG_KEY);
        if (config == null) {
            return List.of("Ferox");
        }
        return Text.fromCSV(config);
    }

    void savePinnedPlugins() {
        String value = this.pluginList.stream().filter(PluginListItem::isPinned).map(p -> p.getPluginConfig().getName()).collect(Collectors.joining(","));
        this.configManager.setConfiguration(RUNELITE_GROUP_NAME, PINNED_PLUGINS_CONFIG_KEY, value);
    }

    @Subscribe
    public void onPluginChanged(PluginChanged event) {
        SwingUtilities.invokeLater(this::refresh);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(242, super.getPreferredSize().height);
    }

    @Override
    public void onActivate() {
        super.onActivate();
        if (this.searchBar.getParent() != null) {
            this.searchBar.requestFocusInWindow();
        }
    }

    @Subscribe
    private void onExternalPluginsChanged(ExternalPluginsChanged ev) {
        SwingUtilities.invokeLater(this::rebuildPluginList);
    }

    @Subscribe
    private void onProfileChanged(ProfileChanged ev) {
        SwingUtilities.invokeLater(this::rebuildPluginList);
    }

    public ExternalPluginManager getExternalPluginManager() {
        return this.externalPluginManager;
    }

    public MultiplexingPluginPanel getMuxer() {
        return this.muxer;
    }
}

