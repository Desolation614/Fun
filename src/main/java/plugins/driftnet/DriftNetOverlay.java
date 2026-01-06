/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.GameObject
 *  net.runelite.api.NPC
 *  net.runelite.api.Point
 */
package net.runelite.client.plugins.driftnet;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.plugins.driftnet.DriftNet;
import net.runelite.client.plugins.driftnet.DriftNetConfig;
import net.runelite.client.plugins.driftnet.DriftNetPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class DriftNetOverlay
extends Overlay {
    private final DriftNetConfig config;
    private final DriftNetPlugin plugin;

    @Inject
    private DriftNetOverlay(DriftNetConfig config, DriftNetPlugin plugin) {
        this.config = config;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(0.0f);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.plugin.isInDriftNetArea()) {
            return null;
        }
        if (this.config.highlightUntaggedFish()) {
            this.renderFish(graphics);
        }
        if (this.config.showNetStatus()) {
            this.renderNets(graphics);
        }
        if (this.config.tagAnnetteWhenNoNets()) {
            this.renderAnnette(graphics);
        }
        return null;
    }

    private void renderFish(Graphics2D graphics) {
        for (NPC fish : this.plugin.getFish()) {
            if (this.plugin.getTaggedFish().containsKey(fish)) continue;
            OverlayUtil.renderActorOverlay(graphics, (Actor)fish, "", this.config.untaggedFishColor());
        }
    }

    private void renderNets(Graphics2D graphics) {
        for (DriftNet net : this.plugin.getNETS()) {
            Shape polygon = net.getNet().getConvexHull();
            if (polygon != null) {
                OverlayUtil.renderPolygon(graphics, polygon, net.getStatus().getColor());
            }
            String text = net.getFormattedCountText();
            Point textLocation = net.getNet().getCanvasTextLocation(graphics, text, 0);
            if (textLocation == null) continue;
            OverlayUtil.renderTextLocation(graphics, textLocation, text, this.config.countColor());
        }
    }

    private void renderAnnette(Graphics2D graphics) {
        GameObject annette = this.plugin.getAnnette();
        if (annette != null && !this.plugin.isDriftNetsInInventory()) {
            OverlayUtil.renderPolygon(graphics, annette.getConvexHull(), this.config.annetteTagColor());
        }
    }
}

