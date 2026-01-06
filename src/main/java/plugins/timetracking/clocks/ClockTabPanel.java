/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.clocks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.timetracking.TabContentPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.plugins.timetracking.clocks.ClockManager;
import net.runelite.client.plugins.timetracking.clocks.ClockPanel;
import net.runelite.client.plugins.timetracking.clocks.Stopwatch;
import net.runelite.client.plugins.timetracking.clocks.StopwatchPanel;
import net.runelite.client.plugins.timetracking.clocks.Timer;
import net.runelite.client.plugins.timetracking.clocks.TimerPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

public class ClockTabPanel
extends TabContentPanel {
    static final ImageIcon DELETE_ICON;
    static final ImageIcon DELETE_ICON_HOVER;
    static final ImageIcon LAP_ICON;
    static final ImageIcon LAP_ICON_HOVER;
    static final ImageIcon PAUSE_ICON;
    static final ImageIcon PAUSE_ICON_HOVER;
    static final ImageIcon RESET_ICON;
    static final ImageIcon RESET_ICON_HOVER;
    static final ImageIcon START_ICON;
    static final ImageIcon START_ICON_HOVER;
    static final ImageIcon LOOP_ICON;
    static final ImageIcon LOOP_ICON_HOVER;
    static final ImageIcon LOOP_SELECTED_ICON;
    static final ImageIcon LOOP_SELECTED_ICON_HOVER;
    private static final ImageIcon ADD_ICON;
    private static final ImageIcon ADD_ICON_HOVER;
    private final ClockManager clockManager;
    private final List<ClockPanel> clockPanels = new ArrayList<ClockPanel>();

    ClockTabPanel(ClockManager clockManager) {
        this.clockManager = clockManager;
        this.setLayout(new DynamicGridLayout(0, 1, 0, 4));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.rebuild();
    }

    void rebuild() {
        ClockPanel panel;
        this.removeAll();
        this.clockPanels.clear();
        this.add(this.createHeaderPanel("Timers", "timer", false, e -> this.clockManager.addTimer()));
        for (Timer timer : this.clockManager.getTimers()) {
            panel = new TimerPanel(this.clockManager, timer);
            this.clockPanels.add(panel);
            this.add(panel);
        }
        if (this.clockManager.getTimers().isEmpty()) {
            this.add(this.createInfoPanel("Click the + button to add a timer."));
        }
        this.add(this.createHeaderPanel("Stopwatches", "stopwatch", true, e -> this.clockManager.addStopwatch()));
        for (Stopwatch stopwatch : this.clockManager.getStopwatches()) {
            panel = new StopwatchPanel(this.clockManager, stopwatch);
            this.clockPanels.add(panel);
            this.add(panel);
        }
        if (this.clockManager.getStopwatches().isEmpty()) {
            this.add(this.createInfoPanel("Click the + button to add a stopwatch."));
        }
        this.revalidate();
    }

    private JPanel createHeaderPanel(String title, String type, boolean largePadding, ActionListener actionListener) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(largePadding ? 11 : 0, 0, 0, 0));
        panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        JLabel headerLabel = new JLabel(title);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(FontManager.getRunescapeSmallFont());
        panel.add((Component)headerLabel, "Center");
        JButton addButton = new JButton(ADD_ICON);
        addButton.setRolloverIcon(ADD_ICON_HOVER);
        SwingUtil.removeButtonDecorations(addButton);
        addButton.setPreferredSize(new Dimension(14, 14));
        addButton.setToolTipText("Add a " + type);
        addButton.addActionListener(actionListener);
        panel.add((Component)addButton, "East");
        return panel;
    }

    private JPanel createInfoPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(7, 8, 6, 8));
        panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        JShadowedLabel infoLabel = new JShadowedLabel(text);
        infoLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
        infoLabel.setFont(FontManager.getRunescapeSmallFont());
        panel.add(infoLabel);
        return panel;
    }

    @Override
    public int getUpdateInterval() {
        return 1;
    }

    @Override
    public void update() {
        for (ClockPanel panel : this.clockPanels) {
            if (!panel.getClock().isActive()) continue;
            panel.updateDisplayInput();
        }
    }

    static {
        BufferedImage deleteIcon = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "delete_icon.png");
        BufferedImage lapIcon = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "lap_icon.png");
        BufferedImage pauseIcon = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "pause_icon.png");
        BufferedImage resetIcon = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "reset_icon.png");
        BufferedImage startIcon = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "start_icon.png");
        BufferedImage addIcon = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "add_icon.png");
        BufferedImage loopIcon = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "loop_icon.png");
        BufferedImage loopSelectedIcon = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "loop_selected_icon.png");
        DELETE_ICON = new ImageIcon(deleteIcon);
        DELETE_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(deleteIcon, -80));
        LAP_ICON = new ImageIcon(lapIcon);
        LAP_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(lapIcon, -80));
        PAUSE_ICON = new ImageIcon(pauseIcon);
        PAUSE_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(pauseIcon, -80));
        RESET_ICON = new ImageIcon(resetIcon);
        RESET_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(resetIcon, -80));
        START_ICON = new ImageIcon(startIcon);
        START_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(startIcon, -80));
        ADD_ICON = new ImageIcon(addIcon);
        ADD_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset((Image)addIcon, 0.53f));
        LOOP_ICON = new ImageIcon(loopIcon);
        LOOP_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(loopIcon, -80));
        LOOP_SELECTED_ICON = new ImageIcon(loopSelectedIcon);
        LOOP_SELECTED_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(loopSelectedIcon, -80));
    }
}

