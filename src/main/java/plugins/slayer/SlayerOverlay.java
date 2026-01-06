/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.inject.Inject
 *  net.runelite.api.widgets.WidgetItem
 */
package net.runelite.client.plugins.slayer;

import com.google.common.collect.ImmutableSet;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.slayer.SlayerConfig;
import net.runelite.client.plugins.slayer.SlayerPlugin;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;

class SlayerOverlay
extends WidgetItemOverlay {
    private static final Set<Integer> SLAYER_JEWELRY = ImmutableSet.copyOf(ItemVariationMapping.getVariations(11866));
    private static final Set<Integer> ALL_SLAYER_ITEMS = Stream.of(ItemVariationMapping.getVariations(11864).stream(), ItemVariationMapping.getVariations(11866).stream(), Stream.of(4155, 21270)).reduce(Stream::concat).orElseGet(Stream::empty).collect(Collectors.toSet());
    private final SlayerConfig config;
    private final SlayerPlugin plugin;

    @Inject
    private SlayerOverlay(SlayerPlugin plugin, SlayerConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.showOnInventory();
        this.showOnEquipment();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        if (!ALL_SLAYER_ITEMS.contains(itemId)) {
            return;
        }
        if (!this.config.showItemOverlay()) {
            return;
        }
        int amount = this.plugin.getAmount();
        if (amount <= 0) {
            return;
        }
        graphics.setFont(FontManager.getRunescapeSmallFont());
        Rectangle bounds = widgetItem.getCanvasBounds();
        TextComponent textComponent = new TextComponent();
        textComponent.setText(String.valueOf(amount));
        textComponent.setPosition(new Point(bounds.x - 1, bounds.y - 1 + (SLAYER_JEWELRY.contains(itemId) ? bounds.height : graphics.getFontMetrics().getHeight())));
        textComponent.render(graphics);
    }
}

