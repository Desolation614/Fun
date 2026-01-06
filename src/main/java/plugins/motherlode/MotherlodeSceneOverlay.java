/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.Perspective
 *  net.runelite.api.Player
 *  net.runelite.api.Point
 *  net.runelite.api.Skill
 *  net.runelite.api.WallObject
 *  net.runelite.api.coords.LocalPoint
 */
package net.runelite.client.plugins.motherlode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.motherlode.MotherlodeConfig;
import net.runelite.client.plugins.motherlode.MotherlodePlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class MotherlodeSceneOverlay
extends Overlay {
    private static final int MAX_DISTANCE = 2350;
    private static final int IMAGE_Z_OFFSET = 20;
    private final Client client;
    private final MotherlodePlugin plugin;
    private final MotherlodeConfig config;
    private final BufferedImage miningIcon;
    private final BufferedImage hammerIcon;

    @Inject
    MotherlodeSceneOverlay(Client client, MotherlodePlugin plugin, MotherlodeConfig config, SkillIconManager iconManager, ItemManager itemManager) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.miningIcon = iconManager.getSkillImage(Skill.MINING);
        this.hammerIcon = itemManager.getImage(2347);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.config.showVeins() && !this.config.showRockFalls() || !this.plugin.isInMlm()) {
            return null;
        }
        Player local = this.client.getLocalPlayer();
        this.renderTiles(graphics, local);
        return null;
    }

    private void renderTiles(Graphics2D graphics, Player local) {
        LocalPoint location;
        LocalPoint localLocation = local.getLocalLocation();
        if (this.config.showVeins()) {
            for (WallObject vein : this.plugin.getVeins()) {
                location = vein.getLocalLocation();
                if (localLocation.distanceTo(location) > 2350 || this.plugin.isUpstairs(localLocation) != this.plugin.isUpstairs(vein.getLocalLocation())) continue;
                this.renderVein(graphics, vein);
            }
        }
        if (this.config.showRockFalls()) {
            for (GameObject rock : this.plugin.getRocks()) {
                location = rock.getLocalLocation();
                if (localLocation.distanceTo(location) > 2350) continue;
                this.renderRock(graphics, rock);
            }
        }
        if (this.config.showBrokenStruts()) {
            for (GameObject brokenStrut : this.plugin.getBrokenStruts()) {
                location = brokenStrut.getLocalLocation();
                if (localLocation.distanceTo(location) > 2350) continue;
                this.renderBrokenStrut(graphics, brokenStrut);
            }
        }
    }

    private void renderVein(Graphics2D graphics, WallObject vein) {
        Point canvasLoc = Perspective.getCanvasImageLocation((Client)this.client, (LocalPoint)vein.getLocalLocation(), (BufferedImage)this.miningIcon, (int)150);
        if (canvasLoc != null) {
            graphics.drawImage((Image)this.miningIcon, canvasLoc.getX(), canvasLoc.getY(), null);
        }
    }

    private void renderRock(Graphics2D graphics, GameObject rock) {
        Polygon poly = Perspective.getCanvasTilePoly((Client)this.client, (LocalPoint)rock.getLocalLocation());
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, Color.red);
        }
    }

    private void renderBrokenStrut(Graphics2D graphics, GameObject brokenStrut) {
        Polygon poly = Perspective.getCanvasTilePoly((Client)this.client, (LocalPoint)brokenStrut.getLocalLocation());
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, Color.red);
            OverlayUtil.renderImageLocation(this.client, graphics, brokenStrut.getLocalLocation(), this.hammerIcon, 20);
        }
    }
}

