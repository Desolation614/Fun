/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.cooking;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.client.plugins.cooking.CookingPlugin;
import net.runelite.client.plugins.cooking.CookingSession;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class CookingOverlay
extends OverlayPanel {
    private static final int COOK_TIMEOUT = 3;
    private static final DecimalFormat FORMAT = new DecimalFormat("#.#");
    private static final String COOKING_RESET = "Reset";
    private final Client client;
    private final CookingPlugin plugin;
    private final XpTrackerService xpTrackerService;

    @Inject
    private CookingOverlay(Client client, CookingPlugin plugin, XpTrackerService xpTrackerService) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.xpTrackerService = xpTrackerService;
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Cooking overlay");
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY, COOKING_RESET, "Cooking overlay", e -> plugin.setSession(null));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        CookingSession session = this.plugin.getSession();
        if (session == null) {
            return null;
        }
        if (this.isCooking() || Duration.between(session.getLastCookingAction(), Instant.now()).getSeconds() < 3L) {
            this.panelComponent.getChildren().add(TitleComponent.builder().text("Cooking").color(Color.GREEN).build());
        } else {
            this.panelComponent.getChildren().add(TitleComponent.builder().text("NOT cooking").color(Color.RED).build());
        }
        this.panelComponent.getChildren().add(LineComponent.builder().left("Cooked:").right(session.getCookAmount() + (String)(session.getCookAmount() >= 1 ? " (" + this.xpTrackerService.getActionsHr(Skill.COOKING) + "/hr)" : "")).build());
        this.panelComponent.getChildren().add(LineComponent.builder().left("Burnt:").right(session.getBurnAmount() + (String)(session.getBurnAmount() >= 1 ? " (" + FORMAT.format(session.getBurntPercentage()) + "%)" : "")).build());
        return super.render(graphics);
    }

    private boolean isCooking() {
        switch (this.client.getLocalPlayer().getAnimation()) {
            case 896: 
            case 897: {
                return true;
            }
        }
        return false;
    }
}

