/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.npcunaggroarea;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.npcunaggroarea.NpcAggroAreaPlugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

class UncalibratedInfobox
extends InfoBox {
    private final NpcAggroAreaPlugin plugin;

    public UncalibratedInfobox(BufferedImage image, NpcAggroAreaPlugin plugin) {
        super(image, plugin);
        this.plugin = plugin;
        this.setTooltip("Unaggressive NPC timers require calibration.</br>Teleport far away or enter a dungeon, then run until this infobox disappears.");
        this.setPriority(InfoBoxPriority.HIGH);
    }

    @Override
    public String getText() {
        return "?";
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    @Override
    public boolean render() {
        return this.plugin.getSafeCenters()[1] == null;
    }
}

