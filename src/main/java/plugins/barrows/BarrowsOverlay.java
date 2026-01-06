/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.barrows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.barrows.BarrowsBrothers;
import net.runelite.client.plugins.barrows.BarrowsConfig;
import net.runelite.client.plugins.barrows.BarrowsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class BarrowsOverlay
extends Overlay {
    private final Client client;
    private final BarrowsPlugin plugin;
    private final BarrowsConfig config;

    @Inject
    private BarrowsOverlay(Client client, BarrowsPlugin plugin, BarrowsConfig config) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget puzzleAnswer;
        if (this.plugin.isBarrowsLoaded() && this.config.showBrotherLoc()) {
            this.renderBarrowsBrothers(graphics);
        }
        if ((puzzleAnswer = this.plugin.getPuzzleAnswer()) != null && this.config.showPuzzleAnswer() && !puzzleAnswer.isHidden()) {
            Rectangle answerRect = puzzleAnswer.getBounds();
            graphics.setColor(Color.GREEN);
            graphics.draw(answerRect);
        }
        return null;
    }

    private void renderBarrowsBrothers(Graphics2D graphics) {
        for (BarrowsBrothers brother : BarrowsBrothers.values()) {
            String brotherLetter;
            Point miniMapLocation;
            LocalPoint localLocation = LocalPoint.fromWorld((Client)this.client, (WorldPoint)brother.getLocation());
            if (localLocation == null || (miniMapLocation = Perspective.getCanvasTextMiniMapLocation((Client)this.client, (Graphics2D)graphics, (LocalPoint)localLocation, (String)(brotherLetter = Character.toString(brother.getName().charAt(0))))) == null) continue;
            graphics.setColor(Color.black);
            graphics.drawString(brotherLetter, miniMapLocation.getX() + 1, miniMapLocation.getY() + 1);
            if (this.client.getVarbitValue(brother.getKilledVarbit()) > 0) {
                graphics.setColor(this.config.deadBrotherLocColor());
            } else {
                graphics.setColor(this.config.brotherLocColor());
            }
            graphics.drawString(brotherLetter, miniMapLocation.getX(), miniMapLocation.getY());
        }
    }
}

