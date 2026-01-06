/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.google.inject.Provides
 *  net.runelite.api.events.FocusChanged
 */
package net.runelite.client.plugins.fps;

import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.events.FocusChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.fps.FpsConfig;
import net.runelite.client.plugins.fps.FpsDrawListener;
import net.runelite.client.plugins.fps.FpsOverlay;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="FPS Control", description="Show current FPS and/or set an FPS limit", tags={"frames", "framerate", "limit", "overlay"}, enabledByDefault=false)
public class FpsPlugin
extends Plugin {
    static final String CONFIG_GROUP_KEY = "fpscontrol";
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private FpsOverlay overlay;
    @Inject
    private FpsDrawListener drawListener;
    @Inject
    private DrawManager drawManager;

    @Provides
    FpsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FpsConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals(CONFIG_GROUP_KEY)) {
            this.drawListener.reloadConfig();
        }
    }

    @Subscribe
    public void onFocusChanged(FocusChanged event) {
        this.drawListener.onFocusChanged(event);
        this.overlay.onFocusChanged(event);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.drawManager.registerEveryFrameListener(this.drawListener);
        this.drawListener.reloadConfig();
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.drawManager.unregisterEveryFrameListener(this.drawListener);
    }
}

