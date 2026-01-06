/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.DecorativeObject
 *  net.runelite.api.Player
 *  net.runelite.api.Point
 */
package net.runelite.client.plugins.runecraft;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.plugins.runecraft.AbyssRifts;
import net.runelite.client.plugins.runecraft.RunecraftConfig;
import net.runelite.client.plugins.runecraft.RunecraftPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class AbyssOverlay
extends Overlay {
    private final Client client;
    private final RunecraftPlugin plugin;
    private final RunecraftConfig config;

    @Inject
    AbyssOverlay(Client client, RunecraftPlugin plugin, RunecraftConfig config) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Player player = this.client.getLocalPlayer();
        if (player == null) {
            return null;
        }
        int region = player.getWorldLocation().getRegionID();
        Set<DecorativeObject> abyssObjects = this.plugin.getAbyssObjects();
        if (region != 12107 || abyssObjects.isEmpty() || !this.config.showRifts() || !this.config.showClickBox()) {
            return null;
        }
        for (DecorativeObject object : abyssObjects) {
            this.renderRift(graphics, object);
        }
        return null;
    }

    private void renderRift(Graphics2D graphics, DecorativeObject object) {
        AbyssRifts rift = AbyssRifts.getRift(object.getId());
        if (rift == null || !rift.getConfigEnabled().test(this.config)) {
            return;
        }
        Point mousePosition = this.client.getMouseCanvasPosition();
        Shape objectClickbox = object.getClickbox();
        if (objectClickbox != null) {
            if (objectClickbox.contains(mousePosition.getX(), mousePosition.getY())) {
                graphics.setColor(Color.MAGENTA.darker());
            } else {
                graphics.setColor(Color.MAGENTA);
            }
            graphics.draw(objectClickbox);
            graphics.setColor(new Color(255, 0, 255, 20));
            graphics.fill(objectClickbox);
        }
    }
}

