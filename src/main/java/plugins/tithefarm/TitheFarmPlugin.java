/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.GameObject
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameTick
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.tithefarm;

import com.google.inject.Provides;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.tithefarm.TitheFarmPlant;
import net.runelite.client.plugins.tithefarm.TitheFarmPlantOverlay;
import net.runelite.client.plugins.tithefarm.TitheFarmPlantState;
import net.runelite.client.plugins.tithefarm.TitheFarmPlantType;
import net.runelite.client.plugins.tithefarm.TitheFarmPluginConfig;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Tithe Farm", description="Show timers for the farming patches within the Tithe Farm minigame", tags={"farming", "minigame", "overlay", "skilling", "timers"}, forceDisabled=true)
public class TitheFarmPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(TitheFarmPlugin.class);
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TitheFarmPlantOverlay titheFarmOverlay;
    private final Set<TitheFarmPlant> plants = new HashSet<TitheFarmPlant>();

    @Provides
    TitheFarmPluginConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(TitheFarmPluginConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.titheFarmOverlay);
        this.titheFarmOverlay.updateConfig();
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.titheFarmOverlay);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("tithefarmplugin")) {
            this.titheFarmOverlay.updateConfig();
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        this.plants.removeIf(plant -> plant.getPlantTimeRelative() == 1.0);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        TitheFarmPlantType type = TitheFarmPlantType.getPlantType(gameObject.getId());
        if (type == null) {
            return;
        }
        TitheFarmPlantState state = TitheFarmPlantState.getState(gameObject.getId());
        TitheFarmPlant newPlant = new TitheFarmPlant(state, type, gameObject);
        TitheFarmPlant oldPlant = this.getPlantFromCollection(gameObject);
        if (oldPlant == null && newPlant.getType() != TitheFarmPlantType.EMPTY) {
            log.debug("Added plant {}", (Object)newPlant);
            this.plants.add(newPlant);
        } else {
            if (oldPlant == null) {
                return;
            }
            if (newPlant.getType() == TitheFarmPlantType.EMPTY) {
                log.debug("Removed plant {}", (Object)oldPlant);
                this.plants.remove(oldPlant);
            } else if (oldPlant.getGameObject().getId() != newPlant.getGameObject().getId()) {
                if (oldPlant.getState() != TitheFarmPlantState.WATERED && newPlant.getState() == TitheFarmPlantState.WATERED) {
                    log.debug("Updated plant (watered)");
                    newPlant.setPlanted(oldPlant.getPlanted());
                    this.plants.remove(oldPlant);
                    this.plants.add(newPlant);
                } else {
                    log.debug("Updated plant");
                    this.plants.remove(oldPlant);
                    this.plants.add(newPlant);
                }
            }
        }
    }

    private TitheFarmPlant getPlantFromCollection(GameObject gameObject) {
        WorldPoint gameObjectLocation = gameObject.getWorldLocation();
        for (TitheFarmPlant plant : this.plants) {
            if (!gameObjectLocation.equals((Object)plant.getWorldLocation())) continue;
            return plant;
        }
        return null;
    }

    public Set<TitheFarmPlant> getPlants() {
        return this.plants;
    }
}

