/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.DecorativeObject
 *  net.runelite.api.Perspective
 *  net.runelite.api.Player
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 */
package net.runelite.client.plugins.runecraft;

import com.google.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.runecraft.AbyssRifts;
import net.runelite.client.plugins.runecraft.RunecraftConfig;
import net.runelite.client.plugins.runecraft.RunecraftPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class AbyssMinimapOverlay
extends Overlay {
    private static final Dimension IMAGE_SIZE = new Dimension(15, 14);
    private final Map<AbyssRifts, BufferedImage> abyssIcons = new HashMap<AbyssRifts, BufferedImage>();
    private final Client client;
    private final RunecraftPlugin plugin;
    private final RunecraftConfig config;
    private final ItemManager itemManager;

    @Inject
    AbyssMinimapOverlay(Client client, RunecraftPlugin plugin, RunecraftConfig config, ItemManager itemManager) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Player player = this.client.getLocalPlayer();
        if (player == null) {
            return null;
        }
        int region = player.getWorldLocation().getRegionID();
        if (region != 12107 || !this.config.showRifts()) {
            return null;
        }
        for (DecorativeObject object : this.plugin.getAbyssObjects()) {
            AbyssRifts rift = AbyssRifts.getRift(object.getId());
            if (rift == null || !rift.getConfigEnabled().test(this.config)) continue;
            BufferedImage image = this.getImage(rift);
            Point miniMapImage = Perspective.getMiniMapImageLocation((Client)this.client, (LocalPoint)object.getLocalLocation(), (BufferedImage)image);
            if (miniMapImage == null) continue;
            graphics.drawImage((Image)image, miniMapImage.getX(), miniMapImage.getY(), null);
        }
        return null;
    }

    private BufferedImage getImage(AbyssRifts rift) {
        BufferedImage image = this.abyssIcons.get((Object)rift);
        if (image != null) {
            return image;
        }
        image = this.itemManager.getImage(rift.getItemId());
        BufferedImage resizedImage = new BufferedImage(AbyssMinimapOverlay.IMAGE_SIZE.width, AbyssMinimapOverlay.IMAGE_SIZE.height, 2);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, AbyssMinimapOverlay.IMAGE_SIZE.width, AbyssMinimapOverlay.IMAGE_SIZE.height, null);
        g.dispose();
        this.abyssIcons.put(rift, resizedImage);
        return resizedImage;
    }
}

