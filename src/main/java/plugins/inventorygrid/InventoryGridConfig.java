/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.inventorygrid;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="inventorygrid")
public interface InventoryGridConfig
extends Config {
    @ConfigItem(keyName="showItem", name="Show item", description="Show a preview of the item in the new slot.", position=6)
    default public boolean showItem() {
        return true;
    }

    @ConfigItem(keyName="showGrid", name="Show grid", description="Show a grid on the inventory while dragging.", position=3)
    default public boolean showGrid() {
        return true;
    }

    @ConfigItem(keyName="showHighlight", name="Highlight background", description="Show a background highlight on the new slot.", position=2)
    default public boolean showHighlight() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName="gridColor", name="Grid color", description="The color of the grid.", position=4)
    default public Color gridColor() {
        return new Color(255, 255, 255, 45);
    }

    @Alpha
    @ConfigItem(keyName="highlightColor", name="Highlight color", description="The color of the new inventory slot highlight.", position=5)
    default public Color highlightColor() {
        return new Color(0, 255, 0, 45);
    }
}

