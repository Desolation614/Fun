/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 */
package net.runelite.client.plugins.blastfurnace;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.blastfurnace.BarsOres;
import net.runelite.client.plugins.blastfurnace.BlastFurnacePlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.util.AsyncBufferedImage;

class BlastFurnaceOverlay
extends OverlayPanel {
    private final Client client;
    private final BlastFurnacePlugin plugin;
    @Inject
    private ItemManager itemManager;

    @Inject
    BlastFurnaceOverlay(Client client, BlastFurnacePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.client = client;
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Blast furnace overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.plugin.getConveyorBelt() == null) {
            return null;
        }
        for (BarsOres varbit : BarsOres.values()) {
            int amount = this.client.getVarbitValue(varbit.getVarbit());
            if (amount == 0) continue;
            this.panelComponent.getChildren().add(new ImageComponent(this.getImage(varbit.getItemID(), amount)));
        }
        return super.render(graphics);
    }

    private BufferedImage getImage(int itemID, int amount) {
        AsyncBufferedImage image = this.itemManager.getImage(itemID, amount, true);
        return image;
    }
}

