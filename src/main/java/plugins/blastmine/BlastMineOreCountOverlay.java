/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.blastmine;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.blastmine.BlastMinePlugin;
import net.runelite.client.plugins.blastmine.BlastMinePluginConfig;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;

class BlastMineOreCountOverlay
extends OverlayPanel {
    private final Client client;
    private final BlastMinePluginConfig config;
    private final ItemManager itemManager;

    @Inject
    private BlastMineOreCountOverlay(BlastMinePlugin plugin, Client client, BlastMinePluginConfig config, ItemManager itemManager) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.config = config;
        this.itemManager = itemManager;
        this.panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Blast mine overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget blastMineWidget = this.client.getWidget(39190530);
        if (blastMineWidget == null) {
            return null;
        }
        if (this.config.showOreOverlay()) {
            blastMineWidget.setHidden(true);
            this.panelComponent.getChildren().add(new ImageComponent(this.getImage(453, this.client.getVarbitValue(10698))));
            this.panelComponent.getChildren().add(new ImageComponent(this.getImage(444, this.client.getVarbitValue(10699))));
            this.panelComponent.getChildren().add(new ImageComponent(this.getImage(447, this.client.getVarbitValue(10700))));
            this.panelComponent.getChildren().add(new ImageComponent(this.getImage(449, this.client.getVarbitValue(10701))));
            this.panelComponent.getChildren().add(new ImageComponent(this.getImage(451, this.client.getVarbitValue(10702))));
        } else {
            blastMineWidget.setHidden(false);
        }
        return super.render(graphics);
    }

    private BufferedImage getImage(int itemID, int amount) {
        return this.itemManager.getImage(itemID, amount, true);
    }
}

