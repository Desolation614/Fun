/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.DecorativeObject
 *  net.runelite.api.GameObject
 *  net.runelite.api.GameState
 *  net.runelite.api.Player
 *  net.runelite.api.Skill
 *  net.runelite.api.Tile
 *  net.runelite.api.TileObject
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.events.AnimationChanged
 *  net.runelite.api.events.DecorativeObjectDespawned
 *  net.runelite.api.events.DecorativeObjectSpawned
 *  net.runelite.api.events.GameObjectDespawned
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.poh;

import com.google.common.collect.Sets;
import com.google.inject.Provides;
import java.io.IOException;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.hiscore.HiscoreManager;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.poh.BurnerOverlay;
import net.runelite.client.plugins.poh.IncenseBurner;
import net.runelite.client.plugins.poh.PohConfig;
import net.runelite.client.plugins.poh.PohIcons;
import net.runelite.client.plugins.poh.PohOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Player-owned House", description="Show minimap icons and mark unlit/lit burners", tags={"construction", "poh", "minimap", "overlay"})
public class PohPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(PohPlugin.class);
    static final Set<Integer> BURNER_UNLIT = Sets.newHashSet((Object[])new Integer[]{13208, 13210, 13212});
    static final Set<Integer> BURNER_LIT = Sets.newHashSet((Object[])new Integer[]{13209, 13211, 13213});
    private final Map<TileObject, Tile> pohObjects = new HashMap<TileObject, Tile>();
    private final Map<Tile, IncenseBurner> incenseBurners = new HashMap<Tile, IncenseBurner>();
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PohOverlay overlay;
    @Inject
    private Client client;
    @Inject
    private ScheduledExecutorService executor;
    @Inject
    private HiscoreManager hiscoreManager;
    @Inject
    private BurnerOverlay burnerOverlay;

    @Provides
    PohConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(PohConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.burnerOverlay);
        this.overlay.updateConfig();
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.burnerOverlay);
        this.pohObjects.clear();
        this.incenseBurners.clear();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        this.overlay.updateConfig();
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        if (!BURNER_LIT.contains(gameObject.getId()) && !BURNER_UNLIT.contains(gameObject.getId())) {
            if (PohIcons.getIcon(gameObject.getId()) != null) {
                this.pohObjects.put((TileObject)gameObject, event.getTile());
            }
            return;
        }
        IncenseBurner incenseBurner = this.incenseBurners.computeIfAbsent(event.getTile(), k -> new IncenseBurner());
        incenseBurner.setStart(Instant.now());
        incenseBurner.setLit(BURNER_LIT.contains(gameObject.getId()));
        incenseBurner.setEnd(null);
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject gameObject = event.getGameObject();
        this.pohObjects.remove(gameObject);
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
        DecorativeObject decorativeObject = event.getDecorativeObject();
        if (PohIcons.getIcon(decorativeObject.getId()) != null) {
            this.pohObjects.put((TileObject)decorativeObject, event.getTile());
        }
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
        DecorativeObject decorativeObject = event.getDecorativeObject();
        this.pohObjects.remove(decorativeObject);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            this.pohObjects.clear();
            this.incenseBurners.clear();
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();
        String actorName = actor.getName();
        if (!(actor instanceof Player) || actor.getAnimation() != 3687) {
            return;
        }
        LocalPoint loc = actor.getLocalLocation();
        this.incenseBurners.keySet().stream().min(Comparator.comparingInt(a -> loc.distanceTo(a.getLocalLocation()))).ifPresent(tile -> {
            IncenseBurner incenseBurner = this.incenseBurners.get(tile);
            incenseBurner.reset();
            if (actor == this.client.getLocalPlayer()) {
                int level = this.client.getRealSkillLevel(Skill.FIREMAKING);
                PohPlugin.updateBurner(incenseBurner, level);
            } else if (actorName != null) {
                this.lookupPlayer(actorName, incenseBurner);
            }
        });
    }

    private void lookupPlayer(String playerName, IncenseBurner incenseBurner) {
        this.executor.execute(() -> {
            try {
                HiscoreResult playerStats = this.hiscoreManager.lookup(playerName, HiscoreEndpoint.NORMAL);
                if (playerStats == null) {
                    return;
                }
                net.runelite.client.hiscore.Skill fm = playerStats.getSkill(HiscoreSkill.FIREMAKING);
                int level = fm.getLevel();
                PohPlugin.updateBurner(incenseBurner, Math.max(level, 1));
            }
            catch (IOException e) {
                log.warn("Error fetching Hiscore data " + e.getMessage());
            }
        });
    }

    private static void updateBurner(IncenseBurner incenseBurner, int fmLevel) {
        double tickLengthSeconds = 0.6;
        incenseBurner.setCountdownTimer((double)(200 + fmLevel) * 0.6);
        incenseBurner.setRandomTimer((double)(fmLevel - 1) * 0.6);
        log.debug("Set burner timer for firemaking level {}", (Object)fmLevel);
    }

    Map<TileObject, Tile> getPohObjects() {
        return this.pohObjects;
    }

    Map<Tile, IncenseBurner> getIncenseBurners() {
        return this.incenseBurners;
    }
}

