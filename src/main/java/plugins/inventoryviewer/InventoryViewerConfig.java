/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.inventoryviewer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup(value="inventoryViewer")
public interface InventoryViewerConfig
extends Config {
    public static final String GROUP = "inventoryViewer";

    @ConfigItem(keyName="toggleKeybind", name="Toggle overlay", description="Binds a key (combination) to toggle the overlay.", position=0)
    default public Keybind toggleKeybind() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName="hiddenDefault", name="Hidden by default", description="Whether or not the overlay is hidden by default.", position=1)
    default public boolean hiddenDefault() {
        return false;
    }

    @ConfigItem(keyName="hideIfInventoryActive", name="Hidden on inventory tab", description="Whether or not the overlay is hidden when the inventory tab is open.", position=2)
    default public boolean hideIfInventoryActive() {
        return false;
    }
}

