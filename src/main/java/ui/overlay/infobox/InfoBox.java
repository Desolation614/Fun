/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.runelite.client.ui.overlay.infobox;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

public abstract class InfoBox {
    @Nonnull
    private final Plugin plugin;
    private BufferedImage image;
    private BufferedImage scaledImage;
    private InfoBoxPriority priority;
    private String tooltip;
    private List<OverlayMenuEntry> menuEntries = new ArrayList<OverlayMenuEntry>();
    String layer;

    public InfoBox(BufferedImage image, @Nonnull Plugin plugin) {
        this.plugin = plugin;
        this.setImage(image);
        this.setPriority(InfoBoxPriority.NONE);
    }

    public abstract String getText();

    public abstract Color getTextColor();

    public boolean render() {
        return true;
    }

    public boolean cull() {
        return false;
    }

    public String getName() {
        return this.plugin.getClass().getSimpleName() + "_" + this.getClass().getSimpleName();
    }

    @Nonnull
    Plugin getPlugin() {
        return this.plugin;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    BufferedImage getScaledImage() {
        return this.scaledImage;
    }

    void setScaledImage(BufferedImage scaledImage) {
        this.scaledImage = scaledImage;
    }

    InfoBoxPriority getPriority() {
        return this.priority;
    }

    public void setPriority(InfoBoxPriority priority) {
        this.priority = priority;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public List<OverlayMenuEntry> getMenuEntries() {
        return this.menuEntries;
    }

    public void setMenuEntries(List<OverlayMenuEntry> menuEntries) {
        this.menuEntries = menuEntries;
    }
}

