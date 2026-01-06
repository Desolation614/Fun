/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Point
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.worldhopper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.worldhopper.WorldHopperConfig;
import net.runelite.client.plugins.worldhopper.WorldHopperPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class WorldHopperPingOverlay
extends Overlay {
    private static final int Y_OFFSET = 11;
    private static final int X_OFFSET = 1;
    private final Client client;
    private final WorldHopperPlugin worldHopperPlugin;
    private final WorldHopperConfig worldHopperConfig;

    @Inject
    private WorldHopperPingOverlay(Client client, WorldHopperPlugin worldHopperPlugin, WorldHopperConfig worldHopperConfig) {
        this.client = client;
        this.worldHopperPlugin = worldHopperPlugin;
        this.worldHopperConfig = worldHopperConfig;
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.setPriority(0.75f);
        this.setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.worldHopperConfig.displayPing()) {
            return null;
        }
        int ping = this.worldHopperPlugin.getCurrentPing();
        if (ping < 0) {
            return null;
        }
        String text = ping + " ms";
        int textWidth = graphics.getFontMetrics().stringWidth(text);
        int textHeight = graphics.getFontMetrics().getAscent() - graphics.getFontMetrics().getDescent();
        Widget logoutButton = this.client.getWidget(10747939);
        int xOffset = 1;
        if (logoutButton != null && !logoutButton.isHidden()) {
            xOffset += logoutButton.getWidth();
        }
        int width = (int)this.client.getRealDimensions().getWidth();
        Point point = new Point(width - textWidth - xOffset, textHeight + 11);
        OverlayUtil.renderTextLocation(graphics, point, text, Color.YELLOW);
        return null;
    }
}

