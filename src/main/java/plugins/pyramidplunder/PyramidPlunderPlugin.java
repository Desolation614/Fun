/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.GameState
 *  net.runelite.api.Tile
 *  net.runelite.api.TileObject
 *  net.runelite.api.WallObject
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.WallObjectSpawned
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.pyramidplunder;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.pyramidplunder.PyramidPlunderConfig;
import net.runelite.client.plugins.pyramidplunder.PyramidPlunderOverlay;
import net.runelite.client.plugins.pyramidplunder.PyramidPlunderTimer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.RSTimeUnit;

@PluginDescriptor(name="Pyramid Plunder", description="Show custom overlay for Pyramid Plunder", tags={"minigame", "thieving", "pp"}, enabledByDefault=false)
public class PyramidPlunderPlugin
extends Plugin {
    private static final Duration PYRAMID_PLUNDER_DURATION = Duration.of(501L, RSTimeUnit.GAME_TICKS);
    private static final int PYRAMID_PLUNDER_REGION = 7749;
    static final Set<Integer> TOMB_DOOR_WALL_IDS = ImmutableSet.of((Object)26618, (Object)26619, (Object)26620, (Object)26621);
    static final int TOMB_DOOR_CLOSED_ID = 20948;
    static final int SPEARTRAP_ID = 21280;
    static final Set<Integer> URN_IDS = ImmutableSet.of((Object)26580, (Object)26600, (Object)26601, (Object)26602, (Object)26603, (Object)26604, (Object[])new Integer[]{26605, 26606, 26607, 26608, 26609, 26610, 26611, 26612, 26613});
    static final Set<Integer> URN_CLOSED_IDS = ImmutableSet.of((Object)21261, (Object)21262, (Object)21263);
    static final int GRAND_GOLD_CHEST_ID = 26616;
    static final int GRAND_GOLD_CHEST_CLOSED_ID = 20946;
    static final int SARCOPHAGUS_ID = 26626;
    static final int SARCOPHAGUS_CLOSED_ID = 21255;
    @Inject
    private Client client;
    @Inject
    private PyramidPlunderConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PyramidPlunderOverlay overlay;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ItemManager itemManager;
    @Inject
    private ClientThread clientThread;
    private final Map<TileObject, Tile> tilesToHighlight = new HashMap<TileObject, Tile>();
    private final List<GameObject> objectsToHighlight = new ArrayList<GameObject>();
    private PyramidPlunderTimer timer;

    @Provides
    PyramidPlunderConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(PyramidPlunderConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.tilesToHighlight.clear();
        this.objectsToHighlight.clear();
        this.overlayManager.remove(this.overlay);
        this.timer = null;
        this.infoBoxManager.removeIf(PyramidPlunderTimer.class::isInstance);
        this.clientThread.invoke(() -> {
            Widget ppWidget = this.client.getWidget(28049410);
            if (ppWidget != null) {
                ppWidget.setHidden(false);
            }
        });
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            this.tilesToHighlight.clear();
            this.objectsToHighlight.clear();
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (this.isInPyramidPlunder()) {
            if (this.timer == null) {
                int ppTimer = this.client.getVarbitValue(2375);
                Duration remaining = PYRAMID_PLUNDER_DURATION.minus(ppTimer, RSTimeUnit.GAME_TICKS);
                this.timer = new PyramidPlunderTimer(remaining, this.itemManager.getImage(9044), this, this.config, this.client);
                this.infoBoxManager.addInfoBox(this.timer);
            }
        } else if (this.timer != null) {
            this.infoBoxManager.removeInfoBox(this.timer);
            this.timer = null;
        }
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event) {
        WallObject object = event.getWallObject();
        if (TOMB_DOOR_WALL_IDS.contains(object.getId())) {
            this.tilesToHighlight.put((TileObject)object, event.getTile());
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject object = event.getGameObject();
        if (21280 == object.getId()) {
            this.tilesToHighlight.put((TileObject)object, event.getTile());
        } else if (URN_IDS.contains(object.getId()) || 26616 == object.getId() || 26626 == object.getId()) {
            this.objectsToHighlight.add(object);
        }
    }

    public boolean isInPyramidPlunder() {
        return this.client.getLocalPlayer() != null && 7749 == this.client.getLocalPlayer().getWorldLocation().getRegionID() && this.client.getVarbitValue(2375) > 0;
    }

    public Map<TileObject, Tile> getTilesToHighlight() {
        return this.tilesToHighlight;
    }

    public List<GameObject> getObjectsToHighlight() {
        return this.objectsToHighlight;
    }
}

