/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  net.runelite.api.widgets.Widget
 *  org.apache.commons.lang3.time.DurationFormatUtils
 */
package net.runelite.client.plugins.blastfurnace;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.blastfurnace.BlastFurnaceConfig;
import net.runelite.client.plugins.blastfurnace.BlastFurnacePlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.time.DurationFormatUtils;

class BlastFurnaceCofferOverlay
extends OverlayPanel {
    private static final float COST_PER_HOUR = 360000.0f;
    private final Client client;
    private final BlastFurnacePlugin plugin;
    private final BlastFurnaceConfig config;

    @Inject
    private BlastFurnaceCofferOverlay(Client client, BlastFurnacePlugin plugin, BlastFurnaceConfig config) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Coffer overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.plugin.getConveyorBelt() == null) {
            return null;
        }
        Widget sack = this.client.getWidget(31064066);
        if (sack != null) {
            int coffer = this.client.getVarbitValue(5357);
            sack.setHidden(true);
            this.panelComponent.getChildren().add(LineComponent.builder().left("Coffer:").right(QuantityFormatter.quantityToStackSize(coffer) + " gp").build());
            if (this.config.showCofferTime()) {
                long millis = (long)((float)coffer / 360000.0f * 60.0f * 60.0f * 1000.0f);
                this.panelComponent.getChildren().add(LineComponent.builder().left("Time:").right(DurationFormatUtils.formatDuration((long)millis, (String)"H'h' m'm' s's'", (boolean)true)).build());
            }
        }
        return super.render(graphics);
    }
}

