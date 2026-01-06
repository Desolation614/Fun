/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.util.concurrent.Runnables
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Menu
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.Tile
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.MenuEntryAdded
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.groundmarkers;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.Runnables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.groundmarkers.ColorTileMarker;
import net.runelite.client.plugins.groundmarkers.GroundMarkerConfig;
import net.runelite.client.plugins.groundmarkers.GroundMarkerMinimapOverlay;
import net.runelite.client.plugins.groundmarkers.GroundMarkerOverlay;
import net.runelite.client.plugins.groundmarkers.GroundMarkerPoint;
import net.runelite.client.plugins.groundmarkers.GroundMarkerSharingManager;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Ground Markers", description="Enable marking of tiles using the Shift key", tags={"overlay", "tiles"})
public class GroundMarkerPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(GroundMarkerPlugin.class);
    private static final String CONFIG_GROUP = "groundMarker";
    private static final String WALK_HERE = "Walk here";
    private static final String REGION_PREFIX = "region_";
    private final List<ColorTileMarker> points = new ArrayList<ColorTileMarker>();
    @Inject
    private Client client;
    @Inject
    private GroundMarkerConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private GroundMarkerOverlay overlay;
    @Inject
    private GroundMarkerMinimapOverlay minimapOverlay;
    @Inject
    private ChatboxPanelManager chatboxPanelManager;
    @Inject
    private EventBus eventBus;
    @Inject
    private GroundMarkerSharingManager sharingManager;
    @Inject
    private Gson gson;
    @Inject
    private ColorPickerManager colorPickerManager;

    void savePoints(int regionId, Collection<GroundMarkerPoint> points) {
        if (points == null || points.isEmpty()) {
            this.configManager.unsetConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);
            return;
        }
        String json = this.gson.toJson(points);
        this.configManager.setConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId, json);
    }

    Collection<GroundMarkerPoint> getPoints(int regionId) {
        String json = this.configManager.getConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);
        if (Strings.isNullOrEmpty((String)json)) {
            return Collections.emptyList();
        }
        return (Collection)this.gson.fromJson(json, new TypeToken<List<GroundMarkerPoint>>(){}.getType());
    }

    @Provides
    GroundMarkerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GroundMarkerConfig.class);
    }

    void loadPoints() {
        this.points.clear();
        int[] regions = this.client.getMapRegions();
        if (regions == null) {
            return;
        }
        for (int regionId : regions) {
            log.debug("Loading points for region {}", (Object)regionId);
            Collection<GroundMarkerPoint> regionPoints = this.getPoints(regionId);
            Collection<ColorTileMarker> colorTileMarkers = this.translateToColorTileMarker(regionPoints);
            this.points.addAll(colorTileMarkers);
        }
    }

    private Collection<ColorTileMarker> translateToColorTileMarker(Collection<GroundMarkerPoint> points) {
        if (points.isEmpty()) {
            return Collections.emptyList();
        }
        return points.stream().map(point -> new ColorTileMarker(WorldPoint.fromRegion((int)point.getRegionId(), (int)point.getRegionX(), (int)point.getRegionY(), (int)point.getZ()), point.getColor(), point.getLabel())).flatMap(colorTile -> {
            Collection localWorldPoints = WorldPoint.toLocalInstance((Client)this.client, (WorldPoint)colorTile.getWorldPoint());
            return localWorldPoints.stream().map(wp -> new ColorTileMarker((WorldPoint)wp, colorTile.getColor(), colorTile.getLabel()));
        }).collect(Collectors.toList());
    }

    @Override
    public void startUp() {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.minimapOverlay);
        if (this.config.showImportExport()) {
            this.sharingManager.addImportExportMenuOptions();
            this.sharingManager.addClearMenuOption();
        }
        this.loadPoints();
        this.eventBus.register(this.sharingManager);
    }

    @Override
    public void shutDown() {
        this.eventBus.unregister(this.sharingManager);
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.minimapOverlay);
        this.sharingManager.removeMenuOptions();
        this.points.clear();
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        this.loadPoints();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        this.loadPoints();
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        boolean hotKeyPressed = this.client.isKeyPressed(81);
        if (hotKeyPressed && event.getOption().equals(WALK_HERE)) {
            Tile selectedSceneTile = this.client.getSelectedSceneTile();
            if (selectedSceneTile == null) {
                return;
            }
            WorldPoint worldPoint = WorldPoint.fromLocalInstance((Client)this.client, (LocalPoint)selectedSceneTile.getLocalLocation());
            int regionId = worldPoint.getRegionID();
            Collection<GroundMarkerPoint> regionPoints = this.getPoints(regionId);
            Optional<GroundMarkerPoint> existingOpt = regionPoints.stream().filter(p -> p.getRegionX() == worldPoint.getRegionX() && p.getRegionY() == worldPoint.getRegionY() && p.getZ() == worldPoint.getPlane()).findFirst();
            this.client.createMenuEntry(-1).setOption(existingOpt.isPresent() ? "Unmark" : "Mark").setTarget("Tile").setType(MenuAction.RUNELITE).onClick(e -> {
                Tile target = this.client.getSelectedSceneTile();
                if (target != null) {
                    this.markTile(target.getLocalLocation());
                }
            });
            if (existingOpt.isPresent()) {
                GroundMarkerPoint existing = existingOpt.get();
                this.client.createMenuEntry(-2).setOption("Label").setTarget("Tile").setType(MenuAction.RUNELITE).onClick(e -> this.labelTile(existing));
                MenuEntry menuColor = this.client.createMenuEntry(-3).setOption("Color").setTarget("Tile").setType(MenuAction.RUNELITE);
                Menu submenu = menuColor.createSubMenu();
                if (regionPoints.size() > 1) {
                    submenu.createMenuEntry(-1).setOption("Reset all").setType(MenuAction.RUNELITE).onClick(e -> this.chatboxPanelManager.openTextMenuInput("Are you sure you want to reset the color of " + regionPoints.size() + " tiles?").option("Yes", () -> {
                        List<GroundMarkerPoint> newPoints = regionPoints.stream().map(p -> new GroundMarkerPoint(p.getRegionId(), p.getRegionX(), p.getRegionY(), p.getZ(), this.config.markerColor(), p.getLabel())).collect(Collectors.toList());
                        this.savePoints(regionId, newPoints);
                        this.loadPoints();
                    }).option("No", Runnables.doNothing()).build());
                }
                submenu.createMenuEntry(-1).setOption("Pick").setType(MenuAction.RUNELITE).onClick(e -> {
                    Color color = existing.getColor();
                    SwingUtilities.invokeLater(() -> {
                        RuneliteColorPicker colorPicker = this.colorPickerManager.create(this.client, color, "Tile marker color", false);
                        colorPicker.setOnClose(c -> this.colorTile(existing, (Color)c));
                        colorPicker.setVisible(true);
                    });
                });
                List existingColors = this.points.stream().map(ColorTileMarker::getColor).distinct().collect(Collectors.toList());
                for (Color color : existingColors) {
                    if (color.equals(existing.getColor())) continue;
                    submenu.createMenuEntry(-1).setOption(ColorUtil.prependColorTag("Color", color)).setType(MenuAction.RUNELITE).onClick(e -> this.colorTile(existing, color));
                }
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals(CONFIG_GROUP) && event.getKey().equals("showImportExport")) {
            this.sharingManager.removeMenuOptions();
            if (this.config.showImportExport()) {
                this.sharingManager.addImportExportMenuOptions();
                this.sharingManager.addClearMenuOption();
            }
        }
    }

    private void markTile(LocalPoint localPoint) {
        if (localPoint == null) {
            return;
        }
        WorldPoint worldPoint = WorldPoint.fromLocalInstance((Client)this.client, (LocalPoint)localPoint);
        int regionId = worldPoint.getRegionID();
        GroundMarkerPoint point = new GroundMarkerPoint(regionId, worldPoint.getRegionX(), worldPoint.getRegionY(), worldPoint.getPlane(), this.config.markerColor(), null);
        log.debug("Updating point: {} - {}", (Object)point, (Object)worldPoint);
        ArrayList<GroundMarkerPoint> groundMarkerPoints = new ArrayList<GroundMarkerPoint>(this.getPoints(regionId));
        if (groundMarkerPoints.contains(point)) {
            groundMarkerPoints.remove(point);
        } else {
            groundMarkerPoints.add(point);
        }
        this.savePoints(regionId, groundMarkerPoints);
        this.loadPoints();
    }

    private void labelTile(GroundMarkerPoint existing) {
        this.chatboxPanelManager.openTextInput("Tile label").value(Optional.ofNullable(existing.getLabel()).orElse("")).onDone(input -> {
            input = Strings.emptyToNull((String)input);
            GroundMarkerPoint newPoint = new GroundMarkerPoint(existing.getRegionId(), existing.getRegionX(), existing.getRegionY(), existing.getZ(), existing.getColor(), (String)input);
            ArrayList<GroundMarkerPoint> points = new ArrayList<GroundMarkerPoint>(this.getPoints(existing.getRegionId()));
            points.remove(existing);
            points.add(newPoint);
            this.savePoints(existing.getRegionId(), points);
            this.loadPoints();
        }).build();
    }

    private void colorTile(GroundMarkerPoint existing, Color newColor) {
        GroundMarkerPoint newPoint = new GroundMarkerPoint(existing.getRegionId(), existing.getRegionX(), existing.getRegionY(), existing.getZ(), newColor, existing.getLabel());
        ArrayList<GroundMarkerPoint> points = new ArrayList<GroundMarkerPoint>(this.getPoints(existing.getRegionId()));
        points.remove(newPoint);
        points.add(newPoint);
        this.savePoints(existing.getRegionId(), points);
        this.loadPoints();
    }

    List<ColorTileMarker> getPoints() {
        return this.points;
    }
}

