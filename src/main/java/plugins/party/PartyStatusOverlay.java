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
 *  net.runelite.api.coords.LocalPoint
 */
package net.runelite.client.plugins.party;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.party.PartyConfig;
import net.runelite.client.plugins.party.PartyPluginService;
import net.runelite.client.plugins.party.data.PartyData;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

@Singleton
class PartyStatusOverlay
extends Overlay {
    private static final Color COLOR_HEALTH_MAX = Color.green;
    private static final Color COLOR_HEALTH_MIN = Color.red;
    private static final Color COLOR_PRAYER = new Color(50, 200, 200);
    private static final Color COLOR_STAMINA = new Color(160, 124, 72);
    private static final Color COLOR_SPEC = new Color(225, 225, 0);
    private static final Font OVERLAY_FONT = FontManager.getRunescapeBoldFont().deriveFont(16.0f);
    private final Client client;
    private final SpriteManager spriteManager;
    private final PartyConfig config;
    private final PartyService partyService;
    private final PartyPluginService partyPluginService;
    private boolean renderHealth = false;
    private boolean renderPrayer = false;
    private boolean renderStamina = false;
    private boolean renderSpec = false;
    private boolean renderVeng = false;
    private boolean renderSelf = false;

    @Inject
    private PartyStatusOverlay(Client client, SpriteManager spriteManager, PartyConfig config, PartyService partyService, PartyPluginService partyPluginService) {
        this.client = client;
        this.spriteManager = spriteManager;
        this.partyService = partyService;
        this.partyPluginService = partyPluginService;
        this.config = config;
        this.updateConfig();
        this.setLayer(OverlayLayer.UNDER_WIDGETS);
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(0.75f);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.partyService.isInParty()) {
            return null;
        }
        for (Player player : this.client.getPlayers()) {
            BufferedImage vengIcon;
            PartyData partyData;
            PartyMember partyMember;
            if (!this.renderSelf && player == this.client.getLocalPlayer() || (partyMember = this.findPartyMember(player)) == null || (partyData = this.partyPluginService.getPartyData(partyMember.getMemberId())) == null) continue;
            int renderIx = 0;
            graphics.setFont(OVERLAY_FONT);
            if (this.renderHealth) {
                double healthRatio = Math.min(1.0, (double)partyData.getHitpoints() / (double)partyData.getMaxHitpoints());
                Color healthColor = ColorUtil.colorLerp(COLOR_HEALTH_MIN, COLOR_HEALTH_MAX, healthRatio);
                this.renderPlayerOverlay(graphics, player, String.valueOf(partyData.getHitpoints()), healthColor, renderIx++);
            }
            if (this.renderPrayer) {
                this.renderPlayerOverlay(graphics, player, String.valueOf(partyData.getPrayer()), COLOR_PRAYER, renderIx++);
            }
            if (this.renderStamina) {
                this.renderPlayerOverlay(graphics, player, String.valueOf(partyData.getRunEnergy()), COLOR_STAMINA, renderIx++);
            }
            if (this.renderSpec) {
                this.renderPlayerOverlay(graphics, player, String.valueOf(partyData.getSpecEnergy()), COLOR_SPEC, renderIx);
            }
            if (!this.renderVeng || !partyData.isVengeanceActive() || (vengIcon = this.spriteManager.getSprite(561, 0)) == null) continue;
            this.renderPlayerOverlay(graphics, player, vengIcon);
        }
        return null;
    }

    private PartyMember findPartyMember(Player p) {
        if (p == null || p.getName() == null) {
            return null;
        }
        return this.partyService.getMemberByDisplayName(p.getName());
    }

    private void renderPlayerOverlay(Graphics2D graphics, Player player, String text, Color color, int renderIx) {
        Point point = Perspective.localToCanvas((Client)this.client, (LocalPoint)player.getLocalLocation(), (int)this.client.getPlane(), (int)player.getLogicalHeight());
        if (point != null) {
            FontMetrics fm = graphics.getFontMetrics();
            int size = fm.getHeight();
            int zOffset = size * renderIx;
            OverlayUtil.renderTextLocation(graphics, new Point(point.getX() + size + 5, point.getY() + zOffset), text, color);
        }
    }

    private void renderPlayerOverlay(Graphics2D graphics, Player player, BufferedImage image) {
        Point textLocation = player.getCanvasImageLocation(image, player.getLogicalHeight() / 2);
        if (textLocation != null) {
            OverlayUtil.renderImageLocation(graphics, textLocation, image);
        }
    }

    void updateConfig() {
        this.renderHealth = this.config.statusOverlayHealth();
        this.renderPrayer = this.config.statusOverlayPrayer();
        this.renderStamina = this.config.statusOverlayStamina();
        this.renderSpec = this.config.statusOverlaySpec();
        this.renderVeng = this.config.statusOverlayVeng();
        this.renderSelf = this.config.statusOverlayRenderSelf();
    }
}

