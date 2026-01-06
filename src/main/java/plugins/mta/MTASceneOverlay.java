/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.mta;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.plugins.mta.MTAPlugin;
import net.runelite.client.plugins.mta.MTARoom;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class MTASceneOverlay
extends Overlay {
    private final MTAPlugin plugin;

    @Inject
    public MTASceneOverlay(MTAPlugin plugin) {
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for (MTARoom room : this.plugin.getRooms()) {
            if (!room.inside()) continue;
            graphics.setFont(FontManager.getRunescapeFont());
            room.under(graphics);
        }
        return null;
    }
}

