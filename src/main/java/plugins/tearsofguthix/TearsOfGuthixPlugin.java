/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.DecorativeObject
 *  net.runelite.api.events.DecorativeObjectDespawned
 *  net.runelite.api.events.DecorativeObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 */
package net.runelite.client.plugins.tearsofguthix;

import com.google.inject.Provides;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.tearsofguthix.TearsOfGuthixConfig;
import net.runelite.client.plugins.tearsofguthix.TearsOfGuthixOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Tears Of Guthix", description="Show timers for the Tears Of Guthix streams", tags={"minigame", "overlay", "skilling", "timers", "tog"})
public class TearsOfGuthixPlugin
extends Plugin {
    private static final int TOG_REGION = 12948;
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TearsOfGuthixOverlay overlay;
    private final Map<DecorativeObject, Instant> streams = new HashMap<DecorativeObject, Instant>();

    @Provides
    TearsOfGuthixConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TearsOfGuthixConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(this.overlay);
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.streams.clear();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case LOADING: 
            case LOGIN_SCREEN: 
            case HOPPING: {
                this.streams.clear();
            }
        }
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
        DecorativeObject object = event.getDecorativeObject();
        if ((object.getId() == 6661 || object.getId() == 6665 || object.getId() == 6662 || object.getId() == 6666) && this.client.getLocalPlayer().getWorldLocation().getRegionID() == 12948) {
            this.streams.put(event.getDecorativeObject(), Instant.now());
        }
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
        if (this.streams.isEmpty()) {
            return;
        }
        DecorativeObject object = event.getDecorativeObject();
        this.streams.remove(object);
    }

    public Map<DecorativeObject, Instant> getStreams() {
        return this.streams;
    }
}

