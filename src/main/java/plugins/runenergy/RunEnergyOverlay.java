/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Point
 *  net.runelite.api.widgets.Widget
 *  org.apache.commons.lang3.StringUtils
 */
package net.runelite.client.plugins.runenergy;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.runenergy.RunEnergyConfig;
import net.runelite.client.plugins.runenergy.RunEnergyPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import org.apache.commons.lang3.StringUtils;

class RunEnergyOverlay
extends Overlay {
    private final RunEnergyPlugin plugin;
    private final Client client;
    private final RunEnergyConfig config;
    private final TooltipManager tooltipManager;

    @Inject
    private RunEnergyOverlay(RunEnergyPlugin plugin, Client client, RunEnergyConfig config, TooltipManager tooltipManager) {
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        this.tooltipManager = tooltipManager;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget runOrb = this.client.getWidget(10485787);
        if (runOrb == null || runOrb.isHidden()) {
            return null;
        }
        Rectangle bounds = runOrb.getBounds();
        if (bounds.getX() <= 0.0) {
            return null;
        }
        Point mousePosition = this.client.getMouseCanvasPosition();
        if (bounds.contains(mousePosition.getX(), mousePosition.getY())) {
            int secondsUntil100;
            StringBuilder sb = new StringBuilder();
            sb.append("Weight: ").append(this.client.getWeight()).append(" kg</br>");
            if (this.config.replaceOrbText()) {
                sb.append("Run Energy: ").append(this.client.getEnergy() / 100).append('%');
            } else {
                sb.append("Run Time Remaining: ").append(this.plugin.getEstimatedRunTimeRemaining(false));
            }
            if (this.client.getVarbitValue(25) == 0 && this.plugin.isRingOfEnduranceEquipped() && this.plugin.getRingOfEnduranceCharges() == null) {
                sb.append("</br>Check your Ring of endurance to get the time remaining.");
            }
            if ((secondsUntil100 = this.plugin.getEstimatedRecoverTimeRemaining()) > 0) {
                int minutes = (int)Math.floor((double)secondsUntil100 / 60.0);
                int seconds = (int)Math.floor((double)secondsUntil100 - (double)minutes * 60.0);
                sb.append("</br>").append("100% Energy In: ").append(minutes).append(':').append(StringUtils.leftPad((String)Integer.toString(seconds), (int)2, (String)"0"));
            }
            this.tooltipManager.add(new Tooltip(sb.toString()));
        }
        return null;
    }
}

