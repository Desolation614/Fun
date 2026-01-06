/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.itemidentification;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.itemidentification.ItemIdentificationConfig;
import net.runelite.client.plugins.itemidentification.ItemIdentificationOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Item Identification", description="Show identifying text over items with difficult to distinguish sprites", tags={"abbreviations", "labels", "seeds", "herbs", "saplings", "seedlings"}, enabledByDefault=false)
public class ItemIdentificationPlugin
extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ItemIdentificationOverlay overlay;

    @Provides
    ItemIdentificationConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ItemIdentificationConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(this.overlay);
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
    }
}

