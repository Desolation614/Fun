/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.hunter;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JToggleButton;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.TabContentPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.TimeablePanel;
import net.runelite.client.plugins.timetracking.hunter.BirdHouse;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseData;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseSpace;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseState;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseTracker;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;

public class BirdHouseTabPanel
extends TabContentPanel {
    private static final Color COMPLETED_COLOR = ColorScheme.PROGRESS_COMPLETE_COLOR.darker();
    private final ConfigManager configManager;
    private final ItemManager itemManager;
    private final BirdHouseTracker birdHouseTracker;
    private final TimeTrackingConfig config;
    private final List<TimeablePanel<BirdHouseSpace>> spacePanels;

    BirdHouseTabPanel(ConfigManager configManager, ItemManager itemManager, BirdHouseTracker birdHouseTracker, TimeTrackingConfig config) {
        this.configManager = configManager;
        this.itemManager = itemManager;
        this.birdHouseTracker = birdHouseTracker;
        this.config = config;
        this.spacePanels = new ArrayList<TimeablePanel<BirdHouseSpace>>();
        this.setLayout(new DynamicGridLayout(0, 1, 0, 0));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        boolean first = true;
        for (BirdHouseSpace space : BirdHouseSpace.values()) {
            TimeablePanel<BirdHouseSpace> panel = new TimeablePanel<BirdHouseSpace>(space, space.getName(), BirdHouseTracker.BIRD_HOUSE_DURATION);
            this.spacePanels.add(panel);
            this.add(panel);
            if (first) {
                first = false;
                panel.setBorder(null);
            }
            JToggleButton toggleNotify = panel.getNotifyButton();
            toggleNotify.addActionListener(e -> {
                if (configManager.getRSProfileKey() != null) {
                    configManager.setRSProfileConfiguration("timetracking", "birdHouseNotification", toggleNotify.isSelected());
                }
                this.spacePanels.forEach(p -> p.getNotifyButton().setSelected(toggleNotify.isSelected()));
            });
        }
    }

    @Override
    public int getUpdateInterval() {
        return 50;
    }

    @Override
    public void update() {
        long unixNow = Instant.now().getEpochSecond();
        boolean notifications = Boolean.TRUE.equals(this.configManager.getRSProfileConfiguration("timetracking", "birdHouseNotification", Boolean.TYPE));
        block5: for (TimeablePanel<BirdHouseSpace> panel : this.spacePanels) {
            BirdHouseSpace space = panel.getTimeable();
            BirdHouseData data = (BirdHouseData)this.birdHouseTracker.getBirdHouseData().get((Object)space);
            int value = -1;
            long startTime = 0L;
            if (data != null) {
                value = data.getVarp();
                startTime = data.getTimestamp();
            }
            BirdHouse birdHouse = BirdHouse.fromVarpValue(value);
            BirdHouseState state = BirdHouseState.fromVarpValue(value);
            if (panel.getProgress().getMaximumValue() != this.birdHouseTracker.getHouseDuration()) {
                panel.getProgress().setMaximumValue(this.birdHouseTracker.getHouseDuration());
            }
            if (birdHouse == null) {
                this.itemManager.getImage(314).addTo(panel.getIcon());
                panel.getProgress().setVisible(false);
            } else {
                this.itemManager.getImage(birdHouse.getItemID()).addTo(panel.getIcon());
                panel.getIcon().setToolTipText(birdHouse.getName());
                panel.getProgress().setVisible(true);
            }
            panel.getNotifyButton().setSelected(notifications);
            panel.getProgress().setForeground(state.getColor().darker());
            switch (state) {
                case EMPTY: {
                    panel.getIcon().setToolTipText("Empty");
                    panel.getEstimate().setText("Empty");
                    continue block5;
                }
                case BUILT: {
                    panel.getProgress().setValue(0);
                    panel.getEstimate().setText("Built");
                    continue block5;
                }
                case SEEDED: {
                    long remainingTime = startTime + (long)this.birdHouseTracker.getHouseDuration() - unixNow;
                    if (remainingTime <= 0L) {
                        panel.getProgress().setValue(this.birdHouseTracker.getHouseDuration());
                        panel.getProgress().setForeground(COMPLETED_COLOR);
                        panel.getEstimate().setText("Done");
                        continue block5;
                    }
                    panel.getProgress().setValue((int)((long)this.birdHouseTracker.getHouseDuration() - remainingTime));
                    panel.getEstimate().setText("Done " + BirdHouseTabPanel.getFormattedEstimate(remainingTime, this.config.timeFormatMode()));
                    continue block5;
                }
            }
            panel.getIcon().setToolTipText("Unknown state");
            panel.getEstimate().setText("Unknown");
        }
    }
}

