/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Strings
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  com.google.inject.Provides
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.DecorativeObject
 *  net.runelite.api.GameObject
 *  net.runelite.api.GameState
 *  net.runelite.api.GroundObject
 *  net.runelite.api.Menu
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.ObjectComposition
 *  net.runelite.api.Scene
 *  net.runelite.api.Tile
 *  net.runelite.api.TileObject
 *  net.runelite.api.WallObject
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.DecorativeObjectDespawned
 *  net.runelite.api.events.DecorativeObjectSpawned
 *  net.runelite.api.events.GameObjectDespawned
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GroundObjectDespawned
 *  net.runelite.api.events.GroundObjectSpawned
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.WallObjectDespawned
 *  net.runelite.api.events.WallObjectSpawned
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.objectindicators;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.objectindicators.ColorTileObject;
import net.runelite.client.plugins.objectindicators.ObjectIndicatorsConfig;
import net.runelite.client.plugins.objectindicators.ObjectIndicatorsOverlay;
import net.runelite.client.plugins.objectindicators.ObjectPoint;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Object Markers", description="Enable marking of objects using the Shift key", tags={"overlay", "objects", "mark", "marker"}, enabledByDefault=false)
public class ObjectIndicatorsPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(ObjectIndicatorsPlugin.class);
    private static final String CONFIG_GROUP = "objectindicators";
    private static final String MARK = "Mark object";
    private static final String UNMARK = "Unmark object";
    private final List<ColorTileObject> objects = new ArrayList<ColorTileObject>();
    private final Map<Integer, Set<ObjectPoint>> points = new HashMap<Integer, Set<ObjectPoint>>();
    @Inject
    private Client client;
    @Inject
    private ConfigManager configManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ObjectIndicatorsOverlay overlay;
    @Inject
    private ObjectIndicatorsConfig config;
    @Inject
    private Gson gson;
    @Inject
    private ColorPickerManager colorPickerManager;
    @Inject
    private ClientThread clientThread;

    @Provides
    ObjectIndicatorsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ObjectIndicatorsConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(this.overlay);
        this.clientThread.invokeLater(this::reloadPoints);
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.points.clear();
        this.objects.clear();
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged e) {
        this.clientThread.invokeLater(this::reloadPoints);
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event) {
        this.checkObjectPoints((TileObject)event.getWallObject());
    }

    @Subscribe
    public void onWallObjectDespawned(WallObjectDespawned event) {
        this.objects.removeIf(o -> o.getTileObject() == event.getWallObject());
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        this.checkObjectPoints((TileObject)event.getGameObject());
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
        this.checkObjectPoints((TileObject)event.getDecorativeObject());
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        this.objects.removeIf(o -> o.getTileObject() == event.getGameObject());
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
        this.objects.removeIf(o -> o.getTileObject() == event.getDecorativeObject());
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        this.checkObjectPoints((TileObject)event.getGroundObject());
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event) {
        this.objects.removeIf(o -> o.getTileObject() == event.getGroundObject());
    }

    private void reloadPoints() {
        this.points.clear();
        if (this.client.getMapRegions() != null) {
            for (int regionId : this.client.getMapRegions()) {
                Set<ObjectPoint> regionPoints = this.loadPoints(regionId);
                if (regionPoints == null) continue;
                this.points.put(regionId, regionPoints);
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        GameState gameState = gameStateChanged.getGameState();
        if (gameState == GameState.LOADING) {
            this.objects.clear();
            this.reloadPoints();
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (event.getType() != MenuAction.EXAMINE_OBJECT.getId() || !this.client.isKeyPressed(81)) {
            return;
        }
        TileObject tileObject = this.findTileObject(this.client.getPlane(), event.getActionParam0(), event.getActionParam1(), event.getIdentifier());
        if (tileObject == null) {
            return;
        }
        int idx = -1;
        Optional<ColorTileObject> marked = this.objects.stream().filter(o -> o.getTileObject() == tileObject).findFirst();
        this.client.createMenuEntry(idx--).setOption(marked.isPresent() ? UNMARK : MARK).setTarget(event.getTarget()).setParam0(event.getActionParam0()).setParam1(event.getActionParam1()).setIdentifier(event.getIdentifier()).setType(MenuAction.RUNELITE).onClick(this::markObject);
        if (marked.isPresent()) {
            idx = this.createTagBorderColorMenu(idx, event.getTarget(), tileObject, marked.get());
            idx = this.createTagFillColorMenu(idx, event.getTarget(), tileObject, marked.get());
            idx = this.createTagStyleMenu(idx, event.getTarget(), tileObject);
        }
    }

    private int createTagBorderColorMenu(int idx, String target, TileObject object, ColorTileObject colorTileObject) {
        List<Color> colors = this.getUsedColors(ObjectPoint::getBorderColor);
        for (Color default_ : new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA}) {
            if (colors.size() >= 5 || colors.contains(default_)) continue;
            colors.add(default_);
        }
        MenuEntry parent = this.client.createMenuEntry(idx--).setOption("Mark border color").setTarget(target).setType(MenuAction.RUNELITE);
        Menu submenu = parent.createSubMenu();
        for (Color c : colors) {
            submenu.createMenuEntry(0).setOption(ColorUtil.prependColorTag("Set color", c)).setType(MenuAction.RUNELITE).onClick(e -> this.updateObjectConfig(object, p -> p.setBorderColor(c)));
        }
        submenu.createMenuEntry(0).setOption("Pick color").setType(MenuAction.RUNELITE).onClick(e -> SwingUtilities.invokeLater(() -> {
            RuneliteColorPicker colorPicker = this.colorPickerManager.create(this.client, (Color)MoreObjects.firstNonNull((Object)colorTileObject.getBorderColor(), (Object)this.config.markerColor()), "Mark Border Color", false);
            colorPicker.setOnClose(c -> this.clientThread.invokeLater(() -> this.updateObjectConfig(object, p -> p.setBorderColor((Color)c))));
            colorPicker.setVisible(true);
        }));
        return idx;
    }

    private int createTagFillColorMenu(int idx, String target, TileObject object, ColorTileObject colorTileObject) {
        List<Color> colors = this.getUsedColors(ObjectPoint::getFillColor);
        for (Color default_ : new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA}) {
            default_ = ColorUtil.colorWithAlpha(default_, default_.getAlpha() / 12);
            if (colors.size() >= 5 || colors.contains(default_)) continue;
            colors.add(default_);
        }
        MenuEntry parent = this.client.createMenuEntry(idx--).setOption("Mark fill color").setTarget(target).setType(MenuAction.RUNELITE);
        Menu submenu = parent.createSubMenu();
        for (Color c : colors) {
            submenu.createMenuEntry(0).setOption(ColorUtil.prependColorTag("Set color", c)).setType(MenuAction.RUNELITE).onClick(e -> this.updateObjectConfig(object, p -> p.setFillColor(c)));
        }
        submenu.createMenuEntry(0).setOption("Pick color").setType(MenuAction.RUNELITE).onClick(e -> SwingUtilities.invokeLater(() -> {
            Color previousColor = (Color)MoreObjects.firstNonNull((Object)colorTileObject.getFillColor(), (Object)new Color(0, 0, 0, 50));
            RuneliteColorPicker colorPicker = this.colorPickerManager.create(this.client, previousColor, "Mark Fill Color", false);
            colorPicker.setOnClose(c -> this.clientThread.invokeLater(() -> this.updateObjectConfig(object, p -> p.setFillColor((Color)c))));
            colorPicker.setVisible(true);
        }));
        submenu.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(e -> this.updateObjectConfig(object, p -> p.setFillColor(null)));
        return idx;
    }

    private int createTagStyleMenu(int idx, String target, TileObject object) {
        MenuEntry parent = this.client.createMenuEntry(idx--).setOption("Mark style").setTarget(target).setType(MenuAction.RUNELITE);
        Menu submenu = parent.createSubMenu();
        submenu.createMenuEntry(0).setOption("Hull").setType(MenuAction.RUNELITE).onClick(e -> this.updateObjectConfig(object, c -> c.setHull(c.getHull() != Boolean.TRUE)));
        submenu.createMenuEntry(0).setOption("Outline").setType(MenuAction.RUNELITE).onClick(e -> this.updateObjectConfig(object, c -> c.setOutline(c.getOutline() != Boolean.TRUE)));
        submenu.createMenuEntry(0).setOption("Clickbox").setType(MenuAction.RUNELITE).onClick(e -> this.updateObjectConfig(object, c -> c.setClickbox(c.getClickbox() != Boolean.TRUE)));
        submenu.createMenuEntry(0).setOption("Tile").setType(MenuAction.RUNELITE).onClick(e -> this.updateObjectConfig(object, c -> c.setTile(c.getTile() != Boolean.TRUE)));
        submenu.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(e -> this.updateObjectConfig(object, c -> {
            c.setHull(null);
            c.setOutline(null);
            c.setClickbox(null);
            c.setTile(null);
        }));
        return idx;
    }

    private void markObject(MenuEntry entry) {
        TileObject object = this.findTileObject(this.client.getPlane(), entry.getParam0(), entry.getParam1(), entry.getIdentifier());
        if (object == null) {
            return;
        }
        ObjectComposition objectDefinition = this.getObjectComposition(object.getId());
        String name = objectDefinition.getName();
        if (Strings.isNullOrEmpty((String)name) || name.equals("null")) {
            return;
        }
        this.markObject(objectDefinition, name, object);
    }

    private void updateObjectConfig(TileObject object, Consumer<ObjectPoint> c) {
        WorldPoint worldPoint = WorldPoint.fromLocalInstance((Client)this.client, (LocalPoint)object.getLocalLocation());
        int regionId = worldPoint.getRegionID();
        Set<ObjectPoint> objectPoints = this.points.get(regionId);
        if (objectPoints.isEmpty()) {
            return;
        }
        ObjectComposition objectComposition = this.getObjectComposition(object.getId());
        ObjectPoint objectPoint = objectPoints.stream().filter(ObjectIndicatorsPlugin.findObjectPredicate(objectComposition, object, worldPoint)).findFirst().orElse(null);
        if (objectPoint == null) {
            return;
        }
        c.accept(objectPoint);
        this.savePoints(regionId, objectPoints);
        if (this.objects.removeIf(o -> o.getTileObject() == object)) {
            this.checkObjectPoints(object);
        }
    }

    private void checkObjectPoints(TileObject object) {
        String name;
        if (object.getPlane() < 0) {
            return;
        }
        WorldPoint worldPoint = WorldPoint.fromLocalInstance((Client)this.client, (LocalPoint)object.getLocalLocation(), (int)object.getPlane());
        Set<ObjectPoint> objectPoints = this.points.get(worldPoint.getRegionID());
        if (objectPoints == null) {
            return;
        }
        ObjectComposition objectComposition = this.client.getObjectDefinition(object.getId());
        if (objectComposition.getImpostorIds() == null && (Strings.isNullOrEmpty((String)(name = objectComposition.getName())) || name.equals("null"))) {
            return;
        }
        for (ObjectPoint objectPoint : objectPoints) {
            if (worldPoint.getRegionX() != objectPoint.getRegionX() || worldPoint.getRegionY() != objectPoint.getRegionY() || worldPoint.getPlane() != objectPoint.getZ() || objectPoint.getId() != object.getId()) continue;
            log.debug("Marking object {} due to matching {}", (Object)object, (Object)objectPoint);
            int flags = (objectPoint.getHull() == Boolean.TRUE ? 1 : 0) | (objectPoint.getOutline() == Boolean.TRUE ? 2 : 0) | (objectPoint.getClickbox() == Boolean.TRUE ? 4 : 0) | (objectPoint.getTile() == Boolean.TRUE ? 8 : 0);
            this.objects.add(new ColorTileObject(object, objectComposition, objectPoint.getName(), objectPoint.getBorderColor(), objectPoint.getFillColor(), (byte)flags));
            break;
        }
    }

    private TileObject findTileObject(int z, int x, int y, int id) {
        Scene scene = this.client.getScene();
        Tile[][][] tiles = scene.getTiles();
        Tile tile = tiles[z][x][y];
        if (tile == null) {
            return null;
        }
        GameObject[] tileGameObjects = tile.getGameObjects();
        DecorativeObject tileDecorativeObject = tile.getDecorativeObject();
        WallObject tileWallObject = tile.getWallObject();
        GroundObject groundObject = tile.getGroundObject();
        if (this.objectIdEquals((TileObject)tileWallObject, id)) {
            return tileWallObject;
        }
        if (this.objectIdEquals((TileObject)tileDecorativeObject, id)) {
            return tileDecorativeObject;
        }
        if (this.objectIdEquals((TileObject)groundObject, id)) {
            return groundObject;
        }
        for (GameObject object : tileGameObjects) {
            if (!this.objectIdEquals((TileObject)object, id)) continue;
            return object;
        }
        return null;
    }

    private boolean objectIdEquals(TileObject tileObject, int id) {
        if (tileObject == null) {
            return false;
        }
        if (tileObject.getId() == id) {
            return true;
        }
        ObjectComposition comp = this.client.getObjectDefinition(tileObject.getId());
        if (comp.getImpostorIds() != null) {
            for (int impostorId : comp.getImpostorIds()) {
                if (impostorId != id) continue;
                return true;
            }
        }
        return false;
    }

    private void markObject(ObjectComposition objectComposition, String name, TileObject object) {
        WorldPoint worldPoint = WorldPoint.fromLocalInstance((Client)this.client, (LocalPoint)object.getLocalLocation());
        int regionId = worldPoint.getRegionID();
        Color borderColor = this.config.markerColor();
        Color fillColor = this.config.fillColor();
        ObjectPoint point = new ObjectPoint(object.getId(), name, regionId, worldPoint.getRegionX(), worldPoint.getRegionY(), worldPoint.getPlane(), borderColor, fillColor, null, null, null, null);
        Set objectPoints = this.points.computeIfAbsent(regionId, k -> new HashSet());
        if (this.objects.removeIf(o -> o.getTileObject() == object)) {
            if (!objectPoints.removeIf(ObjectIndicatorsPlugin.findObjectPredicate(objectComposition, object, worldPoint))) {
                log.warn("unable to find object point for unmarked object {}", (Object)object.getId());
            }
            log.debug("Unmarking object: {}", (Object)point);
        } else {
            objectPoints.add(point);
            this.objects.add(new ColorTileObject(object, this.client.getObjectDefinition(object.getId()), name, borderColor, fillColor, 0));
            log.debug("Marking object: {}", (Object)point);
        }
        this.savePoints(regionId, objectPoints);
    }

    private static Predicate<ObjectPoint> findObjectPredicate(ObjectComposition objectComposition, TileObject object, WorldPoint worldPoint) {
        return op -> (op.getId() == -1 || op.getId() == object.getId() || op.getName().equals(objectComposition.getName())) && op.getRegionX() == worldPoint.getRegionX() && op.getRegionY() == worldPoint.getRegionY() && op.getZ() == worldPoint.getPlane();
    }

    private void savePoints(int id, Set<ObjectPoint> points) {
        if (points.isEmpty()) {
            this.configManager.unsetConfiguration(CONFIG_GROUP, "region_" + id);
        } else {
            String json = this.gson.toJson(points);
            this.configManager.setConfiguration(CONFIG_GROUP, "region_" + id, json);
        }
    }

    private Set<ObjectPoint> loadPoints(int id) {
        String json = this.configManager.getConfiguration(CONFIG_GROUP, "region_" + id);
        if (Strings.isNullOrEmpty((String)json)) {
            return null;
        }
        Set points = (Set)this.gson.fromJson(json, new TypeToken<Set<ObjectPoint>>(){}.getType());
        return points.stream().filter(point -> !point.getName().equals("null")).collect(Collectors.toSet());
    }

    @Nullable
    private ObjectComposition getObjectComposition(int id) {
        ObjectComposition objectComposition = this.client.getObjectDefinition(id);
        return objectComposition.getImpostorIds() == null ? objectComposition : objectComposition.getImpostor();
    }

    private List<Color> getUsedColors(Function<ObjectPoint, Color> getColor) {
        ArrayList<Color> colors = new ArrayList<Color>();
        for (int region : this.client.getMapRegions()) {
            Set<ObjectPoint> points = this.points.get(region);
            if (points == null) continue;
            for (ObjectPoint p : points) {
                Color c = getColor.apply(p);
                if (!(c != null & !colors.contains(c))) continue;
                colors.add(c);
                if (colors.size() < 5) continue;
                return colors;
            }
        }
        return colors;
    }

    List<ColorTileObject> getObjects() {
        return this.objects;
    }
}

