/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Player
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.kourendlibrary;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.kourendlibrary.KourendLibraryConfig;
import net.runelite.client.plugins.kourendlibrary.Library;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

class KourendLibraryTutorialOverlay
extends OverlayPanel {
    private final Client client;
    private final KourendLibraryConfig config;
    private final Library library;
    private final LineComponent noDataMessageComponent;
    private final LineComponent incompleteMessageComponent;
    private final LineComponent completeMessageComponent;
    private final LineComponent sidebarMessageComponent;

    @Inject
    private KourendLibraryTutorialOverlay(Client client, KourendLibraryConfig config, Library library) {
        this.client = client;
        this.config = config;
        this.library = library;
        this.panelComponent.setPreferredSize(new Dimension(177, 0));
        this.noDataMessageComponent = LineComponent.builder().left("Click on the white squares to start finding books.").build();
        this.incompleteMessageComponent = LineComponent.builder().left("Some books have been found. Keep checking marked bookcases to find more.").build();
        this.completeMessageComponent = LineComponent.builder().left("All books found.").build();
        this.sidebarMessageComponent = LineComponent.builder().left("Locations are in the sidebar.").build();
        this.setPriority(0.0f);
        this.setPosition(OverlayPosition.TOP_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.config.showTutorialOverlay()) {
            return null;
        }
        Player player = this.client.getLocalPlayer();
        if (player == null) {
            return null;
        }
        WorldPoint playerLoc = player.getWorldLocation();
        if (playerLoc.getRegionID() != 6459) {
            return null;
        }
        switch (this.library.getState()) {
            case NO_DATA: {
                this.panelComponent.getChildren().add(this.noDataMessageComponent);
                break;
            }
            case INCOMPLETE: {
                this.panelComponent.getChildren().add(this.incompleteMessageComponent);
                this.panelComponent.getChildren().add(this.sidebarMessageComponent);
                break;
            }
            case COMPLETE: {
                this.panelComponent.getChildren().add(this.completeMessageComponent);
                this.panelComponent.getChildren().add(this.sidebarMessageComponent);
            }
        }
        return super.render(graphics);
    }
}

