/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.prayer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.prayer.PrayerConfig;
import net.runelite.client.plugins.prayer.PrayerFlickLocation;
import net.runelite.client.plugins.prayer.PrayerPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class PrayerFlickOverlay
extends Overlay {
    private final Client client;
    private final PrayerConfig config;
    private final PrayerPlugin plugin;

    @Inject
    private PrayerFlickOverlay(Client client, PrayerConfig config, PrayerPlugin plugin) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        int orbInnerHeight;
        if (!this.plugin.isPrayersActive() && !this.config.prayerFlickAlwaysOn() || this.config.prayerFlickLocation().equals((Object)PrayerFlickLocation.NONE) || this.config.prayerFlickLocation().equals((Object)PrayerFlickLocation.PRAYER_BAR)) {
            return null;
        }
        Widget xpOrb = this.client.getWidget(10485779);
        if (xpOrb == null || xpOrb.isHidden()) {
            return null;
        }
        Rectangle2D bounds = xpOrb.getBounds().getBounds2D();
        if (bounds.getX() <= 0.0) {
            return null;
        }
        int orbInnerWidth = orbInnerHeight = (int)bounds.getHeight();
        int orbInnerX = (int)(bounds.getX() + 24.0);
        int orbInnerY = (int)(bounds.getY() - 1.0);
        double t = this.plugin.getTickProgress();
        int xOffset = (int)(-Math.cos(t) * (double)orbInnerWidth / 2.0) + orbInnerWidth / 2;
        int indicatorHeight = (int)(Math.sin(t) * (double)orbInnerHeight);
        int yOffset = orbInnerHeight / 2 - indicatorHeight / 2;
        graphics.setColor(this.config.prayerFlickColor());
        graphics.fillRect(orbInnerX + xOffset, orbInnerY + yOffset, 1, indicatorHeight);
        return null;
    }
}

