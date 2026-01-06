/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Point
 *  net.runelite.api.widgets.WidgetItem
 */
package net.runelite.client.plugins.ferox;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.ferox.FeroxConfig;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

class ClueScrollStepOverlay
extends WidgetItemOverlay {
    private static final Set<Integer> WILDERNESS_CLUES = Set.of(2729, 2731, 2776, 2780, 2783, 2785, 2786, 2790, 2792, 2793, 2794, 2796, 2797, 2799, 3524, 3525, 3526, 3528, 7239, 12073, 12086, 12087, 12088, 12090, 12093, 12096, 55058, 55060, 55062, 55068, 55071, 55073, 55074, 55075, 55076, 55626, 55627, 55628, 55629, 55630, 55631, 55632, 55633, 55634, 55635, 55636, 55637, 55638, 55639, 55640, 55641, 55642, 55643, 55644, 55645, 55646);
    private final FeroxConfig config;
    private final SpriteManager spriteManager;

    @Inject
    ClueScrollStepOverlay(FeroxConfig config, SpriteManager spriteManager) {
        this.config = config;
        this.spriteManager = spriteManager;
        this.showOnInventory();
        this.showOnBank();
        this.showOnInterfaces(4, 1016, 1030);
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        BufferedImage skull;
        if (this.config.displayWildernessClueScrollStep() && WILDERNESS_CLUES.contains(itemId) && (skull = this.spriteManager.getSprite(2165, 0)) != null) {
            Point location = widgetItem.getCanvasLocation();
            OverlayUtil.renderImageLocation(graphics, new Point(location.getX() + 19, location.getY() + 2), skull);
        }
    }
}

