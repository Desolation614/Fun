/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.playerindicators;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.client.plugins.playerindicators.PlayerIndicatorsConfig;
import net.runelite.client.plugins.playerindicators.PlayerIndicatorsService;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class PlayerIndicatorsTileOverlay
extends Overlay {
    private final PlayerIndicatorsService playerIndicatorsService;
    private final PlayerIndicatorsConfig config;

    @Inject
    private PlayerIndicatorsTileOverlay(PlayerIndicatorsConfig config, PlayerIndicatorsService playerIndicatorsService) {
        this.config = config;
        this.playerIndicatorsService = playerIndicatorsService;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(0.5f);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.config.drawTiles()) {
            return null;
        }
        this.playerIndicatorsService.forEachPlayer((player, decorations) -> {
            Polygon poly = player.getCanvasTilePoly();
            if (poly != null) {
                OverlayUtil.renderPolygon(graphics, poly, decorations.getColor());
            }
        });
        return null;
    }
}

