/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.runelite.client.plugins.timetracking;

import com.google.common.collect.ImmutableMap;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.OverviewItemPanel;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.TabContentPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.TimeTrackingPanel;
import net.runelite.client.plugins.timetracking.clocks.ClockManager;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import net.runelite.client.plugins.timetracking.farming.FarmingTracker;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseTracker;
import net.runelite.client.ui.ColorScheme;

class OverviewTabPanel
extends TabContentPanel {
    private final TimeTrackingConfig config;
    private final FarmingTracker farmingTracker;
    private final BirdHouseTracker birdHouseTracker;
    private final ClockManager clockManager;
    private final FarmingContractManager farmingContractManager;
    private final OverviewItemPanel timerOverview;
    private final OverviewItemPanel stopwatchOverview;
    private final Map<Tab, OverviewItemPanel> farmingOverviews;
    private final OverviewItemPanel birdHouseOverview;
    private final OverviewItemPanel farmingContractOverview;

    OverviewTabPanel(ItemManager itemManager, TimeTrackingConfig config, TimeTrackingPanel pluginPanel, FarmingTracker farmingTracker, BirdHouseTracker birdHouseTracker, ClockManager clockManager, FarmingContractManager farmingContractManager) {
        this.config = config;
        this.farmingTracker = farmingTracker;
        this.birdHouseTracker = birdHouseTracker;
        this.clockManager = clockManager;
        this.farmingContractManager = farmingContractManager;
        this.setLayout(new GridLayout(0, 1, 0, 8));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.timerOverview = new OverviewItemPanel(itemManager, pluginPanel, Tab.CLOCK, "Timers");
        this.add(this.timerOverview);
        this.stopwatchOverview = new OverviewItemPanel(itemManager, pluginPanel, Tab.CLOCK, "Stopwatches");
        this.add(this.stopwatchOverview);
        this.birdHouseOverview = new OverviewItemPanel(itemManager, pluginPanel, Tab.BIRD_HOUSE, "Bird Houses");
        this.add(this.birdHouseOverview);
        this.farmingOverviews = (Map)Stream.of(Tab.FARMING_TABS).filter(v -> v != Tab.OVERVIEW).collect(ImmutableMap.toImmutableMap(Function.identity(), t -> {
            OverviewItemPanel p = new OverviewItemPanel(itemManager, pluginPanel, (Tab)((Object)t), t.getName());
            this.add(p);
            return p;
        }));
        this.farmingContractOverview = new OverviewItemPanel(itemManager, () -> pluginPanel.switchTab(farmingContractManager.getContractTab()), farmingContractManager::hasContract, 22993, "Farming Contract");
        this.add(this.farmingContractOverview);
    }

    @Override
    public int getUpdateInterval() {
        return 50;
    }

    @Override
    public void update() {
        long timers = this.clockManager.getActiveTimerCount();
        long stopwatches = this.clockManager.getActiveStopwatchCount();
        if (timers == 0L) {
            this.timerOverview.updateStatus("No active timers", Color.GRAY);
        } else {
            this.timerOverview.updateStatus(timers + " active timer" + (timers == 1L ? "" : "s"), ColorScheme.PROGRESS_COMPLETE_COLOR);
        }
        if (stopwatches == 0L) {
            this.stopwatchOverview.updateStatus("No active stopwatches", Color.GRAY);
        } else {
            this.stopwatchOverview.updateStatus(stopwatches + " active stopwatch" + (stopwatches == 1L ? "" : "es"), ColorScheme.PROGRESS_COMPLETE_COLOR);
        }
        this.farmingOverviews.forEach((patchType, panel) -> this.updateItemPanel((OverviewItemPanel)panel, this.farmingTracker.getSummary((Tab)((Object)patchType)), this.farmingTracker.getCompletionTime((Tab)((Object)patchType))));
        this.updateItemPanel(this.birdHouseOverview, this.birdHouseTracker.getSummary(), this.birdHouseTracker.getCompletionTime());
        this.updateContractPanel();
    }

    private void updateItemPanel(OverviewItemPanel panel, SummaryState summary, long completionTime) {
        switch (summary) {
            case COMPLETED: 
            case IN_PROGRESS: {
                long duration = completionTime - Instant.now().getEpochSecond();
                if (duration <= 0L) {
                    panel.updateStatus("Ready", ColorScheme.PROGRESS_COMPLETE_COLOR);
                    break;
                }
                panel.updateStatus("Ready " + OverviewTabPanel.getFormattedEstimate(duration, this.config.timeFormatMode()), Color.GRAY);
                break;
            }
            case EMPTY: {
                panel.updateStatus("Empty", Color.GRAY);
                break;
            }
            default: {
                panel.updateStatus("Unknown", Color.GRAY);
            }
        }
    }

    private void updateContractPanel() {
        switch (this.farmingContractManager.getSummary()) {
            case COMPLETED: 
            case IN_PROGRESS: {
                switch (this.farmingContractManager.getContractCropState()) {
                    case HARVESTABLE: 
                    case GROWING: {
                        long duration = this.farmingContractManager.getCompletionTime() - Instant.now().getEpochSecond();
                        if (duration <= 0L) {
                            this.farmingContractOverview.updateStatus("Ready", ColorScheme.PROGRESS_COMPLETE_COLOR);
                            return;
                        }
                        this.farmingContractOverview.updateStatus("Ready " + OverviewTabPanel.getFormattedEstimate(duration, this.config.timeFormatMode()), Color.GRAY);
                        return;
                    }
                    case DISEASED: {
                        this.farmingContractOverview.updateStatus("Diseased", CropState.DISEASED.getColor());
                        return;
                    }
                    case DEAD: {
                        this.farmingContractOverview.updateStatus("Dead", CropState.DEAD.getColor());
                        return;
                    }
                }
            }
            default: {
                this.farmingContractOverview.updateStatus("Unknown", Color.GRAY);
                return;
            }
            case EMPTY: {
                this.farmingContractOverview.updateStatus(this.farmingContractManager.getContractName(), Color.GRAY);
                return;
            }
            case OCCUPIED: 
        }
        this.farmingContractOverview.updateStatus(this.farmingContractManager.getContractName(), Color.RED);
    }
}

