/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Strings
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Sets
 *  com.google.common.primitives.Ints
 *  javax.inject.Inject
 *  javax.inject.Provider
 *  org.apache.commons.lang3.ArrayUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.config;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import net.runelite.client.config.ConfigDescriptor;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigItemDescriptor;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.ConfigObject;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigSectionDescriptor;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.ModifierlessKeybind;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ExternalPluginsChanged;
import net.runelite.client.events.PluginChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.config.FixedWidthPanel;
import net.runelite.client.plugins.config.HotkeyButton;
import net.runelite.client.plugins.config.NotificationPanel;
import net.runelite.client.plugins.config.PluginConfigurationDescriptor;
import net.runelite.client.plugins.config.PluginListItem;
import net.runelite.client.plugins.config.PluginListPanel;
import net.runelite.client.plugins.config.PluginToggleButton;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.UnitFormatterFactory;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.TitleCaseListCellRenderer;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConfigPanel
extends PluginPanel {
    private static final Logger log = LoggerFactory.getLogger(ConfigPanel.class);
    private static final int SPINNER_FIELD_WIDTH = 6;
    private static final ImageIcon SECTION_EXPAND_ICON;
    private static final ImageIcon SECTION_RETRACT_ICON;
    static final ImageIcon CONFIG_ICON;
    static final ImageIcon BACK_ICON;
    private static final Map<ConfigSectionDescriptor, Boolean> sectionExpandStates;
    private final PluginListPanel pluginList;
    private final ConfigManager configManager;
    private final PluginManager pluginManager;
    private final ExternalPluginManager externalPluginManager;
    private final ColorPickerManager colorPickerManager;
    private final Provider<NotificationPanel> notificationPanelProvider;
    private final TitleCaseListCellRenderer listCellRenderer = new TitleCaseListCellRenderer();
    private final FixedWidthPanel mainPanel;
    private final JLabel title;
    private final PluginToggleButton pluginToggle;
    private PluginConfigurationDescriptor pluginConfig = null;

    @Inject
    private ConfigPanel(PluginListPanel pluginList, ConfigManager configManager, PluginManager pluginManager, ExternalPluginManager externalPluginManager, ColorPickerManager colorPickerManager, Provider<NotificationPanel> notificationPanelProvider) {
        super(false);
        this.pluginList = pluginList;
        this.configManager = configManager;
        this.pluginManager = pluginManager;
        this.externalPluginManager = externalPluginManager;
        this.colorPickerManager = colorPickerManager;
        this.notificationPanelProvider = notificationPanelProvider;
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new BorderLayout(0, 6));
        this.add((Component)topPanel, "North");
        this.mainPanel = new FixedWidthPanel();
        this.mainPanel.setBorder(new EmptyBorder(8, 10, 10, 10));
        this.mainPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));
        this.mainPanel.setAlignmentX(0.0f);
        FixedWidthPanel northPanel = new FixedWidthPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add((Component)this.mainPanel, "North");
        JScrollPane scrollPane = new JScrollPane(northPanel);
        scrollPane.setHorizontalScrollBarPolicy(31);
        this.add((Component)scrollPane, "Center");
        JButton topPanelBackButton = new JButton(BACK_ICON);
        SwingUtil.removeButtonDecorations(topPanelBackButton);
        topPanelBackButton.setPreferredSize(new Dimension(22, 0));
        topPanelBackButton.setBorder(new EmptyBorder(0, 0, 0, 5));
        topPanelBackButton.addActionListener(e -> pluginList.getMuxer().popState());
        topPanelBackButton.setToolTipText("Back");
        topPanel.add((Component)topPanelBackButton, "West");
        this.pluginToggle = new PluginToggleButton();
        topPanel.add((Component)this.pluginToggle, "East");
        this.title = new JLabel();
        this.title.setForeground(Color.WHITE);
        topPanel.add(this.title);
    }

    void init(PluginConfigurationDescriptor pluginConfig) {
        assert (this.pluginConfig == null);
        this.pluginConfig = pluginConfig;
        String name = pluginConfig.getName();
        this.title.setText(name);
        this.title.setForeground(Color.WHITE);
        this.title.setToolTipText("<html>" + name + ":<br>" + pluginConfig.getDescription() + "</html>");
        String iname = pluginConfig.getInternalPluginHubName();
        JMenuItem uninstallItem = null;
        if (iname != null) {
            uninstallItem = new JMenuItem("Uninstall");
            uninstallItem.addActionListener(ev -> this.externalPluginManager.remove(iname));
        }
        PluginListItem.addLabelPopupMenu(this.title, pluginConfig.createSupportMenuItem(), uninstallItem);
        if (pluginConfig.getPlugin() != null) {
            this.pluginToggle.setConflicts(pluginConfig.getConflicts());
            this.pluginToggle.setSelected(this.pluginManager.isPluginEnabled(pluginConfig.getPlugin()));
            this.pluginToggle.addItemListener(i -> {
                if (this.pluginToggle.isSelected()) {
                    this.pluginList.startPlugin(pluginConfig.getPlugin());
                } else {
                    this.pluginList.stopPlugin(pluginConfig.getPlugin());
                }
            });
        } else {
            this.pluginToggle.setVisible(false);
        }
        this.rebuild();
    }

    private void toggleSection(ConfigSectionDescriptor csd, JButton button, JPanel contents) {
        boolean newState = !contents.isVisible();
        contents.setVisible(newState);
        button.setIcon(newState ? SECTION_RETRACT_ICON : SECTION_EXPAND_ICON);
        button.setToolTipText(newState ? "Retract" : "Expand");
        sectionExpandStates.put(csd, newState);
        SwingUtilities.invokeLater(contents::revalidate);
    }

    private void rebuild() {
        this.mainPanel.removeAll();
        ConfigDescriptor cd = this.pluginConfig.getConfigDescriptor();
        HashMap<String, JPanel> sectionWidgets = new HashMap<String, JPanel>();
        TreeMap<ConfigObject, JPanel> topLevelPanels = new TreeMap<ConfigObject, JPanel>((a, b) -> ComparisonChain.start().compare(a.position(), b.position()).compare((Comparable)((Object)a.name()), (Comparable)((Object)b.name())).result());
        Iterator<ConfigObject> iterator = cd.getSections().iterator();
        while (iterator.hasNext()) {
            ConfigSectionDescriptor csd;
            ConfigSection cs = (csd = iterator.next()).getSection();
            boolean isOpen = sectionExpandStates.getOrDefault(csd, !cs.closedByDefault());
            JPanel section = new JPanel();
            section.setLayout(new BoxLayout(section, 1));
            section.setMinimumSize(new Dimension(225, 0));
            JPanel sectionHeader = new JPanel();
            sectionHeader.setLayout(new BorderLayout());
            sectionHeader.setMinimumSize(new Dimension(225, 0));
            sectionHeader.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, ColorScheme.MEDIUM_GRAY_COLOR), new EmptyBorder(0, 0, 3, 1)));
            section.add((Component)sectionHeader, "North");
            final JButton sectionToggle = new JButton(isOpen ? SECTION_RETRACT_ICON : SECTION_EXPAND_ICON);
            sectionToggle.setPreferredSize(new Dimension(18, 0));
            sectionToggle.setBorder(new EmptyBorder(0, 0, 0, 5));
            sectionToggle.setToolTipText(isOpen ? "Retract" : "Expand");
            SwingUtil.removeButtonDecorations(sectionToggle);
            sectionHeader.add((Component)sectionToggle, "West");
            String name = cs.name();
            JLabel sectionName = new JLabel(name);
            sectionName.setForeground(ColorScheme.BRAND_ORANGE);
            sectionName.setFont(FontManager.getRunescapeBoldFont());
            sectionName.setToolTipText("<html>" + name + ":<br>" + cs.description() + "</html>");
            sectionHeader.add((Component)sectionName, "Center");
            final JPanel sectionContents = new JPanel();
            sectionContents.setLayout(new DynamicGridLayout(0, 1, 0, 5));
            sectionContents.setMinimumSize(new Dimension(225, 0));
            sectionContents.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, ColorScheme.MEDIUM_GRAY_COLOR), new EmptyBorder(6, 0, 6, 0)));
            sectionContents.setVisible(isOpen);
            section.add((Component)sectionContents, "South");
            MouseAdapter adapter = new MouseAdapter(){

                @Override
                public void mouseClicked(MouseEvent e) {
                    ConfigPanel.this.toggleSection(csd, sectionToggle, sectionContents);
                }
            };
            sectionToggle.addActionListener(actionEvent -> this.toggleSection(csd, sectionToggle, sectionContents));
            sectionName.addMouseListener(adapter);
            sectionHeader.addMouseListener(adapter);
            sectionWidgets.put(csd.getKey(), sectionContents);
            topLevelPanels.put(csd, section);
        }
        for (ConfigItemDescriptor cid : cd.getItems()) {
            ParameterizedType parameterizedType;
            if (cid.getItem().hidden()) continue;
            JPanel item = new JPanel();
            item.setLayout(new BorderLayout());
            item.setMinimumSize(new Dimension(225, 0));
            String name = cid.getItem().name();
            JLabel configEntryName = new JLabel(name);
            configEntryName.setForeground(Color.WHITE);
            String description = cid.getItem().description();
            if (!"".equals(description)) {
                configEntryName.setToolTipText("<html>" + name + ":<br>" + description + "</html>");
            }
            PluginListItem.addLabelPopupMenu(configEntryName, this.createResetMenuItem(this.pluginConfig, cid));
            item.add((Component)configEntryName, "Center");
            if (cid.getType() == Boolean.TYPE) {
                item.add((Component)this.createCheckbox(cd, cid), "East");
            } else if (cid.getType() == Integer.TYPE) {
                item.add((Component)this.createIntSpinner(cd, cid), "East");
            } else if (cid.getType() == Double.TYPE) {
                item.add((Component)this.createDoubleSpinner(cd, cid), "East");
            } else if (cid.getType() == String.class) {
                item.add((Component)this.createTextField(cd, cid), "South");
            } else if (cid.getType() == Color.class) {
                item.add((Component)this.createColorPicker(cd, cid), "East");
            } else if (cid.getType() == Dimension.class) {
                item.add((Component)this.createDimension(cd, cid), "East");
            } else if (cid.getType() instanceof Class && ((Class)cid.getType()).isEnum()) {
                item.add(this.createComboBox(cd, cid), "East");
            } else if (cid.getType() == Keybind.class || cid.getType() == ModifierlessKeybind.class) {
                item.add((Component)this.createKeybind(cd, cid), "East");
            } else if (cid.getType() == Notification.class) {
                item.add((Component)this.createNotification(cd, cid), "East");
            } else if (cid.getType() instanceof ParameterizedType && (parameterizedType = (ParameterizedType)cid.getType()).getRawType() == Set.class) {
                item.add(this.createList(cd, cid), "East");
            }
            JPanel section = (JPanel)sectionWidgets.get(cid.getItem().section());
            if (section == null) {
                topLevelPanels.put(cid, item);
                continue;
            }
            section.add(item);
        }
        topLevelPanels.values().forEach(this.mainPanel::add);
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            int result = JOptionPane.showOptionDialog(resetButton, "Are you sure you want to reset this plugin's configuration?", "Are you sure?", 0, 2, null, new String[]{"Yes", "No"}, "No");
            if (result == 0) {
                this.configManager.setDefaultConfiguration(this.pluginConfig.getConfig(), true);
                Plugin plugin = this.pluginConfig.getPlugin();
                if (plugin != null) {
                    plugin.resetConfiguration();
                }
                this.rebuild();
            }
        });
        this.mainPanel.add(resetButton);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> this.pluginList.getMuxer().popState());
        this.mainPanel.add(backButton);
        this.revalidate();
    }

    private JCheckBox createCheckbox(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        JCheckBox checkbox = new JCheckBox();
        checkbox.setSelected(Boolean.parseBoolean(this.configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName())));
        checkbox.addActionListener(ae -> this.changeConfiguration(checkbox, cd, cid));
        return checkbox;
    }

    private JSpinner createIntSpinner(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        int value = (Integer)MoreObjects.firstNonNull((Object)((Integer)this.configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), Integer.TYPE)), (Object)0);
        Range range = cid.getRange();
        int min = 0;
        int max = Integer.MAX_VALUE;
        if (range != null) {
            min = range.min();
            max = range.max();
        }
        value = Ints.constrainToRange((int)value, (int)min, (int)max);
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, 1);
        JSpinner spinner = new JSpinner(model);
        JComponent editor = spinner.getEditor();
        JFormattedTextField spinnerTextField = ((JSpinner.DefaultEditor)editor).getTextField();
        spinnerTextField.setColumns(6);
        spinner.addChangeListener(ce -> this.changeConfiguration(spinner, cd, cid));
        Units units = cid.getUnits();
        if (units != null) {
            JFormattedTextField.AbstractFormatterFactory delegate = spinnerTextField.getFormatterFactory();
            spinnerTextField.setFormatterFactory(new UnitFormatterFactory(delegate, units.value()));
        }
        return spinner;
    }

    private JSpinner createDoubleSpinner(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        double value = (Double)MoreObjects.firstNonNull((Object)((Double)this.configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), Double.TYPE)), (Object)0.0);
        SpinnerNumberModel model = new SpinnerNumberModel(value, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner spinner = new JSpinner(model);
        JComponent editor = spinner.getEditor();
        JFormattedTextField spinnerTextField = ((JSpinner.DefaultEditor)editor).getTextField();
        spinnerTextField.setColumns(6);
        spinner.addChangeListener(ce -> this.changeConfiguration(spinner, cd, cid));
        Units units = cid.getUnits();
        if (units != null) {
            JFormattedTextField.AbstractFormatterFactory delegate = spinnerTextField.getFormatterFactory();
            spinnerTextField.setFormatterFactory(new UnitFormatterFactory(delegate, units.value()));
        }
        return spinner;
    }

    private JTextComponent createTextField(final ConfigDescriptor cd, final ConfigItemDescriptor cid) {
        JTextComponent textField;
        if (cid.getItem().secret()) {
            textField = new JPasswordField();
        } else {
            JTextArea textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textField = textArea;
        }
        textField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textField.setText(this.configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName()));
        textField.addFocusListener(new FocusAdapter(){

            @Override
            public void focusLost(FocusEvent e) {
                ConfigPanel.this.changeConfiguration(textField, cd, cid);
            }
        });
        return textField;
    }

    private ColorJButton createColorPicker(final ConfigDescriptor cd, final ConfigItemDescriptor cid) {
        ColorJButton colorPickerBtn;
        boolean alphaHidden;
        Color existing = (Color)this.configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), (Type)((Object)Color.class));
        boolean bl = alphaHidden = cid.getAlpha() == null;
        if (existing == null) {
            colorPickerBtn = new ColorJButton("Pick a color", Color.BLACK);
        } else {
            String colorHex = "#" + (alphaHidden ? ColorUtil.colorToHexCode(existing) : ColorUtil.colorToAlphaHexCode(existing)).toUpperCase();
            colorPickerBtn = new ColorJButton(colorHex, existing);
        }
        colorPickerBtn.setFocusable(false);
        colorPickerBtn.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                RuneliteColorPicker colorPicker = ConfigPanel.this.colorPickerManager.create(ConfigPanel.this, colorPickerBtn.getColor(), cid.getItem().name(), alphaHidden);
                colorPicker.setLocationRelativeTo(colorPickerBtn);
                colorPicker.setOnColorChange(c -> {
                    colorPickerBtn.setColor((Color)c);
                    colorPickerBtn.setText("#" + (alphaHidden ? ColorUtil.colorToHexCode(c) : ColorUtil.colorToAlphaHexCode(c)).toUpperCase());
                });
                colorPicker.setOnClose(c -> ConfigPanel.this.changeConfiguration(colorPicker, cd, cid));
                colorPicker.setVisible(true);
            }
        });
        return colorPickerBtn;
    }

    private JPanel createDimension(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        JPanel dimensionPanel = new JPanel();
        dimensionPanel.setLayout(new BorderLayout());
        Dimension dimension = (Dimension)MoreObjects.firstNonNull((Object)((Dimension)this.configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), (Type)((Object)Dimension.class))), (Object)new Dimension());
        int width = dimension.width;
        int height = dimension.height;
        SpinnerNumberModel widthModel = new SpinnerNumberModel(width, 0, Integer.MAX_VALUE, 1);
        JSpinner widthSpinner = new JSpinner(widthModel);
        JComponent widthEditor = widthSpinner.getEditor();
        JFormattedTextField widthSpinnerTextField = ((JSpinner.DefaultEditor)widthEditor).getTextField();
        widthSpinnerTextField.setColumns(4);
        SpinnerNumberModel heightModel = new SpinnerNumberModel(height, 0, Integer.MAX_VALUE, 1);
        JSpinner heightSpinner = new JSpinner(heightModel);
        JComponent heightEditor = heightSpinner.getEditor();
        JFormattedTextField heightSpinnerTextField = ((JSpinner.DefaultEditor)heightEditor).getTextField();
        heightSpinnerTextField.setColumns(4);
        ChangeListener listener = e -> this.configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), String.valueOf(widthSpinner.getValue()) + "x" + String.valueOf(heightSpinner.getValue()));
        widthSpinner.addChangeListener(listener);
        heightSpinner.addChangeListener(listener);
        dimensionPanel.add((Component)widthSpinner, "West");
        dimensionPanel.add((Component)new JLabel(" x "), "Center");
        dimensionPanel.add((Component)heightSpinner, "East");
        return dimensionPanel;
    }

    private JComboBox<Enum<?>> createComboBox(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        Class type = (Class)cid.getType();
        JComboBox box = new JComboBox((Enum[])type.getEnumConstants());
        box.setRenderer(this.listCellRenderer);
        box.setPreferredSize(new Dimension(box.getPreferredSize().width, 22));
        try {
            Object selectedItem = Enum.valueOf(type, this.configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName()));
            box.setSelectedItem(selectedItem);
            box.setToolTipText(Text.titleCase(selectedItem));
        }
        catch (IllegalArgumentException ex) {
            log.debug("invalid selected item", (Throwable)ex);
        }
        box.addItemListener(e -> {
            if (e.getStateChange() == 1) {
                this.changeConfiguration(box, cd, cid);
                box.setToolTipText(Text.titleCase((Enum)box.getSelectedItem()));
            }
        });
        return box;
    }

    private HotkeyButton createKeybind(final ConfigDescriptor cd, final ConfigItemDescriptor cid) {
        Keybind startingValue = (Keybind)this.configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), (Class)cid.getType());
        final HotkeyButton button = new HotkeyButton(startingValue, cid.getType() == ModifierlessKeybind.class);
        button.addFocusListener(new FocusAdapter(){

            @Override
            public void focusLost(FocusEvent e) {
                ConfigPanel.this.changeConfiguration(button, cd, cid);
            }
        });
        return button;
    }

    private JPanel createNotification(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JButton button = new JButton(CONFIG_ICON);
        SwingUtil.removeButtonDecorations(button);
        button.setPreferredSize(new Dimension(25, 0));
        button.addActionListener(l -> {
            MultiplexingPluginPanel muxer = this.pluginList.getMuxer();
            NotificationPanel notifPanel = (NotificationPanel)this.notificationPanelProvider.get();
            notifPanel.init(cd, cid);
            muxer.pushState(notifPanel);
        });
        panel.add((Component)button, "West");
        JCheckBox checkbox = new JCheckBox();
        Notification notif = (Notification)this.configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), (Type)((Object)Notification.class));
        checkbox.setSelected(notif.isEnabled());
        checkbox.addActionListener(ae -> {
            button.setVisible(checkbox.isSelected());
            Notification notif = (Notification)this.configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), (Type)((Object)Notification.class));
            this.configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), notif.withEnabled(checkbox.isSelected()));
        });
        checkbox.setBackground(ColorScheme.LIGHT_GRAY_COLOR);
        panel.add((Component)checkbox, "East");
        button.setVisible(checkbox.isSelected());
        return panel;
    }

    private JList<Enum<?>> createList(final ConfigDescriptor cd, final ConfigItemDescriptor cid) {
        ParameterizedType parameterizedType = (ParameterizedType)cid.getType();
        Class type = (Class)parameterizedType.getActualTypeArguments()[0];
        Set set = (Set)this.configManager.getConfiguration(cd.getGroup().value(), null, cid.getItem().keyName(), parameterizedType);
        final JList list = new JList((Enum[])type.getEnumConstants());
        list.setCellRenderer(this.listCellRenderer);
        list.setSelectionMode(2);
        list.setLayoutOrientation(0);
        list.setSelectedIndices(((Set)MoreObjects.firstNonNull((Object)set, Collections.emptySet())).stream().mapToInt(e -> ArrayUtils.indexOf((Object[])type.getEnumConstants(), (Object)e)).toArray());
        list.addFocusListener(new FocusAdapter(){

            @Override
            public void focusLost(FocusEvent e) {
                ConfigPanel.this.changeConfiguration(list, cd, cid);
            }
        });
        return list;
    }

    private void changeConfiguration(Component component, ConfigDescriptor cd, ConfigItemDescriptor cid) {
        int result;
        ConfigItem configItem = cid.getItem();
        if (!Strings.isNullOrEmpty((String)configItem.warning()) && (result = JOptionPane.showOptionDialog(component, configItem.warning(), "Are you sure?", 0, 2, null, new String[]{"Yes", "No"}, "No")) != 0) {
            this.rebuild();
            return;
        }
        if (component instanceof JCheckBox) {
            JCheckBox checkbox = (JCheckBox)component;
            this.configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), "" + checkbox.isSelected());
        } else if (component instanceof JSpinner) {
            JSpinner spinner = (JSpinner)component;
            this.configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), String.valueOf(spinner.getValue()));
        } else if (component instanceof JTextComponent) {
            JTextComponent textField = (JTextComponent)component;
            this.configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), textField.getText());
        } else if (component instanceof RuneliteColorPicker) {
            RuneliteColorPicker colorPicker = (RuneliteColorPicker)component;
            this.configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), "" + colorPicker.getSelectedColor().getRGB());
        } else if (component instanceof JComboBox) {
            JComboBox jComboBox = (JComboBox)component;
            this.configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), ((Enum)jComboBox.getSelectedItem()).name());
        } else if (component instanceof HotkeyButton) {
            HotkeyButton hotkeyButton = (HotkeyButton)component;
            this.configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), hotkeyButton.getValue());
        } else if (component instanceof JList) {
            JList list = (JList)component;
            List selectedValues = list.getSelectedValuesList();
            this.configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), Sets.newHashSet(selectedValues));
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(242, super.getPreferredSize().height);
    }

    @Subscribe
    public void onPluginChanged(PluginChanged event) {
        if (event.getPlugin() == this.pluginConfig.getPlugin()) {
            SwingUtilities.invokeLater(() -> this.pluginToggle.setSelected(event.isLoaded()));
        }
    }

    @Subscribe
    private void onExternalPluginsChanged(ExternalPluginsChanged ev) {
        if (this.pluginManager.getPlugins().stream().noneMatch(p -> p == this.pluginConfig.getPlugin())) {
            this.pluginList.getMuxer().popState();
        }
        SwingUtilities.invokeLater(this::rebuild);
    }

    @Subscribe
    private void onProfileChanged(ProfileChanged profileChanged) {
        SwingUtilities.invokeLater(this::rebuild);
    }

    private JMenuItem createResetMenuItem(PluginConfigurationDescriptor pluginConfig, ConfigItemDescriptor configItemDescriptor) {
        JMenuItem menuItem = new JMenuItem("Reset");
        menuItem.addActionListener(e -> {
            ConfigDescriptor configDescriptor = pluginConfig.getConfigDescriptor();
            ConfigGroup configGroup = configDescriptor.getGroup();
            ConfigItem configItem = configItemDescriptor.getItem();
            this.configManager.unsetConfiguration(configGroup.value(), configItem.keyName());
            this.configManager.setDefaultConfiguration(pluginConfig.getConfig(), false);
            this.rebuild();
        });
        return menuItem;
    }

    static {
        sectionExpandStates = new HashMap<ConfigSectionDescriptor, Boolean>();
        BufferedImage backIcon = ImageUtil.loadImageResource(ConfigPanel.class, "config_back_icon.png");
        BACK_ICON = new ImageIcon(backIcon);
        BufferedImage sectionRetractIcon = ImageUtil.loadImageResource(ConfigPanel.class, "/util/arrow_right.png");
        sectionRetractIcon = ImageUtil.luminanceOffset(sectionRetractIcon, -121);
        SECTION_EXPAND_ICON = new ImageIcon(sectionRetractIcon);
        BufferedImage sectionExpandIcon = ImageUtil.rotateImage(sectionRetractIcon, 1.5707963267948966);
        SECTION_RETRACT_ICON = new ImageIcon(sectionExpandIcon);
        BufferedImage configIcon = ImageUtil.loadImageResource(ConfigPanel.class, "config_edit_icon.png");
        CONFIG_ICON = new ImageIcon(configIcon);
    }
}

