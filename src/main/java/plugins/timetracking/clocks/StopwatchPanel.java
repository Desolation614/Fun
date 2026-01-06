/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.clocks;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.timetracking.clocks.ClockManager;
import net.runelite.client.plugins.timetracking.clocks.ClockPanel;
import net.runelite.client.plugins.timetracking.clocks.ClockTabPanel;
import net.runelite.client.plugins.timetracking.clocks.Stopwatch;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.SwingUtil;

class StopwatchPanel
extends ClockPanel {
    private static final Color LAP_DATA_COLOR = ColorScheme.LIGHT_GRAY_COLOR.darker();
    private final JPanel lapsContainer;
    private final Stopwatch stopwatch;

    StopwatchPanel(ClockManager clockManager, Stopwatch stopwatch) {
        super(clockManager, stopwatch, "stopwatch", false);
        this.stopwatch = stopwatch;
        this.lapsContainer = new JPanel(new GridBagLayout());
        this.lapsContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.rebuildLapList();
        this.contentContainer.add(this.lapsContainer);
        JButton lapButton = new JButton(ClockTabPanel.LAP_ICON);
        lapButton.setRolloverIcon(ClockTabPanel.LAP_ICON_HOVER);
        SwingUtil.removeButtonDecorations(lapButton);
        lapButton.setPreferredSize(new Dimension(16, 14));
        lapButton.setToolTipText("Add lap time");
        lapButton.addActionListener(e -> {
            stopwatch.lap();
            this.rebuildLapList();
            clockManager.saveStopwatches();
        });
        this.leftActions.add(lapButton);
        JButton deleteButton = new JButton(ClockTabPanel.DELETE_ICON);
        deleteButton.setRolloverIcon(ClockTabPanel.DELETE_ICON_HOVER);
        SwingUtil.removeButtonDecorations(deleteButton);
        deleteButton.setPreferredSize(new Dimension(16, 14));
        deleteButton.setToolTipText("Delete stopwatch");
        deleteButton.addActionListener(e -> clockManager.removeStopwatch(stopwatch));
        this.rightActions.add(deleteButton);
    }

    @Override
    void reset() {
        super.reset();
        this.rebuildLapList();
    }

    private void rebuildLapList() {
        this.lapsContainer.removeAll();
        List<Long> laps = this.stopwatch.getLaps();
        if (laps.isEmpty()) {
            this.lapsContainer.setBorder(null);
        } else {
            this.lapsContainer.setBorder(new EmptyBorder(5, 0, 0, 0));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 5, 3, 5);
            c.fill = 2;
            c.weightx = 1.0;
            c.gridx = 0;
            c.gridy = 0;
            long previousLap = 0L;
            for (long lap : this.stopwatch.getLaps()) {
                c.gridx = 0;
                this.lapsContainer.add((Component)this.createSmallLabel("" + (c.gridy + 1)), c);
                c.gridx = 1;
                this.lapsContainer.add((Component)this.createSmallLabel(StopwatchPanel.getFormattedDuration(lap - previousLap)), c);
                c.gridx = 2;
                this.lapsContainer.add((Component)this.createSmallLabel(StopwatchPanel.getFormattedDuration(lap)), c);
                previousLap = lap;
                ++c.gridy;
            }
        }
        this.lapsContainer.revalidate();
        this.lapsContainer.repaint();
    }

    private JLabel createSmallLabel(String text) {
        JLabel label = new JLabel(text, 0);
        label.setFont(FontManager.getRunescapeSmallFont());
        label.setForeground(LAP_DATA_COLOR);
        return label;
    }
}

