/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Point
 */
package net.runelite.client.plugins.tearsofguthix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.client.plugins.tearsofguthix.TearsOfGuthixConfig;
import net.runelite.client.plugins.tearsofguthix.TearsOfGuthixPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.client.util.ColorUtil;

class TearsOfGuthixOverlay
extends Overlay {
    private static final Duration MAX_TIME = Duration.ofSeconds(9L);
    private final TearsOfGuthixConfig config;
    private final TearsOfGuthixPlugin plugin;

    @Inject
    private TearsOfGuthixOverlay(TearsOfGuthixConfig config, TearsOfGuthixPlugin plugin) {
        this.config = config;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.plugin.getStreams().isEmpty()) {
            return null;
        }
        Color blueTearsFill = this.config.getBlueTearsColor();
        Color greenTearsFill = this.config.getGreenTearsColor();
        Color blueTearsBorder = ColorUtil.colorWithAlpha(blueTearsFill, 255);
        Color greenTearsBorder = ColorUtil.colorWithAlpha(greenTearsFill, 255);
        this.plugin.getStreams().forEach((object, timer) -> {
            if (!(object.getId() != 6662 && object.getId() != 6666 || this.config.showGreenTearsTimer())) {
                return;
            }
            Point position = object.getCanvasLocation(100);
            if (position == null) {
                return;
            }
            ProgressPieComponent progressPie = new ProgressPieComponent();
            progressPie.setDiameter(15);
            if (object.getId() == 6661 || object.getId() == 6665) {
                progressPie.setFill(blueTearsFill);
                progressPie.setBorderColor(blueTearsBorder);
            } else if (object.getId() == 6662 || object.getId() == 6666) {
                progressPie.setFill(greenTearsFill);
                progressPie.setBorderColor(greenTearsBorder);
            }
            progressPie.setPosition(position);
            Duration duration = Duration.between(timer, Instant.now());
            progressPie.setProgress(1.0 - (duration.compareTo(MAX_TIME) < 0 ? (double)duration.toMillis() / (double)MAX_TIME.toMillis() : 1.0));
            progressPie.render(graphics);
        });
        return null;
    }
}

