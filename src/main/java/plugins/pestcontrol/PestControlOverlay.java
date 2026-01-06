/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.NPC
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.pestcontrol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.pestcontrol.Game;
import net.runelite.client.plugins.pestcontrol.PestControlPlugin;
import net.runelite.client.plugins.pestcontrol.Portal;
import net.runelite.client.plugins.pestcontrol.PortalContext;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PestControlOverlay
extends Overlay {
    private static final Logger log = LoggerFactory.getLogger(PestControlOverlay.class);
    private final PestControlPlugin plugin;
    private final Client client;
    private Game game;

    @Inject
    public PestControlOverlay(PestControlPlugin plugin, Client client) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.client.getWidget(26738702) == null) {
            if (this.game != null) {
                log.debug("Pest control game has ended");
                this.game = null;
            }
            return null;
        }
        if (this.game == null) {
            log.debug("Pest control game has started");
            this.game = new Game();
        }
        this.renderSpinners(graphics);
        this.renderPortalWidgets(graphics);
        return null;
    }

    private void renderSpinners(Graphics2D graphics) {
        for (NPC npc : this.plugin.getSpinners()) {
            OverlayUtil.renderActorOverlay(graphics, (Actor)npc, npc.getName(), Color.CYAN);
        }
    }

    private void renderPortalWidgets(Graphics2D graphics) {
        PortalContext purple = this.game.getPurple();
        PortalContext blue = this.game.getBlue();
        PortalContext yellow = this.game.getYellow();
        PortalContext red = this.game.getRed();
        Widget purpleHealth = this.client.getWidget(Portal.PURPLE.getHitpoints());
        Widget blueHealth = this.client.getWidget(Portal.BLUE.getHitpoints());
        Widget yellowHealth = this.client.getWidget(Portal.YELLOW.getHitpoints());
        Widget redHealth = this.client.getWidget(Portal.RED.getHitpoints());
        if (PestControlOverlay.isZero(purpleHealth)) {
            this.game.die(purple);
        }
        if (PestControlOverlay.isZero(blueHealth)) {
            this.game.die(blue);
        }
        if (PestControlOverlay.isZero(yellowHealth)) {
            this.game.die(yellow);
        }
        if (PestControlOverlay.isZero(redHealth)) {
            this.game.die(red);
        }
        this.renderAttack(graphics, purple);
        this.renderAttack(graphics, blue);
        this.renderAttack(graphics, yellow);
        this.renderAttack(graphics, red);
        for (Portal portal : this.game.getNextPortals()) {
            this.renderWidgetOverlay(graphics, portal, "NEXT", Color.ORANGE);
        }
        this.renderProgressWidget(graphics);
    }

    private void renderProgressWidget(Graphics2D graphics) {
        Widget bar = this.client.getWidget(26738698).getChild(0);
        Rectangle2D bounds = bar.getBounds().getBounds2D();
        Widget prgs = this.client.getWidget(26738700).getChild(0);
        int perc = (int)(prgs.getBounds().getWidth() / bounds.getWidth() * 100.0);
        Color color = Color.GREEN;
        if (perc < 25) {
            color = Color.RED;
        }
        String text = String.valueOf(perc) + "%";
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D textBounds = fm.getStringBounds(text, graphics);
        int x = (int)(bounds.getX() - textBounds.getWidth());
        int y = (int)(bounds.getY() + (double)fm.getHeight() - 2.0);
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 1, y + 1);
        graphics.setColor(color);
        graphics.drawString(text, x, y);
    }

    private void renderWidgetOverlay(Graphics2D graphics, Portal portal, String text, Color color) {
        Widget shield = this.client.getWidget(portal.getShield());
        Widget icon = this.client.getWidget(portal.getIcon());
        Widget hp = this.client.getWidget(portal.getHitpoints());
        Widget bar = this.client.getWidget(26738698).getChild(0);
        Rectangle2D barBounds = bar.getBounds().getBounds2D();
        Rectangle2D bounds = PestControlOverlay.union(shield.getBounds().getBounds2D(), icon.getBounds().getBounds2D());
        bounds = PestControlOverlay.union(bounds, hp.getBounds().getBounds2D());
        graphics.setColor(color);
        graphics.draw(new Rectangle2D.Double(bounds.getX(), bounds.getY() - 2.0, bounds.getWidth(), bounds.getHeight() - 3.0));
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D textBounds = fm.getStringBounds(text, graphics);
        int x = (int)(bounds.getX() + bounds.getWidth() / 2.0 - textBounds.getWidth() / 2.0);
        int y = (int)(bounds.getY() + bounds.getHeight() + textBounds.getHeight() + barBounds.getHeight());
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 1, y + 5);
        graphics.setColor(color);
        graphics.drawString(text, x, y + 4);
    }

    private static Rectangle2D union(Rectangle2D src1, Rectangle2D src2) {
        double x1 = Math.min(src1.getMinX(), src2.getMinX());
        double y1 = Math.min(src1.getMinY(), src2.getMinY());
        double x2 = Math.max(src1.getMaxX(), src2.getMaxX());
        double y2 = Math.max(src1.getMaxY(), src2.getMaxY());
        Rectangle2D.Double result = new Rectangle2D.Double();
        result.setFrameFromDiagonal(x1, y1, x2, y2);
        return result;
    }

    private void renderAttack(Graphics2D graphics, PortalContext portal) {
        if (portal.isShielded() || portal.isDead()) {
            return;
        }
        this.renderWidgetOverlay(graphics, portal.getPortal(), "ATK", Color.RED);
    }

    private static boolean isZero(Widget widget) {
        return widget.getText().trim().equals("0");
    }

    Game getGame() {
        return this.game;
    }
}

