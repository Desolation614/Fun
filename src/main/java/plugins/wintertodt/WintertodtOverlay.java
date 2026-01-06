/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.MenuAction
 */
package net.runelite.client.plugins.wintertodt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.MenuAction;
import net.runelite.client.plugins.wintertodt.WintertodtActivity;
import net.runelite.client.plugins.wintertodt.WintertodtConfig;
import net.runelite.client.plugins.wintertodt.WintertodtPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class WintertodtOverlay
extends OverlayPanel {
    private final WintertodtPlugin plugin;
    private final WintertodtConfig wintertodtConfig;

    @Inject
    private WintertodtOverlay(WintertodtPlugin plugin, WintertodtConfig wintertodtConfig) {
        super(plugin);
        this.plugin = plugin;
        this.wintertodtConfig = wintertodtConfig;
        this.setPosition(OverlayPosition.BOTTOM_LEFT);
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Wintertodt overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!this.plugin.isInWintertodt() || !this.wintertodtConfig.showOverlay()) {
            return null;
        }
        this.panelComponent.getChildren().add(TitleComponent.builder().text(this.plugin.getCurrentActivity().getActionString()).color(this.plugin.getCurrentActivity() == WintertodtActivity.IDLE ? Color.RED : Color.GREEN).build());
        String inventoryString = this.plugin.getNumLogs() > 0 ? this.plugin.getInventoryScore() + " (" + this.plugin.getTotalPotentialinventoryScore() + ") pts" : this.plugin.getInventoryScore() + " pts";
        this.panelComponent.getChildren().add(LineComponent.builder().left("Inventory:").leftColor(Color.WHITE).right(inventoryString).rightColor(this.plugin.getInventoryScore() > 0 ? Color.GREEN : Color.RED).build());
        String kindlingString = this.plugin.getNumLogs() > 0 ? this.plugin.getNumKindling() + " (" + (this.plugin.getNumLogs() + this.plugin.getNumKindling()) + ")" : Integer.toString(this.plugin.getNumKindling());
        this.panelComponent.getChildren().add(LineComponent.builder().left("Kindling:").leftColor(Color.WHITE).right(kindlingString).rightColor(this.plugin.getNumKindling() + this.plugin.getNumLogs() > 0 ? Color.GREEN : Color.RED).build());
        return super.render(graphics);
    }
}

