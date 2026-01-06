/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 */
package net.runelite.client.plugins.blastfurnace;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ItemContainer;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.blastfurnace.BlastFurnaceConfig;
import net.runelite.client.plugins.blastfurnace.BlastFurnacePlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

class BlastFurnaceClickBoxOverlay
extends Overlay {
    private static final int MAX_DISTANCE = 2350;
    private final Client client;
    private final BlastFurnacePlugin plugin;
    private final BlastFurnaceConfig config;

    @Inject
    private BlastFurnaceClickBoxOverlay(Client client, BlastFurnacePlugin plugin, BlastFurnaceConfig config) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        int dispenserState = this.client.getVarbitValue(936);
        if (this.config.showConveyorBelt() && this.plugin.getConveyorBelt() != null) {
            Color color = dispenserState == 1 ? Color.RED : Color.GREEN;
            this.renderObject(this.plugin.getConveyorBelt(), graphics, color);
        }
        if (this.config.showBarDispenser() && this.plugin.getBarDispenser() != null) {
            boolean hasIceGloves = this.hasIceGloves();
            Color color = dispenserState == 2 && hasIceGloves ? Color.GREEN : (dispenserState == 3 ? Color.GREEN : Color.RED);
            this.renderObject(this.plugin.getBarDispenser(), graphics, color);
        }
        return null;
    }

    private boolean hasIceGloves() {
        ItemContainer equipmentContainer = this.client.getItemContainer(94);
        if (equipmentContainer == null) {
            return false;
        }
        return equipmentContainer.contains(1580) || equipmentContainer.contains(27031);
    }

    private void renderObject(GameObject object, Graphics2D graphics, Color color) {
        Shape objectClickbox;
        LocalPoint localLocation = this.client.getLocalPlayer().getLocalLocation();
        Point mousePosition = this.client.getMouseCanvasPosition();
        LocalPoint location = object.getLocalLocation();
        if (localLocation.distanceTo(location) <= 2350 && (objectClickbox = object.getClickbox()) != null) {
            if (objectClickbox.contains(mousePosition.getX(), mousePosition.getY())) {
                graphics.setColor(color.darker());
            } else {
                graphics.setColor(color);
            }
            graphics.draw(objectClickbox);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            graphics.fill(objectClickbox);
        }
    }
}

