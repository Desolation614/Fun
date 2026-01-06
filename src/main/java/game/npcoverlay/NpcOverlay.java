/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.NPC
 *  net.runelite.api.NPCComposition
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.game.npcoverlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.Map;
import java.util.function.Predicate;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.util.Text;

class NpcOverlay
extends Overlay {
    private final Client client;
    private final ModelOutlineRenderer modelOutlineRenderer;
    private final Map<NPC, HighlightedNpc> highlightedNpcs;

    NpcOverlay(Client client, ModelOutlineRenderer modelOutlineRenderer, Map<NPC, HighlightedNpc> highlightedNpcs) {
        this.client = client;
        this.modelOutlineRenderer = modelOutlineRenderer;
        this.highlightedNpcs = highlightedNpcs;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for (HighlightedNpc highlightedNpc : this.highlightedNpcs.values()) {
            this.renderNpcOverlay(graphics, highlightedNpc);
        }
        return null;
    }

    private void renderNpcOverlay(Graphics2D graphics, HighlightedNpc highlightedNpc) {
        String npcName;
        Point textLocation;
        LocalPoint lp;
        NPC actor = highlightedNpc.getNpc();
        NPCComposition npcComposition = actor.getTransformedComposition();
        if (npcComposition == null || !npcComposition.isInteractible()) {
            return;
        }
        Predicate<NPC> render = highlightedNpc.getRender();
        if (render != null && !render.test(actor)) {
            return;
        }
        Color borderColor = highlightedNpc.getHighlightColor();
        float borderWidth = highlightedNpc.getBorderWidth();
        Color fillColor = highlightedNpc.getFillColor();
        if (highlightedNpc.isHull()) {
            Shape objectClickbox = actor.getConvexHull();
            this.renderPoly(graphics, borderColor, borderWidth, fillColor, objectClickbox);
        }
        if (highlightedNpc.isTile()) {
            Polygon tilePoly = actor.getCanvasTilePoly();
            this.renderPoly(graphics, borderColor, borderWidth, fillColor, tilePoly);
        }
        if (highlightedNpc.isTrueTile() && (lp = LocalPoint.fromWorld((Client)this.client, (WorldPoint)actor.getWorldLocation())) != null) {
            int size = npcComposition.getSize();
            LocalPoint centerLp = lp.plus(128 * (size - 1) / 2, 128 * (size - 1) / 2);
            Polygon tilePoly = Perspective.getCanvasTileAreaPoly((Client)this.client, (LocalPoint)centerLp, (int)size);
            this.renderPoly(graphics, borderColor, borderWidth, fillColor, tilePoly);
        }
        if (highlightedNpc.isSwTile()) {
            int size = npcComposition.getSize();
            LocalPoint lp2 = actor.getLocalLocation().plus(-((size - 1) * 128 / 2), -((size - 1) * 128 / 2));
            Polygon southWestTilePoly = Perspective.getCanvasTilePoly((Client)this.client, (LocalPoint)lp2);
            this.renderPoly(graphics, borderColor, borderWidth, fillColor, southWestTilePoly);
        }
        if (highlightedNpc.isSwTrueTile() && (lp = LocalPoint.fromWorld((Client)this.client, (WorldPoint)actor.getWorldLocation())) != null) {
            Polygon tilePoly = Perspective.getCanvasTilePoly((Client)this.client, (LocalPoint)lp);
            this.renderPoly(graphics, borderColor, borderWidth, fillColor, tilePoly);
        }
        if (highlightedNpc.isOutline()) {
            this.modelOutlineRenderer.drawOutline(actor, (int)highlightedNpc.getBorderWidth(), borderColor, highlightedNpc.getOutlineFeather());
        }
        if (highlightedNpc.isName() && actor.getName() != null && (textLocation = actor.getCanvasTextLocation(graphics, npcName = Text.removeTags(actor.getName()), actor.getLogicalHeight() + 40)) != null) {
            OverlayUtil.renderTextLocation(graphics, textLocation, npcName, borderColor);
        }
    }

    private void renderPoly(Graphics2D graphics, Color borderColor, float borderWidth, Color fillColor, Shape polygon) {
        if (polygon != null) {
            graphics.setColor(borderColor);
            graphics.setStroke(new BasicStroke(borderWidth));
            graphics.draw(polygon);
            graphics.setColor(fillColor);
            graphics.fill(polygon);
        }
    }
}

