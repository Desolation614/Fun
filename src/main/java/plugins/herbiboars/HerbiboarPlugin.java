/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.MenuAction
 *  net.runelite.api.TileObject
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.GameObjectDespawned
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GroundObjectDespawned
 *  net.runelite.api.events.GroundObjectSpawned
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.events.VarbitChanged
 *  org.apache.commons.lang3.ArrayUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.herbiboars;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Provides;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.herbiboars.HerbiboarConfig;
import net.runelite.client.plugins.herbiboars.HerbiboarMinimapOverlay;
import net.runelite.client.plugins.herbiboars.HerbiboarOverlay;
import net.runelite.client.plugins.herbiboars.HerbiboarSearchSpot;
import net.runelite.client.plugins.herbiboars.HerbiboarStart;
import net.runelite.client.plugins.herbiboars.TrailToSpot;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Herbiboar", description="Highlight starting rocks, trails, and the objects to search at the end of each trail", tags={"herblore", "hunter", "skilling", "overlay"}, forceDisabled=true)
public class HerbiboarPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(HerbiboarPlugin.class);
    private static final List<WorldPoint> END_LOCATIONS = ImmutableList.of((Object)new WorldPoint(3693, 3798, 0), (Object)new WorldPoint(3702, 3808, 0), (Object)new WorldPoint(3703, 3826, 0), (Object)new WorldPoint(3710, 3881, 0), (Object)new WorldPoint(3700, 3877, 0), (Object)new WorldPoint(3715, 3840, 0), (Object)new WorldPoint(3751, 3849, 0), (Object)new WorldPoint(3685, 3869, 0), (Object)new WorldPoint(3681, 3863, 0));
    private static final Set<Integer> START_OBJECT_IDS = ImmutableSet.of((Object)30519, (Object)30520, (Object)30521, (Object)30522, (Object)30523);
    private static final List<Integer> HERBIBOAR_REGIONS = ImmutableList.of((Object)14652, (Object)14651, (Object)14908, (Object)14907);
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private HerbiboarOverlay overlay;
    @Inject
    private HerbiboarMinimapOverlay minimapOverlay;
    private final Map<WorldPoint, TileObject> starts = new HashMap<WorldPoint, TileObject>();
    private final Map<WorldPoint, TileObject> trails = new HashMap<WorldPoint, TileObject>();
    private final Map<WorldPoint, TileObject> trailObjects = new HashMap<WorldPoint, TileObject>();
    private final Map<WorldPoint, TileObject> tunnels = new HashMap<WorldPoint, TileObject>();
    private final Set<Integer> shownTrails = new HashSet<Integer>();
    private final List<HerbiboarSearchSpot> currentPath = Lists.newArrayList();
    private boolean inHerbiboarArea;
    private TrailToSpot nextTrail;
    private HerbiboarSearchSpot.Group currentGroup;
    private int finishId;
    private boolean started;
    private WorldPoint startPoint;
    private HerbiboarStart startSpot;

    @Provides
    HerbiboarConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(HerbiboarConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.minimapOverlay);
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.clientThread.invokeLater(() -> {
                this.inHerbiboarArea = this.checkArea();
                this.updateTrailData();
            });
        }
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.minimapOverlay);
        this.resetTrailData();
        this.clearCache();
        this.inHerbiboarArea = false;
    }

    private void updateTrailData() {
        boolean finished;
        if (!this.isInHerbiboarArea()) {
            return;
        }
        boolean pathActive = false;
        boolean wasStarted = this.started;
        for (HerbiboarSearchSpot spot : HerbiboarSearchSpot.values()) {
            for (TrailToSpot trail : spot.getTrails()) {
                int value = this.client.getVarbitValue(trail.getVarbitId());
                if (value == trail.getValue()) {
                    this.currentGroup = spot.getGroup();
                    this.nextTrail = trail;
                    if (this.currentPath.contains((Object)spot)) continue;
                    this.currentPath.add(spot);
                    continue;
                }
                if (value <= 0) continue;
                this.shownTrails.addAll(trail.getFootprintIds());
                pathActive = true;
            }
        }
        this.finishId = this.client.getVarbitValue(5766);
        this.started = this.client.getVarbitValue(5767) > 0 || this.currentGroup != null;
        boolean bl = finished = !pathActive && this.started;
        if (!wasStarted && this.started) {
            this.startSpot = HerbiboarStart.from(this.startPoint);
        }
        if (finished) {
            this.resetTrailData();
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOpt) {
        if (!this.inHerbiboarArea || this.started || MenuAction.GAME_OBJECT_FIRST_OPTION != menuOpt.getMenuAction()) {
            return;
        }
        switch (Text.removeTags(menuOpt.getMenuTarget())) {
            case "Rock": 
            case "Mushroom": 
            case "Driftwood": {
                this.startPoint = WorldPoint.fromScene((Client)this.client, (int)menuOpt.getParam0(), (int)menuOpt.getParam1(), (int)this.client.getPlane());
            }
        }
    }

    private void resetTrailData() {
        log.debug("Reset trail data");
        this.shownTrails.clear();
        this.currentPath.clear();
        this.nextTrail = null;
        this.currentGroup = null;
        this.finishId = 0;
        this.started = false;
        this.startPoint = null;
        this.startSpot = null;
    }

    private void clearCache() {
        this.starts.clear();
        this.trails.clear();
        this.trailObjects.clear();
        this.tunnels.clear();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case HOPPING: 
            case LOGGING_IN: {
                this.resetTrailData();
                break;
            }
            case LOADING: {
                this.clearCache();
                this.inHerbiboarArea = this.checkArea();
                break;
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        this.updateTrailData();
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        this.onTileObject(null, (TileObject)event.getGameObject());
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        this.onTileObject((TileObject)event.getGameObject(), null);
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        this.onTileObject(null, (TileObject)event.getGroundObject());
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event) {
        this.onTileObject((TileObject)event.getGroundObject(), null);
    }

    private void onTileObject(TileObject oldObject, TileObject newObject) {
        if (oldObject != null) {
            WorldPoint oldLocation = oldObject.getWorldLocation();
            this.starts.remove(oldLocation);
            this.trails.remove(oldLocation);
            this.trailObjects.remove(oldLocation);
            this.tunnels.remove(oldLocation);
        }
        if (newObject == null) {
            return;
        }
        if (START_OBJECT_IDS.contains(newObject.getId())) {
            this.starts.put(newObject.getWorldLocation(), newObject);
            return;
        }
        if (HerbiboarSearchSpot.isTrail(newObject.getId())) {
            this.trails.put(newObject.getWorldLocation(), newObject);
            return;
        }
        if (HerbiboarSearchSpot.isSearchSpot(newObject.getWorldLocation())) {
            this.trailObjects.put(newObject.getWorldLocation(), newObject);
            return;
        }
        if (END_LOCATIONS.contains(newObject.getWorldLocation())) {
            this.tunnels.put(newObject.getWorldLocation(), newObject);
        }
    }

    private boolean checkArea() {
        int[] mapRegions = this.client.getMapRegions();
        for (int region : HERBIBOAR_REGIONS) {
            if (!ArrayUtils.contains((int[])mapRegions, (int)region)) continue;
            return true;
        }
        return false;
    }

    List<WorldPoint> getEndLocations() {
        return END_LOCATIONS;
    }

    public Client getClient() {
        return this.client;
    }

    public ClientThread getClientThread() {
        return this.clientThread;
    }

    public OverlayManager getOverlayManager() {
        return this.overlayManager;
    }

    public HerbiboarOverlay getOverlay() {
        return this.overlay;
    }

    public HerbiboarMinimapOverlay getMinimapOverlay() {
        return this.minimapOverlay;
    }

    public Map<WorldPoint, TileObject> getStarts() {
        return this.starts;
    }

    public Map<WorldPoint, TileObject> getTrails() {
        return this.trails;
    }

    public Map<WorldPoint, TileObject> getTrailObjects() {
        return this.trailObjects;
    }

    public Map<WorldPoint, TileObject> getTunnels() {
        return this.tunnels;
    }

    public Set<Integer> getShownTrails() {
        return this.shownTrails;
    }

    public List<HerbiboarSearchSpot> getCurrentPath() {
        return this.currentPath;
    }

    public boolean isInHerbiboarArea() {
        return this.inHerbiboarArea;
    }

    public TrailToSpot getNextTrail() {
        return this.nextTrail;
    }

    public HerbiboarSearchSpot.Group getCurrentGroup() {
        return this.currentGroup;
    }

    public int getFinishId() {
        return this.finishId;
    }

    public boolean isStarted() {
        return this.started;
    }

    public WorldPoint getStartPoint() {
        return this.startPoint;
    }

    public HerbiboarStart getStartSpot() {
        return this.startSpot;
    }
}

