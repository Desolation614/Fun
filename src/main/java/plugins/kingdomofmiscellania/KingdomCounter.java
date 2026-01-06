/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.kingdomofmiscellania;

import java.awt.image.BufferedImage;
import net.runelite.client.plugins.kingdomofmiscellania.KingdomPlugin;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.util.QuantityFormatter;

public class KingdomCounter
extends Counter {
    private final KingdomPlugin plugin;

    KingdomCounter(BufferedImage image, KingdomPlugin plugin) {
        super(image, plugin, plugin.getApproval());
        this.plugin = plugin;
    }

    @Override
    public String getText() {
        return KingdomPlugin.getApprovalPercent(this.plugin.getApproval()) + "%";
    }

    @Override
    public String getTooltip() {
        return "Approval: " + this.plugin.getApproval() + "/127</br>Coffer: " + QuantityFormatter.quantityToStackSize(this.plugin.getCoffer());
    }
}

