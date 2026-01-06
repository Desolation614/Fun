/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.zalcano;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.plugins.zalcano.ZalcanoPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

class ZalcanoPanel
extends OverlayPanel {
    private final ZalcanoPlugin plugin;

    @Inject
    public ZalcanoPanel(ZalcanoPlugin plugin) {
        super(plugin);
        this.setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D g) {
        if (!this.plugin.isInCavern()) {
            return null;
        }
        this.panelComponent.getChildren().add(LineComponent.builder().left("Health damage:").leftColor(ZalcanoPanel.colorFromCount(this.plugin.getHealthDamage())).right(Integer.toString(this.plugin.getHealthDamage())).build());
        this.panelComponent.getChildren().add(LineComponent.builder().left("Shield damage:").leftColor(ZalcanoPanel.colorFromCount(this.plugin.getShieldDamage())).right(Integer.toString(this.plugin.getShieldDamage())).build());
        return super.render(g);
    }

    private static Color colorFromCount(int damage) {
        if (damage >= 50) {
            return Color.GREEN;
        }
        if (damage >= 30) {
            return Color.YELLOW;
        }
        return Color.RED;
    }
}

