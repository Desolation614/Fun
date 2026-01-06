/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.barrows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.barrows.BarrowsBrothers;
import net.runelite.client.plugins.barrows.BarrowsPlugin;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

class BarrowsBrotherSlainOverlay
extends OverlayPanel {
    private static final DecimalFormat REWARD_POTENTIAL_FORMATTER = new DecimalFormat("##0.00%");
    private final Client client;

    @Inject
    private BarrowsBrotherSlainOverlay(BarrowsPlugin plugin, Client client) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setPriority(0.0f);
        this.client = client;
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Barrows overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget barrowsBrothers = this.client.getWidget(1572868);
        if (barrowsBrothers == null) {
            return null;
        }
        for (BarrowsBrothers brother : BarrowsBrothers.values()) {
            boolean brotherSlain = this.client.getVarbitValue(brother.getKilledVarbit()) > 0;
            String slain = brotherSlain ? "\u2713" : "\u2717";
            this.panelComponent.getChildren().add(LineComponent.builder().left(brother.getName()).right(slain).rightFont(FontManager.getDefaultFont()).rightColor(brotherSlain ? Color.GREEN : Color.RED).build());
        }
        return super.render(graphics);
    }

    private int rewardPotential() {
        int brothers = this.client.getVarbitValue(457) + this.client.getVarbitValue(458) + this.client.getVarbitValue(459) + this.client.getVarbitValue(460) + this.client.getVarbitValue(461) + this.client.getVarbitValue(462);
        return this.client.getVarbitValue(463) + brothers * 2;
    }
}

