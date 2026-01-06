/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.tileindicators;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.tileindicators.TileIndicatorsConfig;
import net.runelite.client.plugins.tileindicators.TileIndicatorsOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Tile Indicators", description="Highlight the tile you are currently moving to", tags={"highlight", "overlay"}, enabledByDefault=false)
public class TileIndicatorsPlugin
extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TileIndicatorsOverlay overlay;

    @Provides
    TileIndicatorsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TileIndicatorsConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
    }
}

