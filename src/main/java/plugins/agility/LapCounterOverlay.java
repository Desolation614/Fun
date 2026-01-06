/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.MenuAction
 */
package net.runelite.client.plugins.agility;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.MenuAction;
import net.runelite.client.plugins.agility.AgilityConfig;
import net.runelite.client.plugins.agility.AgilityPlugin;
import net.runelite.client.plugins.agility.AgilitySession;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

class LapCounterOverlay
extends OverlayPanel {
    private static final String AGILITY_RESET = "Reset";
    private final AgilityPlugin plugin;
    private final AgilityConfig config;

    @Inject
    private LapCounterOverlay(AgilityPlugin plugin, AgilityConfig config) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setPriority(0.0f);
        this.plugin = plugin;
        this.config = config;
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Agility overlay");
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY, AGILITY_RESET, "Agility overlay", e -> plugin.setSession(null));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        AgilitySession session = this.plugin.getSession();
        if (!this.config.showLapCount() || session == null || session.getLastLapCompleted() == null || session.getCourse() == null) {
            return null;
        }
        Duration lapTimeout = Duration.ofMinutes(this.config.lapTimeout());
        Duration sinceLap = Duration.between(session.getLastLapCompleted(), Instant.now());
        if (sinceLap.compareTo(lapTimeout) >= 0) {
            session.setLastLapCompleted(null);
            return null;
        }
        this.panelComponent.getChildren().add(LineComponent.builder().left("Total Laps:").right(Integer.toString(session.getTotalLaps())).build());
        if (this.config.lapsToLevel() && session.getLapsTillGoal() > 0) {
            this.panelComponent.getChildren().add(LineComponent.builder().left("Laps until goal:").right(Integer.toString(session.getLapsTillGoal())).build());
        }
        if (this.config.lapsPerHour() && session.getLapsPerHour() > 0) {
            this.panelComponent.getChildren().add(LineComponent.builder().left("Laps per hour:").right(Integer.toString(session.getLapsPerHour())).build());
        }
        return super.render(graphics);
    }
}

