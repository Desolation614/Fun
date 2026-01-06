/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.GameState
 *  net.runelite.api.Player
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.AnimationChanged
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameObjectDespawned
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ScriptPreFired
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.mining;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.mining.MiningAnimation;
import net.runelite.client.plugins.mining.MiningConfig;
import net.runelite.client.plugins.mining.MiningOverlay;
import net.runelite.client.plugins.mining.MiningRocksOverlay;
import net.runelite.client.plugins.mining.MiningSession;
import net.runelite.client.plugins.mining.Rock;
import net.runelite.client.plugins.mining.RockRespawn;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.RSTimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Mining", description="Show mining statistics and ore respawn timers", tags={"overlay", "skilling", "timers"}, enabledByDefault=false)
@PluginDependency(value=XpTrackerPlugin.class)
public class MiningPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(MiningPlugin.class);
    private static final Pattern MINING_PATTERN = Pattern.compile("You swing your pick at the (?:rock|star)(?:\\.|!)");
    private static final int DAEYALT_ESSENCE_MINE_REGION = 14744;
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MiningOverlay overlay;
    @Inject
    private MiningRocksOverlay rocksOverlay;
    @Inject
    private MiningConfig config;
    @Nullable
    private MiningSession session;
    private final List<RockRespawn> respawns = new ArrayList<RockRespawn>();
    private boolean isMining;
    private Instant lastAnimationChange;
    private int lastActionAnimationId;

    @Provides
    MiningConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MiningConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.rocksOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.session = null;
        this.isMining = false;
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.rocksOverlay);
        this.respawns.forEach(respawn -> this.clearHintArrowAt(respawn.getWorldPoint()));
        this.respawns.clear();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.HOPPING) {
            this.respawns.clear();
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Player local = this.client.getLocalPlayer();
        if (event.getActor() != local) {
            return;
        }
        this.lastAnimationChange = Instant.now();
        int animId = local.getAnimation();
        if (animId != -1) {
            this.lastActionAnimationId = animId;
        }
        if (animId == 7201) {
            if (this.session == null) {
                this.session = new MiningSession();
            }
            this.session.setLastMined();
        } else {
            this.isMining |= MiningAnimation.MINING_ANIMATIONS.contains(animId);
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        this.clearExpiredRespawns();
        if (this.session == null || this.session.getLastMined() == null) {
            return;
        }
        if (this.isMining && MiningAnimation.MINING_ANIMATIONS.contains(this.client.getLocalPlayer().getAnimation())) {
            this.session.setLastMined();
            return;
        }
        Duration statTimeout = Duration.ofMinutes(this.config.statTimeout());
        Duration sinceMined = Duration.between(this.session.getLastMined(), Instant.now());
        if (sinceMined.compareTo(statTimeout) >= 0) {
            this.resetSession();
        }
    }

    private void clearExpiredRespawns() {
        this.respawns.removeIf(rockRespawn -> {
            boolean expired = rockRespawn.isExpired();
            if (expired && rockRespawn.getRock() == Rock.DAEYALT_ESSENCE) {
                this.clearHintArrowAt(rockRespawn.getWorldPoint());
            }
            return expired;
        });
    }

    public void resetSession() {
        this.session = null;
        this.isMining = false;
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject object = event.getGameObject();
        if (object.getId() == 39095) {
            WorldPoint point = object.getWorldLocation();
            this.respawns.removeIf(rockRespawn -> rockRespawn.getWorldPoint().equals((Object)point));
            this.clearHintArrowAt(point);
        }
    }

    private void clearHintArrowAt(WorldPoint worldPoint) {
        if (this.client.getHintArrowType() == 2 && this.client.getHintArrowPoint().equals((Object)worldPoint)) {
            this.client.clearHintArrow();
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject object = event.getGameObject();
        if (object.getId() == 39095 && this.client.getLocalPlayer().getWorldLocation().getRegionID() == 14744) {
            RockRespawn rockRespawn = new RockRespawn(Rock.DAEYALT_ESSENCE, object.getWorldLocation(), Instant.now(), (int)Duration.of(110L, RSTimeUnit.GAME_TICKS).toMillis(), Rock.DAEYALT_ESSENCE.getZOffset());
            this.respawns.add(rockRespawn);
            this.client.setHintArrow(object.getWorldLocation());
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if ((event.getType() == ChatMessageType.SPAM || event.getType() == ChatMessageType.GAMEMESSAGE) && MINING_PATTERN.matcher(event.getMessage()).matches()) {
            if (this.session == null) {
                this.session = new MiningSession();
            }
            this.session.setLastMined();
        }
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired) {
        if (scriptPreFired.getScriptId() == 5474) {
            Object[] args = scriptPreFired.getScriptEvent().getArguments();
            int locCoord = (Integer)args[1];
            int locId = (Integer)args[2];
            int ticks = (Integer)args[5];
            switch (locId) {
                case 26665: 
                case 26666: 
                case 26667: 
                case 26668: {
                    this.addRockRespawn(Rock.MLM_ORE_VEIN, WorldPoint.fromCoord((int)locCoord), ticks);
                    break;
                }
                case 5992: 
                case 51486: 
                case 51488: 
                case 51490: 
                case 51492: {
                    this.addRockRespawn(Rock.ORE_VEIN, WorldPoint.fromCoord((int)locCoord), ticks);
                    break;
                }
                case 11390: 
                case 11391: 
                case 11392: 
                case 30986: 
                case 33253: 
                case 36202: {
                    this.addRockRespawn(Rock.ROCK, WorldPoint.fromCoord((int)locCoord), ticks);
                    break;
                }
                case 11393: {
                    this.addRockRespawn(Rock.AMETHYST, WorldPoint.fromCoord((int)locCoord), ticks);
                    break;
                }
                case 41549: 
                case 41550: {
                    this.addRockRespawn(Rock.BARRONITE, WorldPoint.fromCoord((int)locCoord), ticks);
                }
            }
        }
    }

    private void addRockRespawn(Rock rock, WorldPoint point, int ticks) {
        RockRespawn rockRespawn = new RockRespawn(rock, point, Instant.now(), ticks * 600, rock.getZOffset());
        this.respawns.add(rockRespawn);
        log.debug("Adding respawn for rock: {} coord: {} ticks: {}", new Object[]{rock, point, ticks});
    }

    @Nullable
    public MiningSession getSession() {
        return this.session;
    }

    void setSession(@Nullable MiningSession session) {
        this.session = session;
    }

    List<RockRespawn> getRespawns() {
        return this.respawns;
    }

    public boolean isMining() {
        return this.isMining;
    }

    Instant getLastAnimationChange() {
        return this.lastAnimationChange;
    }

    int getLastActionAnimationId() {
        return this.lastActionAnimationId;
    }
}

