/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 */
package net.runelite.client.plugins.poh;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.poh.PohConfig;
import net.runelite.client.plugins.poh.PohPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

class BurnerOverlay
extends Overlay {
    private final Client client;
    private final PohConfig config;
    private final PohPlugin plugin;

    @Inject
    private BurnerOverlay(Client client, PohConfig config, PohPlugin plugin) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.config.showBurner()) {
            return null;
        }
        this.plugin.getIncenseBurners().forEach((tile, burner) -> {
            if (tile.getPlane() != this.client.getPlane() || !burner.isLit()) {
                return;
            }
            Instant now = Instant.now();
            long startCountdown = Duration.between(burner.getStart(), now).getSeconds();
            double certainSec = burner.getCountdownTimer() - (double)startCountdown;
            long endCountdown = 0L;
            if (certainSec <= 0.0) {
                if (burner.getEnd() == null) {
                    burner.setEnd(Instant.now());
                }
                endCountdown = Duration.between(burner.getEnd(), now).getSeconds();
            }
            double randomSec = burner.getRandomTimer() - (double)endCountdown;
            ProgressPieComponent pieComponent = new ProgressPieComponent();
            Point loc = Perspective.localToCanvas((Client)this.client, (LocalPoint)tile.getLocalLocation(), (int)tile.getPlane());
            if (loc == null) {
                return;
            }
            pieComponent.setPosition(loc);
            if (certainSec > 0.0) {
                pieComponent.setProgress(certainSec / burner.getCountdownTimer());
                pieComponent.setFill(Color.GREEN);
                pieComponent.setBorderColor(Color.GREEN);
                pieComponent.render(graphics);
            } else if (randomSec > 0.0) {
                pieComponent.setProgress(randomSec / burner.getRandomTimer());
                pieComponent.setFill(Color.ORANGE);
                pieComponent.setBorderColor(Color.ORANGE);
                pieComponent.render(graphics);
            }
        });
        return null;
    }
}

