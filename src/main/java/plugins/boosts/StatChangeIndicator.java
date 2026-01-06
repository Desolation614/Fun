/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.boosts;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.boosts.BoostsConfig;
import net.runelite.client.plugins.boosts.BoostsPlugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

class StatChangeIndicator
extends InfoBox {
    private final boolean up;
    private final BoostsPlugin plugin;
    private final BoostsConfig config;

    StatChangeIndicator(boolean up, BufferedImage image, BoostsPlugin plugin, BoostsConfig config) {
        super(image, plugin);
        this.up = up;
        this.plugin = plugin;
        this.config = config;
        this.setPriority(InfoBoxPriority.MED);
        this.setTooltip(up ? "Next debuff change" : "Next buff change");
    }

    @Override
    public String getText() {
        return String.format("%02d", this.plugin.getChangeTime(this.up ? this.plugin.getChangeUpTicks() : this.plugin.getChangeDownTicks()));
    }

    @Override
    public Color getTextColor() {
        return (this.up ? this.plugin.getChangeUpTicks() : this.plugin.getChangeDownTicks()) < 10 ? Color.RED.brighter() : Color.WHITE;
    }

    @Override
    public boolean render() {
        int time = this.up ? this.plugin.getChangeUpTicks() : this.plugin.getChangeDownTicks();
        return time != -1 && this.config.displayInfoboxes();
    }
}

