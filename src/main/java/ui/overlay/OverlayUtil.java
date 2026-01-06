/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.TileObject
 *  net.runelite.api.coords.LocalPoint
 */
package net.runelite.client.ui.overlay;

import com.google.common.base.Strings;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ColorUtil;

public class OverlayUtil {
    private static final int MINIMAP_DOT_RADIUS = 4;

    public static void renderPolygon(Graphics2D graphics, Shape poly, Color color) {
        OverlayUtil.renderPolygon(graphics, poly, color, new BasicStroke(2.0f));
    }

    public static void renderPolygon(Graphics2D graphics, Shape poly, Color color, Stroke borderStroke) {
        OverlayUtil.renderPolygon(graphics, poly, color, new Color(0, 0, 0, 50), borderStroke);
    }

    public static void renderPolygon(Graphics2D graphics, Shape poly, Color color, Color fillColor, Stroke borderStroke) {
        graphics.setColor(color);
        Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(borderStroke);
        graphics.draw(poly);
        graphics.setColor(fillColor);
        graphics.fill(poly);
        graphics.setStroke(originalStroke);
    }

    public static void renderMinimapLocation(Graphics2D graphics, Point mini, Color color) {
        graphics.setColor(Color.BLACK);
        graphics.fillOval(mini.getX() - 2, mini.getY() - 2 + 1, 4, 4);
        graphics.setColor(ColorUtil.colorWithAlpha(color, 255));
        graphics.fillOval(mini.getX() - 2, mini.getY() - 2, 4, 4);
    }

    @Deprecated
    public static void renderMinimapRect(Client client2, Graphics2D graphics, Point center, int width, int height, Color color) {
        double angle = (double)client2.getCameraYawTarget() * 0.0030679615757712823;
        graphics.setColor(color);
        graphics.rotate(angle, center.getX(), center.getY());
        graphics.drawRect(center.getX() - width / 2, center.getY() - height / 2, width - 1, height - 1);
        graphics.rotate(-angle, center.getX(), center.getY());
    }

    public static void renderTextLocation(Graphics2D graphics, Point txtLoc, String text, Color color) {
        if (Strings.isNullOrEmpty((String)text)) {
            return;
        }
        int x = txtLoc.getX();
        int y = txtLoc.getY();
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 1, y + 1);
        graphics.setColor(ColorUtil.colorWithAlpha(color, 255));
        graphics.drawString(text, x, y);
    }

    public static void renderImageLocation(Client client2, Graphics2D graphics, LocalPoint localPoint, BufferedImage image, int zOffset) {
        Point imageLocation = Perspective.getCanvasImageLocation((Client)client2, (LocalPoint)localPoint, (BufferedImage)image, (int)zOffset);
        if (imageLocation != null) {
            OverlayUtil.renderImageLocation(graphics, imageLocation, image);
        }
    }

    public static void renderImageLocation(Graphics2D graphics, Point imgLoc, BufferedImage image) {
        int x = imgLoc.getX();
        int y = imgLoc.getY();
        graphics.drawImage((Image)image, x, y, null);
    }

    public static void renderActorOverlay(Graphics2D graphics, Actor actor, String text, Color color) {
        Point textLocation;
        Polygon poly = actor.getCanvasTilePoly();
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }
        if ((textLocation = actor.getCanvasTextLocation(graphics, text, actor.getLogicalHeight() + 40)) != null) {
            OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
        }
    }

    public static void renderActorOverlayImage(Graphics2D graphics, Actor actor, BufferedImage image, Color color, int zOffset) {
        Point imageLocation;
        Polygon poly = actor.getCanvasTilePoly();
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }
        if ((imageLocation = actor.getCanvasImageLocation(image, zOffset)) != null) {
            OverlayUtil.renderImageLocation(graphics, imageLocation, image);
        }
    }

    public static void renderTileOverlay(Graphics2D graphics, TileObject tileObject, String text, Color color) {
        Point textLocation;
        Point minimapLocation;
        Polygon poly = tileObject.getCanvasTilePoly();
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }
        if ((minimapLocation = tileObject.getMinimapLocation()) != null) {
            OverlayUtil.renderMinimapLocation(graphics, minimapLocation, color);
        }
        if ((textLocation = tileObject.getCanvasTextLocation(graphics, text, 0)) != null) {
            OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
        }
    }

    public static void renderTileOverlay(Client client2, Graphics2D graphics, LocalPoint localLocation, BufferedImage image, Color color) {
        Polygon poly = Perspective.getCanvasTilePoly((Client)client2, (LocalPoint)localLocation);
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }
        OverlayUtil.renderImageLocation(client2, graphics, localLocation, image, 0);
    }

    public static void renderHoverableArea(Graphics2D graphics, Shape area, Point mousePosition, Color fillColor, Color borderColor, Color borderHoverColor) {
        if (area != null) {
            if (area.contains(mousePosition.getX(), mousePosition.getY())) {
                graphics.setColor(borderHoverColor);
            } else {
                graphics.setColor(borderColor);
            }
            graphics.draw(area);
            graphics.setColor(fillColor);
            graphics.fill(area);
        }
    }

    public static void setGraphicProperties(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    static void shiftSnapCorner(OverlayPosition overlayPosition, Rectangle snapCorner, Rectangle bounds, int padding) {
        int sX = snapCorner.x;
        int sY = snapCorner.y;
        switch (overlayPosition) {
            case BOTTOM_LEFT: {
                sX = Math.max(sX, bounds.x + bounds.width + padding);
                break;
            }
            case BOTTOM_RIGHT: {
                sX = Math.min(sX, bounds.x - padding);
                break;
            }
            case TOP_LEFT: 
            case TOP_CENTER: 
            case CANVAS_TOP_RIGHT: 
            case TOP_RIGHT: {
                sY = Math.max(sY, bounds.y + bounds.height + padding);
                break;
            }
            case ABOVE_CHATBOX_RIGHT: {
                sY = Math.min(sY, bounds.y - padding);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        snapCorner.x = sX;
        snapCorner.y = sY;
    }

    public static java.awt.Point transformPosition(OverlayPosition position, Dimension dimension) {
        java.awt.Point result = new java.awt.Point();
        switch (position) {
            case TOP_LEFT: {
                break;
            }
            case TOP_CENTER: {
                result.x = -dimension.width / 2;
                break;
            }
            case BOTTOM_LEFT: {
                result.y = -dimension.height;
                break;
            }
            case BOTTOM_RIGHT: 
            case ABOVE_CHATBOX_RIGHT: {
                result.y = -dimension.height;
            }
            case CANVAS_TOP_RIGHT: 
            case TOP_RIGHT: {
                result.x = -dimension.width;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return result;
    }
}

