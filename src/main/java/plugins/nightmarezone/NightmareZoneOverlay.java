/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.nightmarezone;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.nightmarezone.AbsorptionCounter;
import net.runelite.client.plugins.nightmarezone.NightmareZoneConfig;
import net.runelite.client.plugins.nightmarezone.NightmareZonePlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.QuantityFormatter;

class NightmareZoneOverlay
extends OverlayPanel {
    private final Client client;
    private final NightmareZoneConfig config;
    private final NightmareZonePlugin plugin;
    private final InfoBoxManager infoBoxManager;
    private final ItemManager itemManager;
    private AbsorptionCounter absorptionCounter;

    @Inject
    NightmareZoneOverlay(Client client, NightmareZoneConfig config, NightmareZonePlugin plugin, InfoBoxManager infoBoxManager, ItemManager itemManager) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setPriority(0.0f);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.infoBoxManager = infoBoxManager;
        this.itemManager = itemManager;
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "NMZ overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.plugin.isInNightmareZone() || !this.config.moveOverlay()) {
            if (this.absorptionCounter != null) {
                this.removeAbsorptionCounter();
                Widget nmzWidget = this.client.getWidget(0xCA0000);
                if (nmzWidget != null) {
                    nmzWidget.setHidden(false);
                }
            }
            return null;
        }
        this.renderAbsorptionCounter();
        int currentPoints = this.client.getVarbitValue(3949);
        int totalPoints = currentPoints + this.client.getVarpValue(1060);
        this.panelComponent.getChildren().add(LineComponent.builder().left("Points: ").right(QuantityFormatter.formatNumber(currentPoints)).build());
        this.panelComponent.getChildren().add(LineComponent.builder().left("Points/Hr: ").right(QuantityFormatter.formatNumber(this.plugin.getPointsPerHour())).build());
        this.panelComponent.getChildren().add(LineComponent.builder().left("Total: ").right(QuantityFormatter.formatNumber(totalPoints)).build());
        return super.render(graphics);
    }

    private void renderAbsorptionCounter() {
        int absorptionPoints = this.client.getVarbitValue(3956);
        if (absorptionPoints == 0) {
            if (this.absorptionCounter != null) {
                this.removeAbsorptionCounter();
                this.absorptionCounter = null;
            }
        } else if (this.config.moveOverlay()) {
            if (this.absorptionCounter == null) {
                this.addAbsorptionCounter(absorptionPoints);
            } else {
                this.absorptionCounter.setCount(absorptionPoints);
            }
        }
    }

    private void addAbsorptionCounter(int startValue) {
        this.absorptionCounter = new AbsorptionCounter(this.itemManager.getImage(11734), this.plugin, startValue, this.config.absorptionThreshold());
        this.absorptionCounter.setAboveThresholdColor(this.config.absorptionColorAboveThreshold());
        this.absorptionCounter.setBelowThresholdColor(this.config.absorptionColorBelowThreshold());
        this.infoBoxManager.addInfoBox(this.absorptionCounter);
    }

    public void removeAbsorptionCounter() {
        this.infoBoxManager.removeInfoBox(this.absorptionCounter);
        this.absorptionCounter = null;
    }

    public void updateConfig() {
        if (this.absorptionCounter != null) {
            this.absorptionCounter.setAboveThresholdColor(this.config.absorptionColorAboveThreshold());
            this.absorptionCounter.setBelowThresholdColor(this.config.absorptionColorBelowThreshold());
            this.absorptionCounter.setThreshold(this.config.absorptionThreshold());
        }
    }
}

