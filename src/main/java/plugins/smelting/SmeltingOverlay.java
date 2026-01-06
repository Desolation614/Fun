/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.smelting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.client.plugins.smelting.SmeltingPlugin;
import net.runelite.client.plugins.smelting.SmeltingSession;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class SmeltingOverlay
extends OverlayPanel {
    private static final int SMELT_TIMEOUT = 7;
    private static final String SMELTING_RESET = "Reset";
    private final Client client;
    private final SmeltingPlugin plugin;
    private final XpTrackerService xpTrackerService;

    @Inject
    SmeltingOverlay(Client client, SmeltingPlugin plugin, XpTrackerService xpTrackerService) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.xpTrackerService = xpTrackerService;
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Smelting overlay");
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY, SMELTING_RESET, "Smelting overlay", e -> plugin.setSession(null));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        SmeltingSession session = this.plugin.getSession();
        if (session == null) {
            return null;
        }
        if (this.isSmelting() || Duration.between(session.getLastItemSmelted(), Instant.now()).getSeconds() < 7L) {
            this.panelComponent.getChildren().add(TitleComponent.builder().text("Smelting").color(Color.GREEN).build());
        } else {
            this.panelComponent.getChildren().add(TitleComponent.builder().text("NOT smelting").color(Color.RED).build());
        }
        int actions = this.xpTrackerService.getActions(Skill.SMITHING);
        if (actions > 0) {
            if (this.plugin.getSession().getBarsSmelted() > 0) {
                this.panelComponent.getChildren().add(LineComponent.builder().left("Bars:").right(Integer.toString(session.getBarsSmelted())).build());
            }
            if (this.plugin.getSession().getCannonBallsSmelted() > 0) {
                this.panelComponent.getChildren().add(LineComponent.builder().left("Cannonballs:").right(Integer.toString(session.getCannonBallsSmelted())).build());
            }
            if (actions > 2) {
                this.panelComponent.getChildren().add(LineComponent.builder().left("Actions/hr:").right(Integer.toString(this.xpTrackerService.getActionsHr(Skill.SMITHING))).build());
            }
        }
        return super.render(graphics);
    }

    private boolean isSmelting() {
        switch (this.client.getLocalPlayer().getAnimation()) {
            case 827: 
            case 899: {
                return true;
            }
        }
        return false;
    }
}

