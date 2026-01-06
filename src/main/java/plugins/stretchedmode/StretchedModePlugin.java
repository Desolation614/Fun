/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.events.ResizeableChanged
 */
package net.runelite.client.plugins.stretchedmode;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.ResizeableChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.stretchedmode.StretchedModeConfig;
import net.runelite.client.plugins.stretchedmode.TranslateMouseListener;
import net.runelite.client.plugins.stretchedmode.TranslateMouseWheelListener;

@PluginDescriptor(name="Stretched Mode", description="Stretches the game in fixed and resizable modes.", tags={"resize", "ui", "interface", "stretch", "scaling", "fixed"}, enabledByDefault=false)
public class StretchedModePlugin
extends Plugin {
    @Inject
    private Client client;
    @Inject
    private StretchedModeConfig config;
    @Inject
    private MouseManager mouseManager;
    @Inject
    private TranslateMouseListener mouseListener;
    @Inject
    private TranslateMouseWheelListener mouseWheelListener;

    @Provides
    StretchedModeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(StretchedModeConfig.class);
    }

    @Override
    protected void startUp() {
        this.mouseManager.registerMouseListener(0, this.mouseListener);
        this.mouseManager.registerMouseWheelListener(0, this.mouseWheelListener);
        this.client.setStretchedEnabled(true);
        this.updateConfig();
    }

    @Override
    protected void shutDown() throws Exception {
        this.client.setStretchedEnabled(false);
        this.client.invalidateStretching(true);
        this.mouseManager.unregisterMouseListener(this.mouseListener);
        this.mouseManager.unregisterMouseWheelListener(this.mouseWheelListener);
    }

    @Subscribe
    public void onResizeableChanged(ResizeableChanged event) {
        this.client.invalidateStretching(true);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("stretchedmode")) {
            return;
        }
        this.updateConfig();
    }

    private void updateConfig() {
        this.client.setStretchedIntegerScaling(this.config.integerScaling());
        this.client.setStretchedKeepAspectRatio(this.config.keepAspectRatio());
        this.client.setStretchedFast(this.config.increasedPerformance());
        this.client.setScalingFactor(this.config.scalingFactor());
        this.client.invalidateStretching(true);
    }
}

