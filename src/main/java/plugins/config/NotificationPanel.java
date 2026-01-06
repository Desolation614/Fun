/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  javax.inject.Inject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.config;

import com.google.common.primitives.Ints;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigDescriptor;
import net.runelite.client.config.ConfigItemDescriptor;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.FlashNotification;
import net.runelite.client.config.Notification;
import net.runelite.client.config.NotificationSound;
import net.runelite.client.config.RequestFocusType;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.plugins.config.ConfigPanel;
import net.runelite.client.plugins.config.FixedWidthPanel;
import net.runelite.client.plugins.config.PluginListPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.UnitFormatterFactory;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.TitleCaseListCellRenderer;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NotificationPanel
extends PluginPanel {
    private static final Logger log = LoggerFactory.getLogger(NotificationPanel.class);
    private final ConfigManager configManager;
    private final ColorPickerManager colorPickerManager;
    private final JLabel title;
    private final FixedWidthPanel mainPanel;
    private final JCheckBox enabled;
    private ConfigDescriptor configDescriptor;
    private ConfigItemDescriptor configItemDescriptor;

    @Inject
    private NotificationPanel(ConfigManager configManager, ColorPickerManager colorPickerManager, RuneLiteConfig runeLiteConfig, PluginListPanel pluginList) {
        super(false);
        this.configManager = configManager;
        this.colorPickerManager = colorPickerManager;
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
        FixedWidthPanel contentsPanel = new FixedWidthPanel();
        contentsPanel.setLayout(new BorderLayout());
        contentsPanel.add((Component)this.mainPanel, "North");
        JScrollPane scrollPane = new JScrollPane(contentsPanel);
        scrollPane.setHorizontalScrollBarPolicy(31);
        this.add((Component)scrollPane, "Center");
        JButton topPanelBackButton = new JButton(ConfigPanel.BACK_ICON);
        SwingUtil.removeButtonDecorations(topPanelBackButton);
        topPanelBackButton.setPreferredSize(new Dimension(22, 0));
        topPanelBackButton.setBorder(new EmptyBorder(0, 0, 0, 5));
        topPanelBackButton.addActionListener(e -> pluginList.getMuxer().popState());
        topPanelBackButton.setToolTipText("Back");
        topPanel.add((Component)topPanelBackButton, "West");
        this.title = new JLabel();
        this.title.setForeground(Color.WHITE);
        topPanel.add((Component)this.title, "Center");
        this.enabled = new JCheckBox();
        this.enabled.addActionListener(l -> {
            Notification notif = this.loadNotification();
            if (this.enabled.isSelected() && !notif.isInitialized()) {
                log.debug("Initializing notification {}.{}", (Object)this.configDescriptor.getGroup().value(), (Object)this.configItemDescriptor.getItem().name());
                notif = new Notification(true, true, true, runeLiteConfig.enableTrayNotifications(), TrayIcon.MessageType.NONE, runeLiteConfig.notificationRequestFocus(), runeLiteConfig.notificationSound(), null, runeLiteConfig.notificationVolume(), runeLiteConfig.notificationTimeout(), runeLiteConfig.enableGameMessageNotification(), runeLiteConfig.flashNotification(), runeLiteConfig.notificationFlashColor(), runeLiteConfig.sendNotificationsWhenFocused());
            } else {
                notif = notif.withOverride(this.enabled.isSelected());
            }
            this.saveNotification(notif);
            this.rebuild(notif);
        });
    }

    private void item(String name, String description, Component component) {
        JPanel item = new JPanel();
        item.setLayout(new BorderLayout());
        item.setMinimumSize(new Dimension(225, 0));
        JLabel configEntryName = new JLabel(name);
        configEntryName.setForeground(Color.WHITE);
        if (!"".equals(description)) {
            configEntryName.setToolTipText("<html>" + name + ":<br>" + description + "</html>");
        }
        item.add((Component)configEntryName, "Center");
        item.add(component, "East");
        this.mainPanel.add(item);
    }

    private JCheckBox checkbox(boolean selected) {
        JCheckBox checkbox = new JCheckBox();
        checkbox.setSelected(selected);
        return checkbox;
    }

    private <T> JComboBox<T> combobox(T[] options, T value) {
        JComboBox box = new JComboBox(options);
        box.setRenderer(new TitleCaseListCellRenderer());
        box.setPreferredSize(new Dimension(box.getPreferredSize().width, 22));
        box.setSelectedItem(value);
        box.setToolTipText(value instanceof Enum ? Text.titleCase((Enum)value) : value.toString());
        box.addItemListener(e -> {
            if (e.getStateChange() == 1) {
                box.setToolTipText(box.getSelectedItem() instanceof Enum ? Text.titleCase((Enum)box.getSelectedItem()) : box.getSelectedItem().toString());
            }
        });
        return box;
    }

    private JSpinner createIntSpinner(int min, int max, int value, String unit) {
        value = Ints.constrainToRange((int)value, (int)min, (int)max);
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, 1);
        JSpinner spinner = new JSpinner(model);
        JComponent editor = spinner.getEditor();
        JFormattedTextField spinnerTextField = ((JSpinner.DefaultEditor)editor).getTextField();
        spinnerTextField.setColumns(6);
        spinnerTextField.setFormatterFactory(new UnitFormatterFactory(spinnerTextField.getFormatterFactory(), unit));
        return spinner;
    }

    private ColorJButton createColorPicker(final String name, Color existing, final Consumer<Color> onClose) {
        ColorJButton colorPickerBtn;
        if (existing == null) {
            colorPickerBtn = new ColorJButton("Pick a color", Color.BLACK);
        } else {
            String colorHex = "#" + ColorUtil.colorToAlphaHexCode(existing).toUpperCase();
            colorPickerBtn = new ColorJButton(colorHex, existing);
        }
        colorPickerBtn.setFocusable(false);
        colorPickerBtn.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                RuneliteColorPicker colorPicker = NotificationPanel.this.colorPickerManager.create(NotificationPanel.this, colorPickerBtn.getColor(), name, false);
                colorPicker.setLocationRelativeTo(colorPickerBtn);
                colorPicker.setOnColorChange(c -> {
                    colorPickerBtn.setColor((Color)c);
                    colorPickerBtn.setText("#" + ColorUtil.colorToAlphaHexCode(c).toUpperCase());
                });
                colorPicker.setOnClose(onClose);
                colorPicker.setVisible(true);
            }
        });
        return colorPickerBtn;
    }

    void init(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        this.title.setText(cid.name());
        this.configDescriptor = cd;
        this.configItemDescriptor = cid;
        Notification notif = this.loadNotification();
        this.enabled.setSelected(notif.isOverride());
        this.rebuild(notif);
    }

    private void rebuild(Notification notif) {
        this.mainPanel.removeAll();
        this.mainPanel.add(this.enabled);
        this.item("Customize notification", "", this.enabled);
        if (notif.isOverride()) {
            JCheckBox checkboxTray = this.checkbox(notif.isTray());
            checkboxTray.addActionListener(ae -> {
                Notification n = this.loadNotification();
                this.saveNotification(n.withTray(checkboxTray.isSelected()));
            });
            this.item("Tray notification", "Enables tray notifications.", checkboxTray);
            JComboBox<RequestFocusType> comboboxRequestFocus = this.combobox((RequestFocusType[])RequestFocusType.class.getEnumConstants(), notif.getRequestFocus());
            comboboxRequestFocus.addItemListener(e -> {
                if (e.getStateChange() == 1) {
                    Notification n = this.loadNotification();
                    this.saveNotification(n.withRequestFocus((RequestFocusType)((Object)((Object)comboboxRequestFocus.getSelectedItem()))));
                }
            });
            this.item("Request focus", "Configures the window focus request type on notification.", comboboxRequestFocus);
            ArrayList options = Arrays.stream(NotificationSound.values()).map(ns -> new NotificationOption((NotificationSound)((Object)ns), null, ns.toString())).collect(Collectors.toCollection(ArrayList::new));
            options.addAll(NotificationPanel.loadCustomNotifications());
            NotificationOption existing = options.stream().filter(no -> no.type == notif.getSound() && Objects.equals(no.soundName, notif.getSoundName())).findAny().orElse(null);
            if (existing == null) {
                String optionName = notif.getSoundName().substring(0, notif.getSoundName().length() - ".wav".length());
                existing = new NotificationOption(notif.getSound(), notif.getSoundName(), "<html><font color=red>" + optionName + "</font></html>");
                options.add(existing);
            }
            JComboBox<NotificationOption> comboboxSound = this.combobox(options.toArray(new NotificationOption[0]), existing);
            comboboxSound.addItemListener(e -> {
                if (e.getStateChange() == 1) {
                    NotificationOption selected = (NotificationOption)comboboxSound.getSelectedItem();
                    NotificationSound sound = selected.type;
                    String soundName = selected.soundName;
                    log.debug("Notification changed to {} ({})", (Object)sound, (Object)(soundName != null ? soundName : "no file"));
                    Notification n = this.loadNotification().withSound(sound).withSoundName(soundName);
                    this.saveNotification(n);
                }
            });
            this.item("Notification sound", "Enables the playing of a sound when notifications are displayed.", comboboxSound);
            JSpinner spinnerVolume = this.createIntSpinner(0, 100, notif.getVolume(), "%");
            spinnerVolume.addChangeListener(ce -> {
                Notification n = this.loadNotification();
                this.saveNotification(n.withVolume((Integer)spinnerVolume.getValue()));
            });
            this.item("Notification volume", "Configures the volume of custom notifications (does not control native volume).", spinnerVolume);
            JSpinner spinnerTimeout = this.createIntSpinner(0, Integer.MAX_VALUE, notif.getTimeout(), "ms");
            spinnerVolume.addChangeListener(ce -> {
                Notification n = this.loadNotification();
                this.saveNotification(n.withTimeout((Integer)spinnerTimeout.getValue()));
            });
            this.item("Notification timeout", "How long notification will be shown in milliseconds. A value of 0 will make it use the system configuration. (Linux only)", spinnerTimeout);
            JCheckBox checkboxGameMessage = this.checkbox(notif.isGameMessage());
            checkboxGameMessage.addActionListener(ae -> {
                Notification n = this.loadNotification();
                this.saveNotification(n.withGameMessage(checkboxGameMessage.isSelected()));
            });
            this.item("Game message notification", "Adds a notification message to the chatbox.", checkboxGameMessage);
            JComboBox<FlashNotification> comboboxFlash = this.combobox((FlashNotification[])FlashNotification.class.getEnumConstants(), notif.getFlash());
            comboboxFlash.addItemListener(e -> {
                if (e.getStateChange() == 1) {
                    Notification n = this.loadNotification();
                    this.saveNotification(n.withFlash((FlashNotification)((Object)((Object)comboboxFlash.getSelectedItem()))));
                }
            });
            this.item("Flash", "Flashes the game frame as a notification.", comboboxFlash);
            ColorJButton colorpickerFlashColor = this.createColorPicker("Flash color", notif.getFlashColor(), c -> {
                Notification n = this.loadNotification();
                this.saveNotification(n.withFlashColor((Color)c));
            });
            this.item("Flash color", "The color of the notification flashes.", colorpickerFlashColor);
            JCheckBox checkboxSendWhenFocused = this.checkbox(notif.isSendWhenFocused());
            checkboxSendWhenFocused.addActionListener(ae -> {
                Notification n = this.loadNotification();
                this.saveNotification(n.withSendWhenFocused(checkboxSendWhenFocused.isSelected()));
            });
            this.item("Send notifications when focused", "Send the notification even when the client is focused.", checkboxSendWhenFocused);
            JButton resetButton = new JButton("Reset");
            resetButton.addActionListener(e -> {
                int result = JOptionPane.showOptionDialog(resetButton, "Are you sure you want to reset this notification configuration?", "Are you sure?", 0, 2, null, new String[]{"Yes", "No"}, "No");
                if (result == 0) {
                    this.enabled.setSelected(false);
                    Notification n = new Notification().withEnabled(true);
                    this.saveNotification(n);
                    this.rebuild(n);
                }
            });
            this.mainPanel.add(resetButton);
        } else {
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BorderLayout());
            this.mainPanel.add(infoPanel);
            infoPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
            infoPanel.add(new JLabel("<html>Notification settings can be customized for each type of notification. Notifications without custom settings use the default settings found in the 'RuneLite' configuration under 'Notification Settings'.</html>"));
        }
    }

    private Notification loadNotification() {
        return (Notification)this.configManager.getConfiguration(this.configDescriptor.getGroup().value(), this.configItemDescriptor.getItem().keyName(), (Type)((Object)Notification.class));
    }

    private void saveNotification(Notification notification) {
        this.configManager.setConfiguration(this.configDescriptor.getGroup().value(), this.configItemDescriptor.getItem().keyName(), notification);
    }

    private static List<NotificationOption> loadCustomNotifications() {
        File[] files = RuneLite.NOTIFICATIONS_DIR.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(files).filter(f -> f.getName().endsWith(".wav")).map(f -> new NotificationOption(NotificationSound.CUSTOM, f.getName(), f.getName().substring(0, f.getName().length() - ".wav".length()))).collect(Collectors.toList());
    }

    private static class NotificationOption {
        NotificationSound type;
        String soundName;
        String optionName;

        public String toString() {
            return this.optionName;
        }

        public NotificationOption(NotificationSound type, String soundName, String optionName) {
            this.type = type;
            this.soundName = soundName;
            this.optionName = optionName;
        }
    }
}

