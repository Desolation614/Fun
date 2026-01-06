/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.WorldView
 *  net.runelite.api.events.BeforeRender
 *  net.runelite.api.events.GameStateChanged
 */
package net.runelite.client.plugins.lowmemory;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.WorldView;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.lowmemory.LowMemoryConfig;

@PluginDescriptor(name="Low Detail", description="Turn off ground decorations and certain textures, reducing memory usage", tags={"memory", "usage", "ground", "decorations"}, enabledByDefault=false)
public class LowMemoryPlugin
extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private LowMemoryConfig config;

    @Override
    protected void startUp() {
        this.clientThread.invoke(() -> {
            if (this.client.getGameState().getState() >= GameState.LOGIN_SCREEN.getState()) {
                this.client.changeMemoryMode(this.config.lowDetail());
            }
        });
    }

    @Override
    protected void shutDown() {
        this.clientThread.invoke(() -> this.client.changeMemoryMode(false));
    }

    @Provides
    LowMemoryConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(LowMemoryConfig.class);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.STARTING) {
            this.client.changeMemoryMode(false);
        } else if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
            this.client.changeMemoryMode(this.config.lowDetail());
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("lowmemory")) {
            this.clientThread.invoke(() -> {
                if (this.client.getGameState().getState() >= GameState.LOGIN_SCREEN.getState()) {
                    this.client.changeMemoryMode(this.config.lowDetail());
                }
            });
        }
    }

    @Subscribe
    public void onBeforeRender(BeforeRender beforeRender) {
        WorldView wv = this.client.getTopLevelWorldView();
        if (wv == null) {
            return;
        }
        wv.getScene().setMinLevel(this.config.hideLowerPlanes() ? wv.getPlane() : 0);
    }
}

