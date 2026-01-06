/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timetracking.farming;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Instant;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.plugins.timetracking.TabContentPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import net.runelite.client.plugins.timetracking.farming.Produce;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ColorUtil;

class FarmingContractInfoBox
extends InfoBox {
    private final Produce contract;
    private final FarmingContractManager manager;
    private final TimeTrackingConfig config;

    FarmingContractInfoBox(BufferedImage image, Plugin plugin, Produce contract, TimeTrackingConfig config, FarmingContractManager manager) {
        super(image, plugin);
        this.contract = contract;
        this.config = config;
        this.manager = manager;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public Color getTextColor() {
        return null;
    }

    @Override
    public String getTooltip() {
        Color contractColor;
        Object contractDescription;
        SummaryState summary = this.manager.getSummary();
        block0 : switch (summary) {
            case COMPLETED: {
                contractDescription = "Ready";
                contractColor = ColorScheme.PROGRESS_COMPLETE_COLOR;
                break;
            }
            case OCCUPIED: {
                contractDescription = "Occupied";
                contractColor = ColorScheme.PROGRESS_ERROR_COLOR;
                break;
            }
            case IN_PROGRESS: {
                CropState cropState = this.manager.getContractCropState();
                switch (cropState) {
                    case DISEASED: {
                        contractDescription = "Diseased";
                        contractColor = cropState.getColor();
                        break block0;
                    }
                    case DEAD: {
                        contractDescription = "Dead";
                        contractColor = cropState.getColor();
                        break block0;
                    }
                }
                contractDescription = "Ready " + TabContentPanel.getFormattedEstimate(this.manager.getCompletionTime() - Instant.now().getEpochSecond(), this.config.timeFormatMode());
                contractColor = Color.GRAY;
                break;
            }
            default: {
                contractDescription = null;
                contractColor = Color.GRAY;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ColorUtil.wrapWithColorTag("Farming Contract", Color.WHITE));
        sb.append("</br>");
        sb.append(ColorUtil.wrapWithColorTag(this.contract.getName(), contractColor));
        if (contractDescription != null) {
            sb.append("</br>");
            sb.append(ColorUtil.wrapWithColorTag((String)contractDescription, contractColor));
        }
        return sb.toString();
    }

    @Override
    public boolean render() {
        return this.config.farmingContractInfoBox();
    }

    public Produce getContract() {
        return this.contract;
    }
}

