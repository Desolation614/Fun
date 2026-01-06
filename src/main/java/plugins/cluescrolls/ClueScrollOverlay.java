/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Item
 *  net.runelite.api.MenuAction
 */
package net.runelite.client.plugins.cluescrolls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.MenuAction;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.item.AnyRequirementCollection;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirements;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;

public class ClueScrollOverlay
extends OverlayPanel {
    private static final ItemRequirement HAS_SPADE = new AnyRequirementCollection("Spade", ItemRequirements.item(952), ItemRequirements.item(27873));
    private static final ItemRequirement HAS_LIGHT = new AnyRequirementCollection("Light Source", ItemRequirements.item(594), ItemRequirements.item(33), ItemRequirements.item(32), ItemRequirements.item(4531), ItemRequirements.item(4534), ItemRequirements.item(4524), ItemRequirements.item(4539), ItemRequirements.item(4550), ItemRequirements.item(4702), ItemRequirements.item(9065), ItemRequirements.item(5013), ItemRequirements.item(9804), ItemRequirements.item(9805), ItemRequirements.item(13137), ItemRequirements.item(13138), ItemRequirements.item(13139), ItemRequirements.item(13140), ItemRequirements.item(20720), ItemRequirements.item(29777), ItemRequirements.item(13280), ItemRequirements.item(13342), ItemRequirements.item(26824), ItemRequirements.item(26826), ItemRequirements.item(26828), ItemRequirements.item(26830), ItemRequirements.item(26832), ItemRequirements.item(26834), ItemRequirements.item(26836), ItemRequirements.item(26838), ItemRequirements.item(26840), ItemRequirements.item(26842), ItemRequirements.item(26844), ItemRequirements.item(26846), ItemRequirements.item(26848), ItemRequirements.item(30363));
    public static final Color TITLED_CONTENT_COLOR = new Color(190, 190, 190);
    private final ClueScrollPlugin plugin;
    private final Client client;

    @Inject
    private ClueScrollOverlay(ClueScrollPlugin plugin, Client client) {
        super(plugin);
        this.plugin = plugin;
        this.client = client;
        this.setPriority(0.0f);
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Clue Scroll overlay");
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Reset", "Clue Scroll overlay", e -> plugin.resetClue(true));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        ClueScroll clue = this.plugin.getClue();
        if (clue == null) {
            return null;
        }
        this.panelComponent.setPreferredSize(new Dimension(129, 0));
        clue.makeOverlayHint(this.panelComponent, this.plugin);
        Item[] inventoryItems = this.plugin.getInventoryItems();
        Item[] equippedItems = this.plugin.getEquippedItems();
        if (clue.isRequiresSpade() && inventoryItems != null && !HAS_SPADE.fulfilledBy(inventoryItems)) {
            this.panelComponent.getChildren().add(LineComponent.builder().left("").build());
            this.panelComponent.getChildren().add(LineComponent.builder().left("Requires Spade!").leftColor(Color.RED).build());
        }
        if (!(!clue.isRequiresLight() || clue.getFirePitVarbitId() != -1 && this.client.getVarbitValue(clue.getFirePitVarbitId()) == 1 || inventoryItems != null && HAS_LIGHT.fulfilledBy(inventoryItems) || equippedItems != null && HAS_LIGHT.fulfilledBy(equippedItems))) {
            this.panelComponent.getChildren().add(LineComponent.builder().left("").build());
            this.panelComponent.getChildren().add(LineComponent.builder().left("Requires Light Source!").leftColor(Color.RED).build());
        }
        if (clue.getEnemy() != null) {
            this.panelComponent.getChildren().add(LineComponent.builder().left("").build());
            this.panelComponent.getChildren().add(LineComponent.builder().left(clue.getEnemy().getText()).leftColor(Color.YELLOW).build());
        }
        return super.render(graphics);
    }
}

