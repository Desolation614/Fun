/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Player
 */
package net.runelite.client.plugins.dpscounter;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.dpscounter.DpsConfig;
import net.runelite.client.plugins.dpscounter.DpsCounterPlugin;
import net.runelite.client.plugins.dpscounter.DpsMember;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.QuantityFormatter;

class DpsOverlay
extends OverlayPanel {
    private static final DecimalFormat DPS_FORMAT = new DecimalFormat("#0.0");
    private static final int PANEL_WIDTH_OFFSET = 10;
    private final DpsCounterPlugin dpsCounterPlugin;
    private final DpsConfig dpsConfig;
    private final PartyService partyService;
    private final Client client;
    private final TooltipManager tooltipManager;

    @Inject
    DpsOverlay(DpsCounterPlugin dpsCounterPlugin, DpsConfig dpsConfig, PartyService partyService, Client client, TooltipManager tooltipManager) {
        super(dpsCounterPlugin);
        this.dpsCounterPlugin = dpsCounterPlugin;
        this.dpsConfig = dpsConfig;
        this.partyService = partyService;
        this.client = client;
        this.tooltipManager = tooltipManager;
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Reset", "DPS counter", e -> dpsCounterPlugin.reset());
        this.setPaused(false);
    }

    @Override
    public void onMouseOver() {
        DpsMember total = this.dpsCounterPlugin.getTotal();
        Duration elapsed = total.elapsed();
        long s = elapsed.getSeconds();
        String format = s >= 3600L ? String.format("%d:%02d:%02d", s / 3600L, s % 3600L / 60L, s % 60L) : String.format("%d:%02d", s / 60L, s % 60L);
        this.tooltipManager.add(new Tooltip("Elapsed time: " + format));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        DpsMember self;
        Player player;
        Map<String, DpsMember> dpsMembers = this.dpsCounterPlugin.getMembers();
        if (dpsMembers.isEmpty()) {
            return null;
        }
        boolean inParty = this.partyService.isInParty();
        boolean showDamage = this.dpsConfig.showDamage();
        DpsMember total = this.dpsCounterPlugin.getTotal();
        boolean paused = total.isPaused();
        String title = (inParty ? "Party " : "") + (showDamage ? "Damage" : "DPS") + (paused ? " (paused)" : "");
        this.panelComponent.getChildren().add(TitleComponent.builder().text(title).build());
        int maxWidth = 129;
        FontMetrics fontMetrics = graphics.getFontMetrics();
        for (DpsMember dpsMember : dpsMembers.values()) {
            String left = dpsMember.getName();
            String right = showDamage ? QuantityFormatter.formatNumber(dpsMember.getDamage()) : DPS_FORMAT.format(dpsMember.getDps());
            maxWidth = Math.max(maxWidth, fontMetrics.stringWidth(left) + fontMetrics.stringWidth(right));
            this.panelComponent.getChildren().add(LineComponent.builder().left(left).right(right).build());
        }
        this.panelComponent.setPreferredSize(new Dimension(maxWidth + 10, 0));
        if (!inParty && (player = this.client.getLocalPlayer()).getName() != null && (self = dpsMembers.get(player.getName())) != null && total.getDamage() > self.getDamage()) {
            this.panelComponent.getChildren().add(LineComponent.builder().left(total.getName()).right(showDamage ? Integer.toString(total.getDamage()) : DPS_FORMAT.format(total.getDps())).build());
        }
        return super.render(graphics);
    }

    void setPaused(boolean paused) {
        this.removeMenuEntry(MenuAction.RUNELITE_OVERLAY, "Pause", "DPS counter");
        this.removeMenuEntry(MenuAction.RUNELITE_OVERLAY, "Unpause", "DPS counter");
        if (paused) {
            this.addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Unpause", "DPS counter", e -> this.dpsCounterPlugin.unpause());
        } else {
            this.addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Pause", "DPS counter", e -> this.dpsCounterPlugin.pause());
        }
    }
}

