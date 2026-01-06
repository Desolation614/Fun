/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Splitter
 *  com.google.common.base.Strings
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Point
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.worldmap.WorldMap
 */
package net.runelite.client.ui.overlay.worldmap;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ColorUtil;

@Singleton
public class WorldMapOverlay
extends Overlay {
    private static final String FOCUS_ON = "Focus on";
    private static final int TOOLTIP_OFFSET_HEIGHT = 25;
    private static final int TOOLTIP_OFFSET_WIDTH = 5;
    private static final int TOOLTIP_PADDING_HEIGHT = 1;
    private static final int TOOLTIP_PADDING_WIDTH = 2;
    private static final int TOOLTIP_TEXT_OFFSET_HEIGHT = -2;
    private static final Splitter TOOLTIP_SPLITTER = Splitter.on((String)"<br>").trimResults().omitEmptyStrings();
    private final WorldMapPointManager worldMapPointManager;
    private final Client client;
    private WorldMapPoint hoveredPoint;

    @Inject
    private WorldMapOverlay(Client client2, WorldMapPointManager worldMapPointManager) {
        this.client = client2;
        this.worldMapPointManager = worldMapPointManager;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(1.0f);
        this.setLayer(OverlayLayer.MANUAL);
        this.drawAfterInterface(595);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<WorldMapPoint> points = this.worldMapPointManager.getWorldMapPoints();
        if (points.isEmpty()) {
            return null;
        }
        Widget widget = this.client.getWidget(38993927);
        Widget bottomBar = this.client.getWidget(38993943);
        if (widget == null || bottomBar == null) {
            return null;
        }
        bottomBar.setOnTimerListener(new Object[]{ev -> {
            WorldMapPoint worldPoint = this.hoveredPoint;
            if (this.client.isMenuOpen() || worldPoint == null) {
                return;
            }
            this.client.createMenuEntry(-1).setTarget(ColorUtil.wrapWithColorTag(worldPoint.getName(), JagexColors.MENU_TARGET)).setOption(FOCUS_ON).setType(MenuAction.RUNELITE).onClick(m -> this.client.getWorldMap().setWorldMapPositionTarget((WorldPoint)MoreObjects.firstNonNull((Object)worldPoint.getTarget(), (Object)worldPoint.getWorldPoint())));
        }});
        bottomBar.setHasListener(true);
        Rectangle worldMapRectangle = widget.getBounds();
        Shape mapViewArea = this.getWorldMapClipArea(worldMapRectangle);
        Rectangle canvasBounds = new Rectangle(0, 0, this.client.getCanvasWidth(), this.client.getCanvasHeight());
        Shape canvasViewArea = this.getWorldMapClipArea(canvasBounds);
        Shape currentClip = null;
        Point mousePos = this.client.getMouseCanvasPosition();
        if (!mapViewArea.contains(mousePos.getX(), mousePos.getY())) {
            mousePos = null;
        }
        this.hoveredPoint = null;
        WorldMapPoint tooltipPoint = null;
        for (WorldMapPoint worldPoint : points) {
            Point drawPoint;
            BufferedImage image = worldPoint.getImage();
            WorldPoint point = worldPoint.getWorldPoint();
            if (image == null || point == null || (drawPoint = this.mapWorldPointToGraphicsPoint(point)) == null) continue;
            if (worldPoint.isSnapToEdge() && canvasViewArea != currentClip) {
                graphics.setClip(canvasViewArea);
                currentClip = canvasViewArea;
            } else if (!worldPoint.isSnapToEdge() && mapViewArea != currentClip) {
                graphics.setClip(mapViewArea);
                currentClip = mapViewArea;
            }
            if (worldPoint.isSnapToEdge()) {
                Rectangle snappedRect = widget.getBounds();
                snappedRect.grow(-image.getWidth() / 2, -image.getHeight() / 2);
                Rectangle unsnappedRect = new Rectangle(snappedRect);
                if (worldPoint.getImagePoint() != null) {
                    int dx = worldPoint.getImagePoint().getX() - image.getWidth() / 2;
                    int dy = worldPoint.getImagePoint().getY() - image.getHeight() / 2;
                    unsnappedRect.translate(dx, dy);
                }
                if (worldPoint.isCurrentlyEdgeSnapped()) {
                    unsnappedRect.grow(-image.getWidth(), -image.getHeight());
                }
                if (unsnappedRect.contains(drawPoint.getX(), drawPoint.getY())) {
                    if (worldPoint.isCurrentlyEdgeSnapped()) {
                        worldPoint.setCurrentlyEdgeSnapped(false);
                        worldPoint.onEdgeUnsnap();
                    }
                } else {
                    drawPoint = this.clipToRectangle(drawPoint, snappedRect);
                    if (!worldPoint.isCurrentlyEdgeSnapped()) {
                        worldPoint.setCurrentlyEdgeSnapped(true);
                        worldPoint.onEdgeSnap();
                    }
                }
            }
            int drawX = drawPoint.getX();
            int drawY = drawPoint.getY();
            if (worldPoint.getImagePoint() == null) {
                drawX -= image.getWidth() / 2;
                drawY -= image.getHeight() / 2;
            } else {
                drawX -= worldPoint.getImagePoint().getX();
                drawY -= worldPoint.getImagePoint().getY();
            }
            graphics.drawImage((Image)image, drawX, drawY, null);
            Rectangle clickbox = new Rectangle(drawX, drawY, image.getWidth(), image.getHeight());
            if (mousePos == null || !clickbox.contains(mousePos.getX(), mousePos.getY())) continue;
            if (!Strings.isNullOrEmpty((String)worldPoint.getTooltip())) {
                tooltipPoint = worldPoint;
            }
            if (!worldPoint.isJumpOnClick()) continue;
            assert (worldPoint.getName() != null);
            this.hoveredPoint = worldPoint;
        }
        Widget rsTooltip = this.client.getWidget(38993961);
        if (rsTooltip != null) {
            rsTooltip.setHidden(tooltipPoint != null);
        }
        if (tooltipPoint != null) {
            this.drawTooltip(graphics, tooltipPoint);
        }
        return null;
    }

    public Point mapWorldPointToGraphicsPoint(WorldPoint worldPoint) {
        WorldMap worldMap = this.client.getWorldMap();
        if (!worldMap.getWorldMapData().surfaceContainsPosition(worldPoint.getX(), worldPoint.getY())) {
            return null;
        }
        float pixelsPerTile = worldMap.getWorldMapZoom();
        Widget map = this.client.getWidget(38993927);
        if (map != null) {
            Rectangle worldMapRect = map.getBounds();
            int widthInTiles = (int)Math.ceil(worldMapRect.getWidth() / (double)pixelsPerTile);
            int heightInTiles = (int)Math.ceil(worldMapRect.getHeight() / (double)pixelsPerTile);
            Point worldMapPosition = worldMap.getWorldMapPosition();
            int yTileMax = worldMapPosition.getY() - heightInTiles / 2;
            int yTileOffset = (yTileMax - worldPoint.getY() - 1) * -1;
            int xTileOffset = worldPoint.getX() + widthInTiles / 2 - worldMapPosition.getX();
            int xGraphDiff = (int)((float)xTileOffset * pixelsPerTile);
            int yGraphDiff = (int)((float)yTileOffset * pixelsPerTile);
            yGraphDiff = (int)((double)yGraphDiff - ((double)pixelsPerTile - Math.ceil(pixelsPerTile / 2.0f)));
            xGraphDiff = (int)((double)xGraphDiff + ((double)pixelsPerTile - Math.ceil(pixelsPerTile / 2.0f)));
            yGraphDiff = worldMapRect.height - yGraphDiff;
            return new Point(xGraphDiff += (int)worldMapRect.getX(), yGraphDiff += (int)worldMapRect.getY());
        }
        return null;
    }

    private Shape getWorldMapClipArea(Rectangle baseRectangle) {
        Widget overview = this.client.getWidget(38993930);
        Widget surfaceSelector = this.client.getWidget(38993954);
        Area clipArea = new Area(baseRectangle);
        boolean subtracted = false;
        if (overview != null && !overview.isHidden()) {
            clipArea.subtract(new Area(overview.getBounds()));
            subtracted = true;
        }
        if (surfaceSelector != null && !surfaceSelector.isHidden()) {
            clipArea.subtract(new Area(surfaceSelector.getBounds()));
            subtracted = true;
        }
        return subtracted ? clipArea : baseRectangle;
    }

    private void drawTooltip(Graphics2D graphics, WorldMapPoint worldPoint) {
        String tooltip = worldPoint.getTooltip();
        Point drawPoint = this.mapWorldPointToGraphicsPoint(worldPoint.getWorldPoint());
        if (tooltip == null || tooltip.length() <= 0 || drawPoint == null) {
            return;
        }
        List rows = TOOLTIP_SPLITTER.splitToList((CharSequence)tooltip);
        if (rows.isEmpty()) {
            return;
        }
        drawPoint = new Point(drawPoint.getX() + 5, drawPoint.getY() + 25);
        Rectangle bounds = new Rectangle(0, 0, this.client.getCanvasWidth(), this.client.getCanvasHeight());
        Shape mapArea = this.getWorldMapClipArea(bounds);
        graphics.setClip(mapArea);
        graphics.setColor(JagexColors.TOOLTIP_BACKGROUND);
        graphics.setFont(FontManager.getRunescapeFont());
        FontMetrics fm = graphics.getFontMetrics();
        int width = rows.stream().map(fm::stringWidth).max(Integer::compareTo).get();
        int height = fm.getHeight();
        Rectangle tooltipRect = new Rectangle(drawPoint.getX() - 2, drawPoint.getY() - 1, width + 4, height * rows.size() + 2);
        graphics.fillRect((int)tooltipRect.getX(), (int)tooltipRect.getY(), (int)tooltipRect.getWidth(), (int)tooltipRect.getHeight());
        graphics.setColor(JagexColors.TOOLTIP_BORDER);
        graphics.drawRect((int)tooltipRect.getX(), (int)tooltipRect.getY(), (int)tooltipRect.getWidth(), (int)tooltipRect.getHeight());
        graphics.setColor(JagexColors.TOOLTIP_TEXT);
        for (int i = 0; i < rows.size(); ++i) {
            graphics.drawString((String)rows.get(i), drawPoint.getX(), drawPoint.getY() + -2 + (i + 1) * height);
        }
    }

    private Point clipToRectangle(Point drawPoint, Rectangle mapDisplayRectangle) {
        int clippedX = drawPoint.getX();
        if ((double)drawPoint.getX() < mapDisplayRectangle.getX()) {
            clippedX = (int)mapDisplayRectangle.getX();
        }
        if ((double)drawPoint.getX() > mapDisplayRectangle.getX() + mapDisplayRectangle.getWidth()) {
            clippedX = (int)(mapDisplayRectangle.getX() + mapDisplayRectangle.getWidth());
        }
        int clippedY = drawPoint.getY();
        if ((double)drawPoint.getY() < mapDisplayRectangle.getY()) {
            clippedY = (int)mapDisplayRectangle.getY();
        }
        if ((double)drawPoint.getY() > mapDisplayRectangle.getY() + mapDisplayRectangle.getHeight()) {
            clippedY = (int)(mapDisplayRectangle.getY() + mapDisplayRectangle.getHeight());
        }
        return new Point(clippedX, clippedY);
    }
}

