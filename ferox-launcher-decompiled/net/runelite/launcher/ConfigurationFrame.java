/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.runelite.launcher;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.runelite.launcher.HardwareAccelerationMode;
import net.runelite.launcher.LaunchMode;
import net.runelite.launcher.LauncherProperties;
import net.runelite.launcher.LauncherSettings;
import net.runelite.launcher.PackrConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationFrame
extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(ConfigurationFrame.class);
    private static final Color DARKER_GRAY_COLOR = new Color(30, 30, 30);
    private final JCheckBox chkboxDebug;
    private final JCheckBox chkboxNoDiffs;
    private final JCheckBox chkboxSkipTlsVerification;
    private final JCheckBox chkboxNoUpdates;
    private final JCheckBox chkboxSafemode;
    private final JCheckBox chkboxIpv4;
    private final JTextField txtScale;
    private final JTextArea txtClientArguments;
    private final JTextArea txtJvmArguments;
    private final JComboBox<HardwareAccelerationMode> comboHardwareAccelMode;
    private final JComboBox<LaunchMode> comboLaunchMode;

    private ConfigurationFrame(LauncherSettings settings) {
        BufferedImage iconImage;
        this.setTitle("Ferox Launcher Configuration");
        try (InputStream in = ConfigurationFrame.class.getResourceAsStream(LauncherProperties.getRuneLite128());){
            iconImage = ImageIO.read(in);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        this.setDefaultCloseOperation(3);
        this.setIconImage(iconImage);
        Container pane = this.getContentPane();
        pane.setLayout(new BoxLayout(pane, 1));
        pane.setBackground(DARKER_GRAY_COLOR);
        JPanel topPanel = new JPanel();
        topPanel.setBackground(DARKER_GRAY_COLOR);
        topPanel.setLayout(new GridLayout(3, 2, 0, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        this.chkboxDebug = ConfigurationFrame.checkbox("Debug", "Runs the launcher and client in debug mode. Debug mode writes debug level logging to the log files.", Boolean.TRUE.equals(settings.debug));
        topPanel.add(this.chkboxDebug);
        this.chkboxNoDiffs = ConfigurationFrame.checkbox("Disable diffs", "Downloads full artifacts for updates instead of diffs.", Boolean.TRUE.equals(settings.nodiffs));
        topPanel.add(this.chkboxNoDiffs);
        this.chkboxSkipTlsVerification = ConfigurationFrame.checkbox("Disable TLS verification", "Disables TLS verification.", Boolean.TRUE.equals(settings.skipTlsVerification));
        topPanel.add(this.chkboxSkipTlsVerification);
        this.chkboxNoUpdates = ConfigurationFrame.checkbox("Disable updates", "Disables the launcher self updating", Boolean.TRUE.equals(settings.noupdates));
        topPanel.add(this.chkboxNoUpdates);
        this.chkboxSafemode = ConfigurationFrame.checkbox("Safe mode", "Launches the client in safe mode", Boolean.TRUE.equals(settings.safemode));
        topPanel.add(this.chkboxSafemode);
        this.chkboxIpv4 = ConfigurationFrame.checkbox("IPv4", "Prefer IPv4 over IPv6", Boolean.TRUE.equals(settings.ipv4));
        topPanel.add(this.chkboxIpv4);
        pane.add(topPanel);
        JPanel midPanel = new JPanel();
        midPanel.setBackground(DARKER_GRAY_COLOR);
        midPanel.setLayout(new GridLayout(2, 2, 0, 0));
        midPanel.add(ConfigurationFrame.label("Client arguments", "Arguments passed to the client. One per line."));
        this.txtClientArguments = ConfigurationFrame.area(Joiner.on('\n').join(settings.clientArguments));
        JScrollPane sp = new JScrollPane(this.txtClientArguments, 22, 30);
        midPanel.add(sp);
        midPanel.add(ConfigurationFrame.label("JVM arguments", "Arguments passed to the JVM. One per line."));
        this.txtJvmArguments = ConfigurationFrame.area(Joiner.on('\n').join(settings.jvmArguments));
        sp = new JScrollPane(this.txtJvmArguments, 22, 30);
        midPanel.add(sp);
        pane.add(midPanel);
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(DARKER_GRAY_COLOR);
        bottomPanel.setLayout(new GridLayout(3, 2, 0, 0));
        bottomPanel.add(ConfigurationFrame.label("Scale", "Scaling factor for Java 2D"));
        this.txtScale = ConfigurationFrame.field(settings.scale != null ? Double.toString(settings.scale) : null);
        bottomPanel.add(this.txtScale);
        bottomPanel.add(ConfigurationFrame.label("Hardware acceleration", "Hardware acceleration mode for Java 2D."));
        this.comboHardwareAccelMode = ConfigurationFrame.combobox(HardwareAccelerationMode.values(), settings.hardwareAccelerationMode);
        bottomPanel.add(this.comboHardwareAccelMode);
        bottomPanel.add(ConfigurationFrame.label("Launch mode", null));
        this.comboLaunchMode = ConfigurationFrame.combobox(LaunchMode.values(), settings.launchMode);
        bottomPanel.add(this.comboLaunchMode);
        pane.add(bottomPanel);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DARKER_GRAY_COLOR);
        JButton save = new JButton("Save");
        save.addActionListener(this::save);
        buttonPanel.add(save);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(l -> this.dispose());
        buttonPanel.add(cancel);
        pane.add(buttonPanel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setMinimumSize(this.getSize());
    }

    private void save(ActionEvent l) {
        LauncherSettings settings = LauncherSettings.loadSettings();
        settings.debug = this.chkboxDebug.isSelected();
        settings.nodiffs = this.chkboxNoDiffs.isSelected();
        settings.skipTlsVerification = this.chkboxSkipTlsVerification.isSelected();
        settings.noupdates = this.chkboxNoUpdates.isSelected();
        settings.safemode = this.chkboxSafemode.isSelected();
        settings.ipv4 = this.chkboxIpv4.isSelected();
        String t = this.txtScale.getText();
        settings.scale = null;
        if (!t.isEmpty()) {
            try {
                settings.scale = Double.parseDouble(t);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        settings.clientArguments = Splitter.on('\n').omitEmptyStrings().trimResults().splitToList(this.txtClientArguments.getText());
        settings.jvmArguments = Splitter.on('\n').omitEmptyStrings().trimResults().splitToList(this.txtJvmArguments.getText());
        settings.hardwareAccelerationMode = (HardwareAccelerationMode)((Object)this.comboHardwareAccelMode.getSelectedItem());
        settings.launchMode = (LaunchMode)((Object)this.comboLaunchMode.getSelectedItem());
        LauncherSettings.saveSettings(settings);
        PackrConfig.patch(config -> {
            List vmArgs = (List)config.computeIfAbsent("vmArgs", k -> new ArrayList());
            if (settings.ipv4) {
                vmArgs.add("-Djava.net.preferIPv4Stack=true");
            } else {
                vmArgs.remove("-Djava.net.preferIPv4Stack=true");
            }
        });
        log.info("Updated launcher configuration:" + System.lineSeparator() + "{}", (Object)settings.configurationStr());
        this.dispose();
    }

    private static JLabel label(String name, String tooltip) {
        JLabel label = new JLabel(name);
        label.setToolTipText(tooltip);
        label.setForeground(Color.WHITE);
        return label;
    }

    private static JTextField field(@Nullable String value) {
        return new JTextField(value);
    }

    private static JTextArea area(@Nullable String value) {
        return new JTextArea(value, 2, 20);
    }

    private static JCheckBox checkbox(String name, String tooltip, boolean checked) {
        JCheckBox checkbox = new JCheckBox(name);
        checkbox.setSelected(checked);
        checkbox.setToolTipText(tooltip);
        checkbox.setForeground(Color.WHITE);
        checkbox.setBackground(DARKER_GRAY_COLOR);
        return checkbox;
    }

    private static <E> JComboBox<E> combobox(E[] values, E default_) {
        JComboBox<E> combobox = new JComboBox<E>(values);
        combobox.setSelectedItem(default_);
        return combobox;
    }

    static void open() {
        new ConfigurationFrame(LauncherSettings.loadSettings()).setVisible(true);
    }

    public static void main(String[] args) {
        ConfigurationFrame.open();
    }
}

