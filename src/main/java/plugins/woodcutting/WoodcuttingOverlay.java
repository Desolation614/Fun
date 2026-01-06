/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 */
package net.runelite.client.plugins.woodcutting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.plugins.woodcutting.WoodcuttingConfig;
import net.runelite.client.plugins.woodcutting.WoodcuttingPlugin;
import net.runelite.client.plugins.woodcutting.WoodcuttingSession;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class WoodcuttingOverlay
extends OverlayPanel {
    private static final String WOODCUTTING_RESET = "Reset";
    private static final int BUFF_BAR_DISPLAYED = 96;
    private final Client client;
    private final WoodcuttingPlugin plugin;
    private final WoodcuttingConfig config;

    @Inject
    private WoodcuttingOverlay(Client client, WoodcuttingPlugin plugin, WoodcuttingConfig config) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Woodcutting overlay");
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY, WOODCUTTING_RESET, "Woodcutting overlay", e -> plugin.resetSession());
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        int bark;
        WoodcuttingSession session = this.plugin.getSession();
        if (session == null || !session.isActive() || !this.config.showWoodcuttingStats()) {
            return null;
        }
        if (WoodcuttingPlugin.WOODCUTTING_ANIMS.contains(this.client.getLocalPlayer().getAnimation()) || this.client.getVarpValue(4007) == 96) {
            this.panelComponent.getChildren().add(TitleComponent.builder().text("Woodcutting").color(Color.GREEN).build());
        } else {
            this.panelComponent.getChildren().add(TitleComponent.builder().text("NOT woodcutting").color(Color.RED).build());
        }
        int logsCut = session.getLogsCut();
        if (logsCut > 0) {
            this.panelComponent.getChildren().add(LineComponent.builder().left("Logs cut:").right(Integer.toString(logsCut)).build());
            int logsPerHr = session.getLogsPerHr();
            if (logsPerHr > 0) {
                this.panelComponent.getChildren().add(LineComponent.builder().left("Logs/hr:").right(Integer.toString(logsPerHr)).build());
            }
        }
        if ((bark = session.getBark()) > 0) {
            this.panelComponent.getChildren().add(LineComponent.builder().left("Bark:").right(Integer.toString(bark)).build());
            int barkPerHr = session.getBarkPerHr();
            if (barkPerHr > 0) {
                this.panelComponent.getChildren().add(LineComponent.builder().left("Bark/hr:").right(Integer.toString(barkPerHr)).build());
            }
        }
        return super.render(graphics);
    }
}

