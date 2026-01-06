/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Scene
 *  net.runelite.api.Tile
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.PreMapLoad
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.roofremoval;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.PreMapLoad;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.roofremoval.RoofRemovalConfig;
import net.runelite.client.plugins.roofremoval.RoofRemovalConfigOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Roof Removal", description="Remove only the needed roofs above your player, hovered tile, or destination", enabledByDefault=false)
public class RoofRemovalPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(RoofRemovalPlugin.class);
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private Gson gson;
    @Inject
    private RoofRemovalConfig config;
    private final Map<Integer, long[]> overrides = new HashMap<Integer, long[]>();
    private final Set<Integer> configOverrideRegions = new HashSet<Integer>();

    @Provides
    RoofRemovalConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(RoofRemovalConfig.class);
    }

    @Override
    public void startUp() throws IOException {
        this.buildConfigOverrides();
        this.loadRoofOverrides();
        this.clientThread.invoke(() -> {
            Scene scene = this.client.getScene();
            if (scene == null) {
                return false;
            }
            scene.setRoofRemovalMode(this.buildRoofRemovalFlags());
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                this.client.setGameState(GameState.LOADING);
            }
            return true;
        });
    }

    @Override
    public void shutDown() {
        this.overrides.clear();
        this.clientThread.invoke(() -> {
            this.client.getScene().setRoofRemovalMode(0);
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                this.client.setGameState(GameState.LOADING);
            }
        });
    }

    @Subscribe
    public void onPreMapLoad(PreMapLoad preMapLoad) {
        this.performRoofRemoval(preMapLoad.getScene());
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        if (!e.getGroup().equals("roofremoval")) {
            return;
        }
        if (e.getKey().startsWith("remove")) {
            this.client.getScene().setRoofRemovalMode(this.buildRoofRemovalFlags());
        } else if (e.getKey().startsWith("override")) {
            this.buildConfigOverrides();
            this.clientThread.invoke(() -> {
                if (this.client.getGameState() == GameState.LOGGED_IN) {
                    this.client.setGameState(GameState.LOADING);
                }
            });
        }
    }

    private int buildRoofRemovalFlags() {
        int roofRemovalMode = 0;
        if (this.config.removePosition()) {
            roofRemovalMode |= 1;
        }
        if (this.config.removeHovered()) {
            roofRemovalMode |= 2;
        }
        if (this.config.removeDestination()) {
            roofRemovalMode |= 4;
        }
        if (this.config.removeBetween()) {
            roofRemovalMode |= 8;
        }
        return roofRemovalMode;
    }

    private void buildConfigOverrides() {
        this.configOverrideRegions.clear();
        for (RoofRemovalConfigOverride configOverride : RoofRemovalConfigOverride.values()) {
            if (!configOverride.getEnabled().test(this.config)) continue;
            this.configOverrideRegions.addAll(configOverride.getRegions());
        }
    }

    private void performRoofRemoval(Scene scene) {
        this.applyRoofOverrides(scene);
        Stopwatch sw = Stopwatch.createStarted();
        scene.buildRoofs();
        log.debug("Roof building duration: {}", (Object)sw.stop());
    }

    private void loadRoofOverrides() throws IOException {
        try (InputStream in = this.getClass().getResourceAsStream("overrides.jsonc");){
            InputStreamReader data = new InputStreamReader(in, StandardCharsets.UTF_8);
            Type type = new TypeToken<Map<Integer, List<FlaggedArea>>>(){}.getType();
            Map parsed = (Map)this.gson.fromJson((Reader)data, type);
            this.overrides.clear();
            for (Map.Entry entry : parsed.entrySet()) {
                for (FlaggedArea fla : (List)entry.getValue()) {
                    for (int z = fla.z1; z <= fla.z2; ++z) {
                        int packedRegion = (Integer)entry.getKey() << 2 | z;
                        long[] regionData = this.overrides.computeIfAbsent(packedRegion, k -> new long[64]);
                        for (int y = fla.ry1; y <= fla.ry2; ++y) {
                            long row = regionData[y];
                            for (int x = fla.rx1; x <= fla.rx2; ++x) {
                                row |= 1L << x;
                            }
                            regionData[y] = row;
                        }
                    }
                }
            }
        }
    }

    private void applyRoofOverrides(Scene scene) {
        Stopwatch sw = Stopwatch.createStarted();
        boolean regionsHaveOverrides = false;
        block0: for (int regionID : scene.getMapRegions()) {
            if (this.configOverrideRegions.contains(regionID)) {
                regionsHaveOverrides = true;
                break;
            }
            for (int z = 0; z < 4; ++z) {
                if (!this.overrides.containsKey(regionID << 2 | z)) continue;
                regionsHaveOverrides = true;
                break block0;
            }
        }
        if (!regionsHaveOverrides) {
            return;
        }
        Tile[][][] tiles = scene.getTiles();
        byte[][][] settings = scene.getExtendedTileSettings();
        int SCENE_OFFSET = 40;
        for (int z = 0; z < 4; ++z) {
            for (int x = 0; x < 104; ++x) {
                for (int y = 0; y < 104; ++y) {
                    Tile tile = tiles[z][x][y];
                    if (tile == null) continue;
                    WorldPoint wp = WorldPoint.fromLocalInstance((Scene)scene, (LocalPoint)tile.getLocalLocation(), (int)tile.getPlane());
                    int regionAndPlane = wp.getRegionID() << 2 | wp.getPlane();
                    if (this.configOverrideRegions.contains(wp.getRegionID())) {
                        byte[] byArray = settings[z][x + 40];
                        int n = y + 40;
                        byArray[n] = (byte)(byArray[n] | 4);
                        continue;
                    }
                    if (!this.overrides.containsKey(regionAndPlane)) continue;
                    int rx = wp.getRegionX();
                    int ry = wp.getRegionY();
                    long[] region = this.overrides.get(regionAndPlane);
                    if ((region[ry] & 1L << rx) == 0L) continue;
                    byte[] byArray = settings[z][x + 40];
                    int n = y + 40;
                    byArray[n] = (byte)(byArray[n] | 4);
                }
            }
        }
        log.debug("Roof override duration: {}", (Object)sw.stop());
    }

    private static class FlaggedArea {
        int rx1;
        int ry1;
        int rx2;
        int ry2;
        int z1;
        int z2;

        private FlaggedArea() {
        }
    }
}

