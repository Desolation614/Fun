/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.config.ConfigPanel;
import net.runelite.client.plugins.config.PluginConfigurationDescriptor;
import net.runelite.client.plugins.config.PluginListPanel;
import net.runelite.client.plugins.config.PluginToggleButton;
import net.runelite.client.plugins.config.SearchablePlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

class PluginListItem
extends JPanel
implements SearchablePlugin {
    private static final ImageIcon ON_STAR;
    private static final ImageIcon OFF_STAR;
    private final PluginListPanel pluginListPanel;
    private final PluginConfigurationDescriptor pluginConfig;
    private final List<String> keywords = new ArrayList<String>();
    private final JToggleButton pinButton;
    private final PluginToggleButton onOffToggle;

    PluginListItem(PluginListPanel pluginListPanel, PluginConfigurationDescriptor pluginConfig) {
        this.pluginListPanel = pluginListPanel;
        this.pluginConfig = pluginConfig;
        Collections.addAll(this.keywords, pluginConfig.getName().toLowerCase().split(" "));
        Collections.addAll(this.keywords, pluginConfig.getDescription().toLowerCase().split(" "));
        Collections.addAll(this.keywords, pluginConfig.getTags());
        String internalName = pluginConfig.getInternalPluginHubName();
        if (internalName != null) {
            this.keywords.add("pluginhub");
            this.keywords.add(internalName);
        } else {
            this.keywords.add("plugin");
        }
        this.setLayout(new BorderLayout(3, 0));
        this.setPreferredSize(new Dimension(225, 20));
        JLabel nameLabel = new JLabel(pluginConfig.getName());
        nameLabel.setForeground(Color.WHITE);
        if (!pluginConfig.getDescription().isEmpty()) {
            nameLabel.setToolTipText("<html>" + pluginConfig.getName() + ":<br>" + pluginConfig.getDescription() + "</html>");
        }
        this.pinButton = new JToggleButton(OFF_STAR);
        this.pinButton.setSelectedIcon(ON_STAR);
        SwingUtil.removeButtonDecorations(this.pinButton);
        SwingUtil.addModalTooltip(this.pinButton, "Unpin plugin", "Pin plugin");
        this.pinButton.setPreferredSize(new Dimension(21, 0));
        this.add((Component)this.pinButton, "Before");
        this.pinButton.addActionListener(e -> {
            pluginListPanel.savePinnedPlugins();
            pluginListPanel.refresh();
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        this.add((Component)buttonPanel, "After");
        JMenuItem configMenuItem = null;
        if (pluginConfig.getConfigDescriptor() != null) {
            JButton configButton = new JButton(ConfigPanel.CONFIG_ICON);
            SwingUtil.removeButtonDecorations(configButton);
            configButton.setPreferredSize(new Dimension(25, 0));
            configButton.setVisible(false);
            buttonPanel.add(configButton);
            configButton.addActionListener(e -> {
                configButton.setIcon(ConfigPanel.CONFIG_ICON);
                this.openGroupConfigPanel();
            });
            configButton.setVisible(true);
            configButton.setToolTipText("Edit plugin configuration");
            configMenuItem = new JMenuItem("Configure");
            configMenuItem.addActionListener(e -> this.openGroupConfigPanel());
        }
        JMenuItem uninstallItem = null;
        if (internalName != null) {
            uninstallItem = new JMenuItem("Uninstall");
            uninstallItem.addActionListener(ev -> pluginListPanel.getExternalPluginManager().remove(internalName));
        }
        PluginListItem.addLabelPopupMenu(nameLabel, configMenuItem, pluginConfig.createSupportMenuItem(), uninstallItem);
        this.add((Component)nameLabel, "Center");
        this.onOffToggle = new PluginToggleButton();
        this.onOffToggle.setConflicts(pluginConfig.getConflicts());
        buttonPanel.add(this.onOffToggle);
        if (pluginConfig.getPlugin() != null) {
            this.onOffToggle.addActionListener(i -> {
                if (this.onOffToggle.isSelected()) {
                    pluginListPanel.startPlugin(pluginConfig.getPlugin());
                } else {
                    pluginListPanel.stopPlugin(pluginConfig.getPlugin());
                }
            });
        } else {
            this.onOffToggle.setVisible(false);
        }
    }

    @Override
    public String getSearchableName() {
        return this.pluginConfig.getName();
    }

    @Override
    public boolean isPinned() {
        return this.pinButton.isSelected();
    }

    void setPinned(boolean pinned) {
        this.pinButton.setSelected(pinned);
    }

    void setPluginEnabled(boolean enabled) {
        this.onOffToggle.setSelected(enabled);
    }

    private void openGroupConfigPanel() {
        this.pluginListPanel.openConfigurationPanel(this.pluginConfig);
    }

    static void addLabelPopupMenu(final JLabel label, JMenuItem ... menuItems) {
        final JPopupMenu menu = new JPopupMenu();
        Color labelForeground = label.getForeground();
        menu.setBorder(new EmptyBorder(5, 5, 5, 5));
        for (JMenuItem menuItem : menuItems) {
            if (menuItem == null) continue;
            menuItem.addActionListener(e -> label.setForeground(labelForeground));
            menu.add(menuItem);
        }
        label.addMouseListener(new MouseAdapter(){
            private Color lastForeground;

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Component source = (Component)mouseEvent.getSource();
                Point location = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(location, source);
                menu.show(source, location.x, location.y);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                this.lastForeground = label.getForeground();
                label.setForeground(ColorScheme.BRAND_ORANGE);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                label.setForeground(this.lastForeground);
            }
        });
    }

    public PluginConfigurationDescriptor getPluginConfig() {
        return this.pluginConfig;
    }

    @Override
    public List<String> getKeywords() {
        return this.keywords;
    }

    static {
        BufferedImage onStar = ImageUtil.loadImageResource(ConfigPanel.class, "star_on.png");
        ON_STAR = new ImageIcon(onStar);
        BufferedImage offStar = ImageUtil.luminanceScale(ImageUtil.grayscaleImage(onStar), 0.77f);
        OFF_STAR = new ImageIcon(offStar);
    }
}

