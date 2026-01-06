/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.clocks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.timetracking.clocks.Clock;
import net.runelite.client.plugins.timetracking.clocks.ClockManager;
import net.runelite.client.plugins.timetracking.clocks.ClockTabPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.util.SwingUtil;

abstract class ClockPanel
extends JPanel {
    private static final Border NAME_BOTTOM_BORDER = new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR), BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR));
    private static final Color ACTIVE_CLOCK_COLOR = ColorScheme.LIGHT_GRAY_COLOR.brighter();
    private static final Color INACTIVE_CLOCK_COLOR = ColorScheme.LIGHT_GRAY_COLOR.darker();
    private static final String INPUT_HMS_REGEX = ".*[hms].*";
    private static final String WHITESPACE_REGEX = "\\s+";
    final JPanel contentContainer;
    final JPanel leftActions;
    final JPanel rightActions;
    private final FlatTextField nameInput;
    private final JToggleButton startPauseButton;
    protected final FlatTextField displayInput;
    private final Clock clock;
    private final String clockType;
    private final boolean editable;

    ClockPanel(final ClockManager clockManager, final Clock clock, String clockType, boolean editable) {
        this.clock = clock;
        this.clockType = clockType;
        this.editable = editable;
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(3, 0, 0, 0));
        JPanel nameWrapper = new JPanel(new BorderLayout());
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.setBorder(NAME_BOTTOM_BORDER);
        this.nameInput = new FlatTextField();
        this.nameInput.setText(clock.getName());
        this.nameInput.setBorder(null);
        this.nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.nameInput.setPreferredSize(new Dimension(0, 24));
        this.nameInput.getTextField().setBorder(new EmptyBorder(0, 8, 0, 0));
        this.nameInput.addActionListener(e -> this.getParent().requestFocusInWindow());
        this.nameInput.getTextField().addFocusListener(new FocusListener(){

            @Override
            public void focusGained(FocusEvent e) {
                ClockPanel.this.nameInput.getTextField().selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                clock.setName(ClockPanel.this.nameInput.getText());
                clockManager.saveToConfig();
            }
        });
        nameWrapper.add((Component)this.nameInput, "Center");
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBorder(new EmptyBorder(5, 0, 0, 0));
        mainContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.contentContainer = new JPanel(new BorderLayout());
        this.contentContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.displayInput = new FlatTextField();
        this.displayInput.setEditable(editable);
        this.displayInput.setBorder(null);
        this.displayInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.displayInput.setPreferredSize(new Dimension(0, 24));
        this.displayInput.getTextField().setHorizontalAlignment(0);
        this.displayInput.addActionListener(e -> this.getParent().requestFocusInWindow());
        this.displayInput.getTextField().addFocusListener(new FocusListener(){

            @Override
            public void focusGained(FocusEvent e) {
                ClockPanel.this.displayInput.getTextField().setForeground(INACTIVE_CLOCK_COLOR);
                ClockPanel.this.displayInput.getTextField().selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                long duration = 0L;
                try {
                    duration = ClockPanel.stringToSeconds(ClockPanel.this.displayInput.getText());
                }
                catch (Exception exception) {
                    // empty catch block
                }
                clock.setDuration(Math.max(0L, duration));
                clock.reset();
                clockManager.checkForWarnings();
                ClockPanel.this.updateDisplayInput();
                ClockPanel.this.updateActivityStatus();
                clockManager.saveTimers();
            }
        });
        this.updateDisplayInput();
        this.contentContainer.add((Component)this.displayInput, "North");
        JPanel actionsBar = new JPanel(new BorderLayout());
        actionsBar.setBorder(new EmptyBorder(4, 0, 4, 0));
        actionsBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.leftActions = new JPanel(new FlowLayout(0, 6, 0));
        this.leftActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.startPauseButton = new JToggleButton(ClockTabPanel.START_ICON);
        this.startPauseButton.setRolloverIcon(ClockTabPanel.START_ICON_HOVER);
        this.startPauseButton.setSelectedIcon(ClockTabPanel.PAUSE_ICON);
        this.startPauseButton.setRolloverSelectedIcon(ClockTabPanel.PAUSE_ICON_HOVER);
        SwingUtil.removeButtonDecorations(this.startPauseButton);
        this.startPauseButton.setPreferredSize(new Dimension(16, 14));
        this.updateActivityStatus();
        this.startPauseButton.addActionListener(e -> {
            if (!this.startPauseButton.isSelected()) {
                clock.pause();
            } else if (!clock.start()) {
                return;
            }
            this.updateActivityStatus();
            clockManager.saveToConfig();
        });
        JButton resetButton = new JButton(ClockTabPanel.RESET_ICON);
        resetButton.setRolloverIcon(ClockTabPanel.RESET_ICON_HOVER);
        SwingUtil.removeButtonDecorations(resetButton);
        resetButton.setPreferredSize(new Dimension(16, 14));
        resetButton.setToolTipText("Reset " + clockType);
        resetButton.addActionListener(e -> {
            clock.reset();
            clockManager.checkForWarnings();
            this.reset();
            clockManager.saveToConfig();
        });
        this.leftActions.add(this.startPauseButton);
        this.leftActions.add(resetButton);
        this.rightActions = new JPanel(new FlowLayout(2, 6, 0));
        this.rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        actionsBar.add((Component)this.leftActions, "West");
        actionsBar.add((Component)this.rightActions, "East");
        mainContainer.add((Component)this.contentContainer, "Center");
        mainContainer.add((Component)actionsBar, "South");
        this.add((Component)nameWrapper, "North");
        this.add((Component)mainContainer, "Center");
    }

    void reset() {
        this.updateDisplayInput();
        this.updateActivityStatus();
    }

    void updateDisplayInput() {
        if (!this.displayInput.getTextField().hasFocus()) {
            this.displayInput.setText(ClockPanel.getFormattedDuration(this.clock.getDisplayTime()));
        }
    }

    void updateActivityStatus() {
        boolean isActive = this.clock.isActive();
        this.displayInput.setEditable(this.editable && !isActive);
        this.displayInput.getTextField().setForeground(this.getColor());
        this.startPauseButton.setToolTipText(isActive ? "Pause " + this.clockType : "Start " + this.clockType);
        this.startPauseButton.setSelected(isActive);
        if (this.editable && this.clock.getDisplayTime() == 0L && !isActive) {
            this.displayInput.getTextField().setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
        }
    }

    protected Color getColor() {
        return this.clock.isActive() ? ACTIVE_CLOCK_COLOR : INACTIVE_CLOCK_COLOR;
    }

    static String getFormattedDuration(long duration) {
        long hours = duration / 3600L;
        long mins = duration / 60L % 60L;
        long seconds = duration % 60L;
        return String.format("%02d:%02d:%02d", hours, mins, seconds);
    }

    static long stringToSeconds(String time) throws NumberFormatException, DateTimeParseException {
        long duration = 0L;
        if (time.matches(INPUT_HMS_REGEX)) {
            String textWithoutWhitespaces = time.replaceAll(WHITESPACE_REGEX, "");
            duration = Duration.parse("PT" + textWithoutWhitespaces).toMillis() / 1000L;
        } else {
            String[] parts = time.split(":");
            int i = parts.length - 1;
            for (int multiplier = 1; i >= 0 && multiplier <= 3600; --i, multiplier *= 60) {
                duration += (long)(Integer.parseInt(parts[i].trim()) * multiplier);
            }
        }
        return duration;
    }

    public Clock getClock() {
        return this.clock;
    }
}

