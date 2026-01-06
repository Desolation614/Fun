/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.NPC
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.woodcutting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.woodcutting.TreeRespawn;
import net.runelite.client.plugins.woodcutting.WoodcuttingConfig;
import net.runelite.client.plugins.woodcutting.WoodcuttingPlugin;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.client.util.ColorUtil;

class WoodcuttingSceneOverlay
extends Overlay {
    private static final Color ROTTING_LEAVES = new Color(179, 0, 0);
    private static final Color GREEN_LEAVES = new Color(0, 179, 0);
    private static final Color DROPPINGS = new Color(120, 88, 76);
    private static final Color WILD_MUSHROOMS = new Color(220, 220, 220);
    private static final Color SPLINTERED_BARK = new Color(0, 0, 179);
    private final Client client;
    private final WoodcuttingConfig config;
    private final ItemManager itemManager;
    private final WoodcuttingPlugin plugin;

    @Inject
    private WoodcuttingSceneOverlay(Client client, WoodcuttingConfig config, ItemManager itemManager, WoodcuttingPlugin plugin) {
        this.client = client;
        this.config = config;
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        this.renderRedwoods(graphics);
        this.renderTimers(graphics);
        this.renderForestryRoots(graphics);
        this.renderForestrySapling(graphics);
        this.renderForestryFlowers(graphics);
        this.renderForestryPoachers(graphics);
        this.renderForestryPheasants(graphics);
        this.renderForestryBeeHive(graphics);
        this.renderEnchantmentRitual(graphics);
        this.renderLeprechaun(graphics);
        return null;
    }

    private void renderForestryRoots(Graphics2D graphics) {
        if (!this.plugin.getRoots().isEmpty() && this.config.highlightGlowingRoots()) {
            for (GameObject treeRoot : this.plugin.getRoots()) {
                Shape clickbox;
                if (treeRoot.getId() != 47483 || (clickbox = treeRoot.getClickbox()) == null) continue;
                Color color = Color.GREEN;
                graphics.setColor(color);
                graphics.draw(clickbox);
                graphics.setColor(ColorUtil.colorWithAlpha(color, color.getAlpha() / 5));
                graphics.fill(clickbox);
            }
        }
    }

    private void renderForestrySapling(Graphics2D graphics) {
        if (!this.plugin.getSaplingIngredients().isEmpty() && this.config.highlightMulch()) {
            GameObject[] order = this.plugin.getSaplingOrder();
            StringBuilder sb = new StringBuilder();
            graphics.setFont(FontManager.getRunescapeBoldFont().deriveFont(16.0f));
            block7: for (GameObject ingredient : this.plugin.getSaplingIngredients()) {
                String text;
                Point textLocation;
                Color color;
                char letter;
                switch (ingredient.getId()) {
                    case 47493: {
                        letter = 'R';
                        color = ROTTING_LEAVES;
                        break;
                    }
                    case 47494: {
                        letter = 'G';
                        color = GREEN_LEAVES;
                        break;
                    }
                    case 47495: {
                        letter = 'D';
                        color = DROPPINGS;
                        break;
                    }
                    case 47496: 
                    case 47497: 
                    case 47498: {
                        letter = 'M';
                        color = WILD_MUSHROOMS;
                        break;
                    }
                    case 47499: {
                        letter = 'B';
                        color = SPLINTERED_BARK;
                        break;
                    }
                    default: {
                        continue block7;
                    }
                }
                sb.setLength(0);
                sb.append(letter);
                int numSolved = 0;
                boolean found = false;
                for (int i = 0; i < order.length; ++i) {
                    if (order[i] == ingredient) {
                        found = true;
                        if (sb.length() == 1) {
                            sb.append(" -");
                        }
                        sb.append(' ').append(i + 1);
                    }
                    if (order[i] == null) continue;
                    ++numSolved;
                }
                if (numSolved >= 3 && !found) continue;
                Polygon poly = ingredient.getCanvasTilePoly();
                if (poly != null) {
                    OverlayUtil.renderPolygon(graphics, poly, color);
                }
                if ((textLocation = ingredient.getCanvasTextLocation(graphics, text = sb.toString(), 0)) == null) continue;
                OverlayUtil.renderTextLocation(graphics, textLocation, text, Color.WHITE);
            }
        }
    }

    private void renderForestryFlowers(Graphics2D graphics) {
        if (!this.plugin.getFlowers().isEmpty() && this.config.highlightFlowers()) {
            List<NPC> activeFlowers = this.plugin.getActiveFlowers();
            for (NPC flower : this.plugin.getFlowers()) {
                Polygon poly;
                if (activeFlowers.size() == 2 && !activeFlowers.contains(flower) || (poly = flower.getCanvasTilePoly()) == null) continue;
                OverlayUtil.renderPolygon(graphics, poly, Color.YELLOW);
            }
        }
    }

    private void renderForestryPoachers(Graphics2D graphics) {
        NPC foxTrap;
        Polygon poly;
        if (this.plugin.getFoxTrap() != null && this.config.highlightFoxTrap() && (poly = (foxTrap = this.plugin.getFoxTrap()).getCanvasTilePoly()) != null) {
            OverlayUtil.renderPolygon(graphics, poly, Color.RED);
        }
    }

    private void renderForestryPheasants(Graphics2D graphics) {
        if (!this.plugin.getPheasantNests().isEmpty() && this.config.highlightPheasantNest()) {
            Polygon poly;
            for (GameObject nest : this.plugin.getPheasantNests()) {
                Polygon poly2;
                if (nest.getId() != 49937 || (poly2 = nest.getCanvasTilePoly()) == null) continue;
                OverlayUtil.renderPolygon(graphics, poly2, Color.GREEN);
            }
            NPC forester = this.plugin.getFreakyForester();
            if (forester != null && (poly = forester.getCanvasTilePoly()) != null) {
                OverlayUtil.renderPolygon(graphics, poly, Color.GREEN);
            }
        }
    }

    private void renderForestryBeeHive(Graphics2D graphics) {
        NPC beehive;
        Polygon poly;
        if (this.plugin.getUnfinishedBeeHive() != null && this.config.highlightBeeHive() && (poly = (beehive = this.plugin.getUnfinishedBeeHive()).getCanvasTilePoly()) != null) {
            OverlayUtil.renderPolygon(graphics, poly, Color.ORANGE);
        }
    }

    private void renderEnchantmentRitual(Graphics2D graphics) {
        Polygon poly;
        NPC solution = this.plugin.solveCircles();
        if (solution != null && this.config.highlightRitualCircle() && (poly = solution.getCanvasTilePoly()) != null) {
            OverlayUtil.renderPolygon(graphics, poly, Color.GREEN);
        }
    }

    private void renderLeprechaun(Graphics2D graphics) {
        if (!this.plugin.getEndOfRainbows().isEmpty() && this.config.highlightLeprechaunRainbow()) {
            for (GameObject rainbow : this.plugin.getEndOfRainbows()) {
                Polygon poly = rainbow.getCanvasTilePoly();
                if (poly == null) continue;
                OverlayUtil.renderPolygon(graphics, poly, Color.GREEN);
            }
        }
    }

    private void renderRedwoods(Graphics2D graphics) {
        if (this.plugin.getSession() == null || !this.config.showRedwoodTrees()) {
            return;
        }
        for (GameObject treeObject : this.plugin.getRedwoods()) {
            if (treeObject.getWorldLocation().distanceTo(this.client.getLocalPlayer().getWorldLocation()) > 12) continue;
            OverlayUtil.renderImageLocation(this.client, graphics, treeObject.getLocalLocation(), this.itemManager.getImage(19669), 120);
        }
    }

    private void renderTimers(Graphics2D graphics) {
        List<TreeRespawn> respawns = this.plugin.getRespawns();
        if (respawns.isEmpty() || !this.config.showRespawnTimers()) {
            return;
        }
        Instant now = Instant.now();
        for (TreeRespawn treeRespawn : respawns) {
            LocalPoint minLocation = LocalPoint.fromWorld((Client)this.client, (WorldPoint)treeRespawn.getWorldLocation());
            if (minLocation == null) continue;
            LocalPoint centeredLocation = new LocalPoint(minLocation.getX() + treeRespawn.getLenX() * 64, minLocation.getY() + treeRespawn.getLenY() * 64);
            float percent = (float)(now.toEpochMilli() - treeRespawn.getStartTime().toEpochMilli()) / (float)treeRespawn.getRespawnTime();
            Point point = Perspective.localToCanvas((Client)this.client, (LocalPoint)centeredLocation, (int)this.client.getPlane());
            if (point == null || percent > 1.0f) continue;
            ProgressPieComponent ppc = new ProgressPieComponent();
            ppc.setBorderColor(Color.ORANGE);
            ppc.setFill(Color.YELLOW);
            ppc.setPosition(point);
            ppc.setProgress(percent);
            ppc.render(graphics);
        }
    }
}

