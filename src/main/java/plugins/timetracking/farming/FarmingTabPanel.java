/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 */
package net.runelite.client.plugins.timetracking.farming;

import com.google.common.base.Strings;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.TabContentPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.TimeablePanel;
import net.runelite.client.plugins.timetracking.farming.CompostState;
import net.runelite.client.plugins.timetracking.farming.CompostTracker;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import net.runelite.client.plugins.timetracking.farming.FarmingPatch;
import net.runelite.client.plugins.timetracking.farming.FarmingTracker;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;
import net.runelite.client.plugins.timetracking.farming.PatchPrediction;
import net.runelite.client.plugins.timetracking.farming.PaymentTracker;
import net.runelite.client.plugins.timetracking.farming.Produce;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.AsyncBufferedImage;

public class FarmingTabPanel
extends TabContentPanel {
    private final FarmingTracker farmingTracker;
    private final CompostTracker compostTracker;
    private final PaymentTracker paymentTracker;
    private final ItemManager itemManager;
    private final ConfigManager configManager;
    private final TimeTrackingConfig config;
    private final List<TimeablePanel<FarmingPatch>> patchPanels;
    private final FarmingContractManager farmingContractManager;

    FarmingTabPanel(FarmingTracker farmingTracker, CompostTracker compostTracker, PaymentTracker paymentTracker, ItemManager itemManager, ConfigManager configManager, TimeTrackingConfig config, Set<FarmingPatch> patches, FarmingContractManager farmingContractManager) {
        this.farmingTracker = farmingTracker;
        this.compostTracker = compostTracker;
        this.paymentTracker = paymentTracker;
        this.itemManager = itemManager;
        this.configManager = configManager;
        this.config = config;
        this.patchPanels = new ArrayList<TimeablePanel<FarmingPatch>>();
        this.farmingContractManager = farmingContractManager;
        this.setLayout(new GridBagLayout());
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        PatchImplementation lastImpl = null;
        boolean first = true;
        for (FarmingPatch patch : patches) {
            String title = patch.getRegion().getName() + (String)(Strings.isNullOrEmpty((String)patch.getName()) ? "" : " (" + patch.getName() + ")");
            TimeablePanel<FarmingPatch> p = new TimeablePanel<FarmingPatch>(patch, title, 1);
            if (patch.getImplementation() != lastImpl && !Strings.isNullOrEmpty((String)patch.getImplementation().getName())) {
                JLabel groupLabel = new JLabel(patch.getImplementation().getName());
                if (first) {
                    first = false;
                    groupLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
                } else {
                    groupLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
                }
                groupLabel.setFont(FontManager.getRunescapeSmallFont());
                this.add((Component)groupLabel, c);
                ++c.gridy;
                lastImpl = patch.getImplementation();
            }
            JToggleButton toggleNotify = p.getNotifyButton();
            String configKey = patch.notifyConfigKey();
            toggleNotify.addActionListener(e -> {
                if (configManager.getRSProfileKey() != null) {
                    configManager.setRSProfileConfiguration("timetracking", configKey, toggleNotify.isSelected());
                }
            });
            this.patchPanels.add(p);
            this.add(p, c);
            ++c.gridy;
            if (!first) continue;
            first = false;
            p.setBorder(null);
        }
    }

    @Override
    public int getUpdateInterval() {
        return 50;
    }

    @Override
    public void update() {
        long unixNow = Instant.now().getEpochSecond();
        for (TimeablePanel<FarmingPatch> panel : this.patchPanels) {
            FarmingPatch patch = panel.getTimeable();
            PatchPrediction prediction = this.farmingTracker.predictPatch(patch);
            boolean protected_ = this.paymentTracker.getProtectedState(patch);
            CompostState compostState = this.compostTracker.getCompostState(patch);
            AsyncBufferedImage img = this.getPatchImage(compostState, protected_);
            String tooltip = this.getPatchTooltip(compostState, protected_);
            if (img != null) {
                img.onLoaded(() -> panel.setOverlayIconImage(img));
            } else {
                panel.setOverlayIconImage(null);
            }
            if (prediction == null) {
                this.itemManager.getImage(Produce.WEEDS.getItemID()).addTo(panel.getIcon());
                panel.getIcon().setToolTipText("Unknown state" + tooltip);
                panel.getProgress().setMaximumValue(0);
                panel.getProgress().setValue(0);
                panel.getProgress().setVisible(false);
                panel.getEstimate().setText("Unknown");
                panel.getProgress().setBackground(null);
            } else {
                if (prediction.getProduce().getItemID() < 0) {
                    panel.getIcon().setIcon(null);
                    panel.getIcon().setToolTipText("Unknown state" + tooltip);
                } else {
                    this.itemManager.getImage(prediction.getProduce().getItemID()).addTo(panel.getIcon());
                    panel.getIcon().setToolTipText(prediction.getProduce().getName() + tooltip);
                }
                switch (prediction.getCropState()) {
                    case HARVESTABLE: {
                        panel.getEstimate().setText("Done");
                        break;
                    }
                    case GROWING: {
                        if (prediction.getDoneEstimate() < unixNow) {
                            panel.getEstimate().setText("Done");
                            break;
                        }
                        panel.getEstimate().setText("Done " + FarmingTabPanel.getFormattedEstimate(prediction.getDoneEstimate() - unixNow, this.config.timeFormatMode()));
                        break;
                    }
                    case DISEASED: {
                        panel.getEstimate().setText("Diseased");
                        break;
                    }
                    case DEAD: {
                        panel.getEstimate().setText("Dead");
                        break;
                    }
                    case EMPTY: {
                        panel.getEstimate().setText("Empty");
                        break;
                    }
                    case FILLING: {
                        panel.getEstimate().setText("Filling");
                    }
                }
                if (prediction.getProduce() != Produce.WEEDS || prediction.getStage() < prediction.getStages() - 1) {
                    panel.getProgress().setVisible(true);
                    panel.getProgress().setForeground(prediction.getCropState().getColor().darker());
                    panel.getProgress().setMaximumValue(prediction.getStages() - 1);
                    panel.getProgress().setValue(prediction.getStage());
                } else {
                    panel.getProgress().setVisible(false);
                }
            }
            JLabel farmingContractIcon = panel.getFarmingContractIcon();
            if (this.farmingContractManager.shouldHighlightFarmingTabPanel(patch)) {
                this.itemManager.getImage(22993).addTo(farmingContractIcon);
                farmingContractIcon.setToolTipText(this.farmingContractManager.getContract().getName());
            } else {
                farmingContractIcon.setIcon(null);
                farmingContractIcon.setToolTipText("");
            }
            String configKey = patch.notifyConfigKey();
            JToggleButton toggleNotify = panel.getNotifyButton();
            boolean notifyEnabled = Boolean.TRUE.equals(this.configManager.getRSProfileConfiguration("timetracking", configKey, (Type)((Object)Boolean.class)));
            toggleNotify.setSelected(notifyEnabled);
        }
    }

    private AsyncBufferedImage getPatchImage(CompostState compostState, boolean protected_) {
        return protected_ ? this.itemManager.getImage(5386) : (compostState != null ? this.itemManager.getImage(compostState.getItemId()) : null);
    }

    private String getPatchTooltip(CompostState compostState, boolean protected_) {
        StringBuilder stringBuilder = new StringBuilder();
        if (protected_) {
            stringBuilder.append(" protected");
            if (compostState != null) {
                stringBuilder.append(" and ").append(compostState.name().toLowerCase()).append("ed");
            }
        } else if (compostState != null) {
            stringBuilder.append(" with ").append(compostState.name().toLowerCase());
        }
        return stringBuilder.toString();
    }
}

