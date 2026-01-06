/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.barbarianassault;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.barbarianassault.BarbarianAssaultConfig;
import net.runelite.client.plugins.barbarianassault.BarbarianAssaultPlugin;
import net.runelite.client.plugins.barbarianassault.Role;
import net.runelite.client.plugins.barbarianassault.Round;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class TimerOverlay
extends Overlay {
    private final Client client;
    private final BarbarianAssaultPlugin plugin;
    private final BarbarianAssaultConfig config;

    @Inject
    private TimerOverlay(Client client, BarbarianAssaultPlugin plugin, BarbarianAssaultConfig config) {
        super(plugin);
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Round round = this.plugin.getCurrentRound();
        if (round == null) {
            return null;
        }
        Role role = round.getRoundRole();
        Widget roleText = this.client.getWidget(role.getRoleText());
        Widget roleSprite = this.client.getWidget(role.getRoleSprite());
        if (this.config.showTimer() && roleText != null && roleSprite != null) {
            roleText.setText(String.format("00:%02d", round.getTimeToChange()));
            Rectangle spriteBounds = roleSprite.getBounds();
            roleSprite.setHidden(true);
            graphics.drawImage(this.plugin.getClockImage(), spriteBounds.x, spriteBounds.y, null);
        }
        return null;
    }
}

