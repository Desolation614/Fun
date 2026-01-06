/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.NPC
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 */
package net.runelite.client.plugins.slayer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.slayer.SlayerConfig;
import net.runelite.client.plugins.slayer.SlayerPlugin;
import net.runelite.client.plugins.slayer.Task;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.AsyncBufferedImage;

class TargetWeaknessOverlay
extends Overlay {
    private final Client client;
    private final SlayerConfig config;
    private final SlayerPlugin plugin;
    private final ItemManager itemManager;
    private final NPCManager npcManager;

    @Inject
    private TargetWeaknessOverlay(Client client, SlayerConfig config, SlayerPlugin plugin, ItemManager itemManager, NPCManager npcManager) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.npcManager = npcManager;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<NPC> targets = this.plugin.getTargets();
        if (targets.isEmpty() || !this.config.weaknessPrompt()) {
            return null;
        }
        Task curTask = Task.getTask(this.plugin.getTaskName());
        if (curTask == null || curTask.getWeaknessThreshold() < 0 || curTask.getWeaknessItem() < 0) {
            return null;
        }
        int threshold = curTask.getWeaknessThreshold();
        AsyncBufferedImage image = this.itemManager.getImage(curTask.getWeaknessItem());
        if (image == null) {
            return null;
        }
        for (NPC target : targets) {
            int currentHealth = this.calculateHealth(target);
            if (currentHealth < 0 || currentHealth > threshold) continue;
            this.renderTargetItem(graphics, target, image);
        }
        return null;
    }

    private int calculateHealth(NPC target) {
        if (target == null || target.getName() == null) {
            return -1;
        }
        int healthScale = target.getHealthScale();
        int healthRatio = target.getHealthRatio();
        Integer maxHealth = this.npcManager.getHealth(target.getId());
        if (healthRatio < 0 || healthScale <= 0 || maxHealth == null) {
            return -1;
        }
        return (int)((float)(maxHealth * healthRatio / healthScale) + 0.5f);
    }

    private void renderTargetItem(Graphics2D graphics, NPC actor, BufferedImage image) {
        LocalPoint actorPosition = actor.getLocalLocation();
        int offset = actor.getLogicalHeight() + 40;
        if (actorPosition == null || image == null) {
            return;
        }
        Point imageLoc = Perspective.getCanvasImageLocation((Client)this.client, (LocalPoint)actorPosition, (BufferedImage)image, (int)offset);
        if (imageLoc != null) {
            OverlayUtil.renderImageLocation(graphics, imageLoc, image);
        }
    }
}

