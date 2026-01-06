/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.clocks;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import net.runelite.client.plugins.timetracking.clocks.ClockManager;
import net.runelite.client.plugins.timetracking.clocks.ClockPanel;
import net.runelite.client.plugins.timetracking.clocks.ClockTabPanel;
import net.runelite.client.plugins.timetracking.clocks.Timer;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.SwingUtil;

class TimerPanel
extends ClockPanel {
    private static final Color WARNING_COLOR = ColorScheme.BRAND_ORANGE;

    TimerPanel(ClockManager clockManager, Timer timer) {
        super(clockManager, timer, "timer", true);
        JToggleButton loopButton = new JToggleButton(ClockTabPanel.LOOP_ICON);
        loopButton.setRolloverIcon(ClockTabPanel.LOOP_ICON_HOVER);
        loopButton.setSelectedIcon(ClockTabPanel.LOOP_SELECTED_ICON);
        loopButton.setRolloverSelectedIcon(ClockTabPanel.LOOP_SELECTED_ICON_HOVER);
        SwingUtil.removeButtonDecorations(loopButton);
        loopButton.setPreferredSize(new Dimension(16, 14));
        loopButton.setToolTipText("Loop timer");
        loopButton.addActionListener(e -> timer.setLoop(!timer.isLoop()));
        loopButton.setSelected(timer.isLoop());
        this.leftActions.add(loopButton);
        JButton deleteButton = new JButton(ClockTabPanel.DELETE_ICON);
        SwingUtil.removeButtonDecorations(deleteButton);
        deleteButton.setRolloverIcon(ClockTabPanel.DELETE_ICON_HOVER);
        deleteButton.setPreferredSize(new Dimension(16, 14));
        deleteButton.setToolTipText("Delete timer");
        deleteButton.addActionListener(e -> clockManager.removeTimer(timer));
        this.rightActions.add(deleteButton);
    }

    @Override
    void updateDisplayInput() {
        super.updateDisplayInput();
        Timer timer = (Timer)this.getClock();
        if (timer.isWarning()) {
            this.displayInput.getTextField().setForeground(this.getColor());
        }
    }

    @Override
    protected Color getColor() {
        Timer timer = (Timer)this.getClock();
        Color warningColor = timer.isActive() ? WARNING_COLOR : WARNING_COLOR.darker();
        return timer.isWarning() ? warningColor : super.getColor();
    }
}

