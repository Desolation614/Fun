/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.runepouch;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.runepouch.RunepouchConfig;
import net.runelite.client.plugins.runepouch.RunepouchOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Rune Pouch", description="Show the contents of your rune pouch", tags={"combat", "magic", "overlay"})
public class RunepouchPlugin
extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private RunepouchOverlay overlay;

    @Provides
    RunepouchConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(RunepouchConfig.class);
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

