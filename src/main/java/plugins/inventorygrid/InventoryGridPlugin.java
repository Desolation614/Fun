/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.google.inject.Provides
 */
package net.runelite.client.plugins.inventorygrid;

import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.inventorygrid.InventoryGridConfig;
import net.runelite.client.plugins.inventorygrid.InventoryGridOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Inventory Grid", description="Shows a grid over the inventory and a preview of where items will be dragged", tags={"items", "overlay"}, enabledByDefault=false)
public class InventoryGridPlugin
extends Plugin {
    @Inject
    private InventoryGridOverlay overlay;
    @Inject
    private OverlayManager overlayManager;

    @Override
    public void startUp() {
        this.overlayManager.add(this.overlay);
    }

    @Override
    public void shutDown() {
        this.overlayManager.remove(this.overlay);
    }

    @Provides
    InventoryGridConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(InventoryGridConfig.class);
    }
}

