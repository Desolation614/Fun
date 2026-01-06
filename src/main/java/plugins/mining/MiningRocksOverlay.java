/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.mining;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mining.MiningPlugin;
import net.runelite.client.plugins.mining.Rock;
import net.runelite.client.plugins.mining.RockRespawn;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

class MiningRocksOverlay
extends Overlay {
    static final int DAEYALT_MAX_RESPAWN_TIME = 110;
    private static final int DAEYALT_MIN_RESPAWN_TIME = 91;
    private static final float DAEYALT_RANDOM_PERCENT_THRESHOLD = 0.8272727f;
    private static final Color DARK_GREEN = new Color(0, 100, 0);
    private static final int MOTHERLODE_UPPER_FLOOR_HEIGHT = -500;
    private final Client client;
    private final MiningPlugin plugin;

    @Inject
    private MiningRocksOverlay(Client client, MiningPlugin plugin) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<RockRespawn> respawns = this.plugin.getRespawns();
        if (respawns.isEmpty()) {
            return null;
        }
        Instant now = Instant.now();
        for (RockRespawn rockRespawn : respawns) {
            LocalPoint loc = LocalPoint.fromWorld((Client)this.client, (WorldPoint)rockRespawn.getWorldPoint());
            if (loc == null) continue;
            float percent = (float)(now.toEpochMilli() - rockRespawn.getStartTime().toEpochMilli()) / (float)rockRespawn.getRespawnTime();
            Point point = Perspective.localToCanvas((Client)this.client, (LocalPoint)loc, (int)this.client.getPlane(), (int)rockRespawn.getZOffset());
            if (point == null || percent > 1.0f) continue;
            Rock rock = rockRespawn.getRock();
            LocalPoint localLocation = this.client.getLocalPlayer().getLocalLocation();
            if (rock == Rock.MLM_ORE_VEIN && this.isUpstairsMotherlode(localLocation) != this.isUpstairsMotherlode(loc)) continue;
            Color pieFillColor = Color.YELLOW;
            Color pieBorderColor = Color.ORANGE;
            if (rock == Rock.DAEYALT_ESSENCE && percent > 0.8272727f) {
                pieFillColor = Color.GREEN;
                pieBorderColor = DARK_GREEN;
            }
            ProgressPieComponent ppc = new ProgressPieComponent();
            ppc.setBorderColor(pieBorderColor);
            ppc.setFill(pieFillColor);
            ppc.setPosition(point);
            ppc.setProgress(percent);
            ppc.render(graphics);
        }
        return null;
    }

    private boolean isUpstairsMotherlode(LocalPoint localPoint) {
        return Perspective.getTileHeight((Client)this.client, (LocalPoint)localPoint, (int)0) < -500;
    }
}

