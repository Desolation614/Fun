/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.GameState
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.blastmine;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.blastmine.BlastMineOreCountOverlay;
import net.runelite.client.plugins.blastmine.BlastMinePluginConfig;
import net.runelite.client.plugins.blastmine.BlastMineRock;
import net.runelite.client.plugins.blastmine.BlastMineRockOverlay;
import net.runelite.client.plugins.blastmine.BlastMineRockType;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Blast Mine", description="Show helpful information for the Blast Mine minigame", tags={"explode", "explosive", "mining", "minigame", "skilling"}, forceDisabled=true)
public class BlastMinePlugin
extends Plugin {
    private final Map<WorldPoint, BlastMineRock> rocks = new HashMap<WorldPoint, BlastMineRock>();
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Client client;
    @Inject
    private BlastMineRockOverlay blastMineRockOverlay;
    @Inject
    private BlastMineOreCountOverlay blastMineOreCountOverlay;

    @Provides
    BlastMinePluginConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BlastMinePluginConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.blastMineRockOverlay);
        this.overlayManager.add(this.blastMineOreCountOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.blastMineRockOverlay);
        this.overlayManager.remove(this.blastMineOreCountOverlay);
        Widget blastMineWidget = this.client.getWidget(39190530);
        if (blastMineWidget != null) {
            blastMineWidget.setHidden(false);
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        BlastMineRockType blastMineRockType = BlastMineRockType.getRockType(gameObject.getId());
        if (blastMineRockType == null) {
            return;
        }
        BlastMineRock newRock = new BlastMineRock(gameObject, blastMineRockType);
        BlastMineRock oldRock = this.rocks.get(gameObject.getWorldLocation());
        if (oldRock == null || oldRock.getType() != newRock.getType()) {
            this.rocks.put(gameObject.getWorldLocation(), newRock);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            this.rocks.clear();
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (this.rocks.isEmpty()) {
            return;
        }
        this.rocks.values().removeIf(rock -> rock.getRemainingTimeRelative() == 1.0 && rock.getType() != BlastMineRockType.NORMAL || rock.getRemainingFuseTimeRelative() == 1.0 && rock.getType() == BlastMineRockType.LIT);
    }

    public Map<WorldPoint, BlastMineRock> getRocks() {
        return this.rocks;
    }
}

