/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Strings
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.DecorativeObject
 *  net.runelite.api.GameObject
 *  net.runelite.api.GroundObject
 *  net.runelite.api.ObjectComposition
 *  net.runelite.api.TileObject
 *  net.runelite.api.WallObject
 */
package net.runelite.client.plugins.objectindicators;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.client.plugins.objectindicators.ColorTileObject;
import net.runelite.client.plugins.objectindicators.ObjectIndicatorsConfig;
import net.runelite.client.plugins.objectindicators.ObjectIndicatorsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.util.ColorUtil;

class ObjectIndicatorsOverlay
extends Overlay {
    private final Client client;
    private final ObjectIndicatorsConfig config;
    private final ObjectIndicatorsPlugin plugin;
    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private ObjectIndicatorsOverlay(Client client, ObjectIndicatorsConfig config, ObjectIndicatorsPlugin plugin, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(0.0f);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<ColorTileObject> objects = this.plugin.getObjects();
        if (objects.isEmpty()) {
            return null;
        }
        BasicStroke stroke = new BasicStroke((float)this.config.borderWidth());
        int defaultFlags = (this.config.highlightHull() ? 1 : 0) | (this.config.highlightOutline() ? 2 : 0) | (this.config.highlightClickbox() ? 4 : 0) | (this.config.highlightTile() ? 8 : 0);
        for (ColorTileObject obj : objects) {
            Polygon tilePoly;
            Color fillColor;
            Shape clickbox;
            int flags;
            ObjectComposition composition;
            TileObject object = obj.getTileObject();
            if (object.getPlane() != this.client.getPlane() || (composition = obj.getComposition()).getImpostorIds() != null && ((composition = composition.getImpostor()) == null || Strings.isNullOrEmpty((String)composition.getName()) || "null".equals(composition.getName()) || !composition.getName().equals(obj.getName()))) continue;
            Color borderColor = obj.getBorderColor();
            if (borderColor == null) {
                borderColor = this.config.markerColor();
            }
            int n = flags = obj.getHighlightFlags() != 0 ? (int)obj.getHighlightFlags() : defaultFlags;
            if ((flags & 1) != 0) {
                Color fillColor2 = (Color)MoreObjects.firstNonNull((Object)obj.getFillColor(), (Object)new Color(0, 0, 0, 50));
                this.renderConvexHull(graphics, object, borderColor, fillColor2, stroke);
            }
            if ((flags & 2) != 0) {
                this.modelOutlineRenderer.drawOutline(object, (int)this.config.borderWidth(), borderColor, this.config.outlineFeather());
            }
            if ((flags & 4) != 0 && (clickbox = object.getClickbox()) != null) {
                fillColor = (Color)MoreObjects.firstNonNull((Object)obj.getFillColor(), (Object)ColorUtil.colorWithAlpha(borderColor, borderColor.getAlpha() / 12));
                OverlayUtil.renderPolygon(graphics, clickbox, borderColor, fillColor, stroke);
            }
            if ((flags & 8) == 0 || (tilePoly = object.getCanvasTilePoly()) == null) continue;
            fillColor = (Color)MoreObjects.firstNonNull((Object)obj.getFillColor(), (Object)ColorUtil.colorWithAlpha(borderColor, borderColor.getAlpha() / 12));
            OverlayUtil.renderPolygon(graphics, tilePoly, borderColor, fillColor, stroke);
        }
        return null;
    }

    private void renderConvexHull(Graphics2D graphics, TileObject object, Color color, Color fillColor, Stroke stroke) {
        Shape polygon;
        Shape polygon2 = null;
        if (object instanceof GameObject) {
            polygon = ((GameObject)object).getConvexHull();
        } else if (object instanceof WallObject) {
            polygon = ((WallObject)object).getConvexHull();
            polygon2 = ((WallObject)object).getConvexHull2();
        } else if (object instanceof DecorativeObject) {
            polygon = ((DecorativeObject)object).getConvexHull();
            polygon2 = ((DecorativeObject)object).getConvexHull2();
        } else {
            polygon = object instanceof GroundObject ? ((GroundObject)object).getConvexHull() : object.getCanvasTilePoly();
        }
        if (polygon != null) {
            OverlayUtil.renderPolygon(graphics, polygon, color, fillColor, stroke);
        }
        if (polygon2 != null) {
            OverlayUtil.renderPolygon(graphics, polygon2, color, fillColor, stroke);
        }
    }
}

