/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.ObjectComposition
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.pyramidplunder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.pyramidplunder.PyramidPlunderConfig;
import net.runelite.client.plugins.pyramidplunder.PyramidPlunderPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

class PyramidPlunderOverlay
extends Overlay {
    private static final int MAX_DISTANCE = 2350;
    private final Client client;
    private final PyramidPlunderPlugin plugin;
    private final PyramidPlunderConfig config;

    @Inject
    private PyramidPlunderOverlay(Client client, PyramidPlunderPlugin plugin, PyramidPlunderConfig config) {
        super(plugin);
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget ppWidget = this.client.getWidget(28049410);
        if (ppWidget == null) {
            return null;
        }
        ppWidget.setHidden(this.config.hideTimer());
        LocalPoint playerLocation = this.client.getLocalPlayer().getLocalLocation();
        int currentFloor = this.client.getVarbitValue(2377);
        for (GameObject object2 : this.plugin.getObjectsToHighlight()) {
            Shape shape;
            ObjectComposition imposter;
            if (this.config.highlightUrnsFloor() > currentFloor && PyramidPlunderPlugin.URN_IDS.contains(object2.getId()) || this.config.highlightChestFloor() > currentFloor && 26616 == object2.getId() || this.config.highlightSarcophagusFloor() > currentFloor && 26626 == object2.getId() || object2.getLocalLocation().distanceTo(playerLocation) >= 2350 || !PyramidPlunderPlugin.URN_CLOSED_IDS.contains((imposter = this.client.getObjectDefinition(object2.getId()).getImpostor()).getId()) && 20946 != imposter.getId() && 21255 != imposter.getId() || (shape = object2.getConvexHull()) == null) continue;
            OverlayUtil.renderPolygon(graphics, shape, this.config.highlightContainersColor());
        }
        Point mousePosition = this.client.getMouseCanvasPosition();
        this.plugin.getTilesToHighlight().forEach((object, tile) -> {
            Color highlightColor;
            if (!this.config.highlightDoors() && PyramidPlunderPlugin.TOMB_DOOR_WALL_IDS.contains(object.getId()) || !this.config.highlightSpeartraps() && 21280 == object.getId() || tile.getPlane() != this.client.getPlane() || object.getLocalLocation().distanceTo(playerLocation) >= 2350) {
                return;
            }
            if (21280 == object.getId()) {
                if (this.client.getVarbitValue(2365) != 1) {
                    return;
                }
                highlightColor = this.config.highlightSpeartrapsColor();
            } else {
                ObjectComposition imposter = this.client.getObjectDefinition(object.getId()).getImpostor();
                if (imposter.getId() != 20948) {
                    return;
                }
                highlightColor = this.config.highlightDoorsColor();
            }
            Shape objectClickbox = object.getClickbox();
            if (objectClickbox != null) {
                if (objectClickbox.contains(mousePosition.getX(), mousePosition.getY())) {
                    graphics.setColor(highlightColor.darker());
                } else {
                    graphics.setColor(highlightColor);
                }
                graphics.draw(objectClickbox);
                graphics.setColor(ColorUtil.colorWithAlpha(highlightColor, highlightColor.getAlpha() / 5));
                graphics.fill(objectClickbox);
            }
        });
        return null;
    }
}

