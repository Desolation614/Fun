/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Point
 *  net.runelite.api.events.FocusChanged
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.fps;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.fps.FpsConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class FpsOverlay
extends Overlay {
    private static final int Y_OFFSET = 1;
    private static final int X_OFFSET = 1;
    private static final String FPS_STRING = " FPS";
    private final FpsConfig config;
    private final Client client;
    private boolean isFocused = true;

    @Inject
    private FpsOverlay(FpsConfig config, Client client) {
        this.config = config;
        this.client = client;
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.setPriority(0.75f);
        this.setPosition(OverlayPosition.DYNAMIC);
    }

    void onFocusChanged(FocusChanged event) {
        this.isFocused = event.isFocused();
    }

    private boolean isEnforced() {
        return this.config.limitFps() || this.config.limitFpsUnfocused() && !this.isFocused;
    }

    private Color getFpsValueColor() {
        return this.isEnforced() ? Color.red : Color.yellow;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.config.drawFps()) {
            return null;
        }
        Widget logoutButton = this.client.getWidget(10747939);
        int xOffset = 1;
        if (logoutButton != null && !logoutButton.isHidden()) {
            xOffset += logoutButton.getWidth();
        }
        String text = this.client.getFPS() + FPS_STRING;
        int textWidth = graphics.getFontMetrics().stringWidth(text);
        int textHeight = graphics.getFontMetrics().getAscent() - graphics.getFontMetrics().getDescent();
        int width = (int)this.client.getRealDimensions().getWidth();
        Point point = new Point(width - textWidth - xOffset, textHeight + 1);
        OverlayUtil.renderTextLocation(graphics, point, text, this.getFpsValueColor());
        return null;
    }
}

