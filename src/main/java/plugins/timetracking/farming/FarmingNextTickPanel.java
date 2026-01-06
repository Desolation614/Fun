/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 */
package net.runelite.client.plugins.timetracking.farming;

import com.google.inject.Inject;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.timetracking.TabContentPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.TimeablePanel;
import net.runelite.client.plugins.timetracking.farming.FarmingTracker;
import net.runelite.client.ui.ColorScheme;

public class FarmingNextTickPanel
extends TabContentPanel {
    private final FarmingTracker farmingTracker;
    private final TimeTrackingConfig config;
    private final ConfigManager configManager;
    private final List<TimeablePanel<Void>> patchPanels;
    private final JTextArea infoTextArea;

    @Inject
    public FarmingNextTickPanel(FarmingTracker farmingTracker, TimeTrackingConfig config, ConfigManager configManager) {
        int[] times;
        this.farmingTracker = farmingTracker;
        this.config = config;
        this.configManager = configManager;
        this.patchPanels = new ArrayList<TimeablePanel<Void>>();
        this.setLayout(new GridBagLayout());
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        for (int time : times = new int[]{5, 10, 20, 40, 80, 160, 320, 640}) {
            TimeablePanel<Object> panel = new TimeablePanel<Object>(null, time + " minute tick", time);
            this.patchPanels.add(panel);
            this.add(panel, c);
            ++c.gridy;
        }
        this.infoTextArea = new JTextArea();
        this.add((Component)this.infoTextArea, c);
        ++c.gridy;
    }

    @Override
    public int getUpdateInterval() {
        return 50;
    }

    @Override
    public void update() {
        long unixNow = Instant.now().getEpochSecond();
        for (TimeablePanel<Void> panel : this.patchPanels) {
            int tickLength = panel.getProgress().getMaximumValue();
            long nextTick = this.farmingTracker.getTickTime(tickLength, 1);
            panel.getEstimate().setText(FarmingNextTickPanel.getFormattedEstimate(nextTick - unixNow, this.config.timeFormatMode()));
        }
        String offsetPrecisionMins = this.configManager.getRSProfileConfiguration("timetracking", "farmTickOffsetPrecision");
        String offsetTimeMins = this.configManager.getRSProfileConfiguration("timetracking", "farmTickOffset");
        this.infoTextArea.setText("Offset precision:" + offsetPrecisionMins + "\nFarming tick offset: -" + offsetTimeMins);
    }
}

