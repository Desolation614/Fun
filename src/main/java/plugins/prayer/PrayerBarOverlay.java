/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.Perspective
 *  net.runelite.api.Player
 *  net.runelite.api.Point
 *  net.runelite.api.Skill
 *  net.runelite.api.coords.LocalPoint
 */
package net.runelite.client.plugins.prayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.prayer.PrayerConfig;
import net.runelite.client.plugins.prayer.PrayerFlickLocation;
import net.runelite.client.plugins.prayer.PrayerPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageUtil;

@Singleton
class PrayerBarOverlay
extends Overlay {
    private static final Color BAR_FILL_COLOR = new Color(0, 149, 151);
    private static final Color BAR_BG_COLOR = Color.black;
    private static final Dimension PRAYER_BAR_SIZE = new Dimension(30, 5);
    private static final int HD_PRAYER_BAR_PADDING = 1;
    private static final BufferedImage HD_FRONT_BAR = ImageUtil.loadImageResource(PrayerPlugin.class, "front.png");
    private static final BufferedImage HD_BACK_BAR = ImageUtil.loadImageResource(PrayerPlugin.class, "back.png");
    private final Client client;
    private final PrayerConfig config;
    private final PrayerPlugin plugin;
    private boolean showingPrayerBar;

    @Inject
    private PrayerBarOverlay(Client client, PrayerConfig config, PrayerPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(0.75f);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.config.showPrayerBar() || !this.showingPrayerBar) {
            return null;
        }
        int height = this.client.getLocalPlayer().getLogicalHeight() + 10;
        LocalPoint localLocation = this.client.getLocalPlayer().getLocalLocation();
        Point canvasPoint = Perspective.localToCanvas((Client)this.client, (LocalPoint)localLocation, (int)this.client.getPlane(), (int)height);
        float ratio = (float)this.client.getBoostedSkillLevel(Skill.PRAYER) / (float)this.client.getRealSkillLevel(Skill.PRAYER);
        if (this.client.getSpriteOverrides().containsKey(2176)) {
            int barWidth = HD_FRONT_BAR.getWidth();
            int barHeight = HD_FRONT_BAR.getHeight();
            int barX = canvasPoint.getX() - barWidth / 2;
            int barY = canvasPoint.getY();
            int progressFill = (int)Math.ceil(Math.max(2.0f, Math.min((float)barWidth * ratio, (float)barWidth)));
            graphics.drawImage(HD_BACK_BAR, barX, barY, barWidth, barHeight, null);
            graphics.drawImage(HD_FRONT_BAR.getSubimage(0, 0, progressFill, barHeight), barX, barY, progressFill, barHeight, null);
            if ((this.plugin.isPrayersActive() || this.config.prayerFlickAlwaysOn()) && (this.config.prayerFlickLocation().equals((Object)PrayerFlickLocation.PRAYER_BAR) || this.config.prayerFlickLocation().equals((Object)PrayerFlickLocation.BOTH))) {
                double t = this.plugin.getTickProgress();
                int halfBarWidth = barWidth / 2 - 1;
                int xOffset = (int)(-Math.cos(t) * (double)halfBarWidth) + halfBarWidth;
                graphics.setColor(this.config.prayerFlickColor());
                graphics.fillRect(barX + xOffset, barY + 1, 1, barHeight - 2);
            }
            return null;
        }
        int barX = canvasPoint.getX() - 15;
        int barY = canvasPoint.getY();
        int barWidth = PrayerBarOverlay.PRAYER_BAR_SIZE.width;
        int barHeight = PrayerBarOverlay.PRAYER_BAR_SIZE.height;
        int progressFill = (int)Math.ceil(Math.min((float)barWidth * ratio, (float)barWidth));
        graphics.setColor(BAR_BG_COLOR);
        graphics.fillRect(barX, barY, barWidth, barHeight);
        graphics.setColor(BAR_FILL_COLOR);
        graphics.fillRect(barX, barY, progressFill, barHeight);
        if ((this.plugin.isPrayersActive() || this.config.prayerFlickAlwaysOn()) && (this.config.prayerFlickLocation().equals((Object)PrayerFlickLocation.PRAYER_BAR) || this.config.prayerFlickLocation().equals((Object)PrayerFlickLocation.BOTH))) {
            double t = this.plugin.getTickProgress();
            int xOffset = (int)(-Math.cos(t) * (double)barWidth / 2.0) + barWidth / 2;
            graphics.setColor(this.config.prayerFlickColor());
            graphics.fillRect(barX + xOffset, barY, 1, barHeight);
        }
        return null;
    }

    void onTick() {
        Player localPlayer = this.client.getLocalPlayer();
        this.showingPrayerBar = true;
        if (localPlayer == null) {
            this.showingPrayerBar = false;
            return;
        }
        if (this.config.hideIfNotPraying() && !this.plugin.isPrayersActive()) {
            this.showingPrayerBar = false;
            return;
        }
        if (this.config.hideIfOutOfCombat() && localPlayer.getHealthScale() == -1) {
            this.showingPrayerBar = false;
        }
    }
}

