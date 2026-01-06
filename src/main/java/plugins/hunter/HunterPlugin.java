/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.Player
 *  net.runelite.api.Tile
 *  net.runelite.api.coords.Angle
 *  net.runelite.api.coords.Direction
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameTick
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.hunter;

import com.google.inject.Provides;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.coords.Angle;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.hunter.HunterConfig;
import net.runelite.client.plugins.hunter.HunterTrap;
import net.runelite.client.plugins.hunter.TrapOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Hunter", description="Show the state of your traps", tags={"overlay", "skilling", "timers"})
public class HunterPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(HunterPlugin.class);
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TrapOverlay overlay;
    @Inject
    private Notifier notifier;
    @Inject
    private HunterConfig config;
    private final Map<WorldPoint, HunterTrap> traps = new HashMap<WorldPoint, HunterTrap>();
    private WorldPoint lastTickLocalPlayerLocation;

    @Provides
    HunterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(HunterConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(this.overlay);
        this.overlay.updateConfig();
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.traps.clear();
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        WorldPoint trapLocation = gameObject.getWorldLocation();
        HunterTrap myTrap = this.traps.get(trapLocation);
        Player localPlayer = this.client.getLocalPlayer();
        switch (gameObject.getId()) {
            case 19217: 
            case 28827: {
                if (localPlayer.getWorldLocation().distanceTo(trapLocation) > 2) break;
                log.debug("Trap placed by \"{}\" on {}", (Object)localPlayer.getName(), (Object)trapLocation);
                this.traps.put(trapLocation, new HunterTrap(gameObject));
                break;
            }
            case 9345: 
            case 9380: 
            case 19223: {
                if (this.lastTickLocalPlayerLocation == null || trapLocation.distanceTo(this.lastTickLocalPlayerLocation) != 0) break;
                log.debug("Trap placed by \"{}\" on {}", (Object)localPlayer.getName(), (Object)localPlayer.getWorldLocation());
                this.traps.put(trapLocation, new HunterTrap(gameObject));
                break;
            }
            case 8731: 
            case 8992: 
            case 9002: 
            case 9343: 
            case 50723: {
                if (this.lastTickLocalPlayerLocation == null || trapLocation.distanceTo(this.lastTickLocalPlayerLocation) != 0) break;
                Direction trapOrientation = new Angle(gameObject.getOrientation()).getNearestDirection();
                WorldPoint translatedTrapLocation = trapLocation;
                switch (trapOrientation) {
                    case SOUTH: {
                        translatedTrapLocation = trapLocation.dy(-1);
                        break;
                    }
                    case WEST: {
                        translatedTrapLocation = trapLocation.dx(-1);
                    }
                }
                log.debug("Trap placed by \"{}\" on {} facing {}", new Object[]{localPlayer.getName(), translatedTrapLocation, trapOrientation});
                this.traps.put(translatedTrapLocation, new HunterTrap(gameObject));
                break;
            }
            case 721: 
            case 8734: 
            case 8986: 
            case 8996: 
            case 9004: 
            case 9348: 
            case 9373: 
            case 9375: 
            case 9377: 
            case 9379: 
            case 9382: 
            case 9383: 
            case 9384: 
            case 19226: 
            case 20648: 
            case 20649: 
            case 20650: 
            case 20651: 
            case 28830: 
            case 28831: 
            case 50717: 
            case 50726: 
            case 50727: {
                if (myTrap == null) break;
                myTrap.setState(HunterTrap.State.FULL);
                myTrap.resetTimer();
                if (myTrap.getObjectId() != 28827) break;
                this.notifier.notify(this.config.maniacalMonkeyNotify(), "You've caught part of a monkey's tail.");
                break;
            }
            case 9344: 
            case 9385: 
            case 19215: 
            case 19224: 
            case 50719: {
                if (myTrap == null) break;
                myTrap.setState(HunterTrap.State.EMPTY);
                myTrap.resetTimer();
                break;
            }
            case 2025: 
            case 2026: 
            case 2028: 
            case 2029: 
            case 8972: 
            case 8974: 
            case 8985: 
            case 8987: 
            case 8993: 
            case 8997: 
            case 9003: 
            case 9005: 
            case 9346: 
            case 9347: 
            case 9349: 
            case 9374: 
            case 9376: 
            case 9378: 
            case 9381: 
            case 9386: 
            case 9387: 
            case 9388: 
            case 9390: 
            case 9391: 
            case 9392: 
            case 9393: 
            case 9394: 
            case 9396: 
            case 9397: 
            case 19218: 
            case 19225: 
            case 19851: 
            case 20128: 
            case 20129: 
            case 20130: 
            case 20131: 
            case 28828: 
            case 28829: 
            case 50716: 
            case 50718: 
            case 50724: 
            case 50725: 
            case 50728: 
            case 50729: 
            case 50730: 
            case 50731: {
                if (myTrap == null) break;
                myTrap.setState(HunterTrap.State.TRANSITION);
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Iterator<Map.Entry<WorldPoint, HunterTrap>> it = this.traps.entrySet().iterator();
        Tile[][][] tiles = this.client.getScene().getTiles();
        Instant expire = Instant.now().minus(HunterTrap.TRAP_TIME.multipliedBy(2L));
        while (it.hasNext()) {
            Map.Entry<WorldPoint, HunterTrap> entry = it.next();
            HunterTrap trap = entry.getValue();
            WorldPoint world = entry.getKey();
            LocalPoint local = LocalPoint.fromWorld((Client)this.client, (WorldPoint)world);
            if (local == null) {
                if (!trap.getPlacedOn().isBefore(expire)) continue;
                log.debug("Trap removed from personal trap collection due to timeout, {} left", (Object)this.traps.size());
                it.remove();
                continue;
            }
            Tile tile = tiles[world.getPlane()][local.getSceneX()][local.getSceneY()];
            GameObject[] objects = tile.getGameObjects();
            boolean containsBoulder = false;
            boolean containsAnything = false;
            boolean containsYoungTree = false;
            for (GameObject object : objects) {
                if (object == null) continue;
                containsAnything = true;
                if (object.getId() == 19215 || object.getId() == 28824) {
                    containsBoulder = true;
                    break;
                }
                if (object.getId() != 8732 && object.getId() != 8990 && object.getId() != 9000 && object.getId() != 9341 && object.getId() != 50721 && object.getId() != 50722) continue;
                containsYoungTree = true;
            }
            if (!containsAnything || containsYoungTree) {
                it.remove();
                log.debug("Trap removed from personal trap collection, {} left", (Object)this.traps.size());
                continue;
            }
            if (!containsBoulder) continue;
            it.remove();
            log.debug("Special trap removed from personal trap collection, {} left", (Object)this.traps.size());
            if (trap.getObjectId() != 28827 || trap.getState().equals((Object)HunterTrap.State.FULL) || trap.getState().equals((Object)HunterTrap.State.OPEN)) continue;
            this.notifier.notify(this.config.maniacalMonkeyNotify(), "The monkey escaped.");
        }
        this.lastTickLocalPlayerLocation = this.client.getLocalPlayer().getWorldLocation();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("hunterplugin")) {
            this.overlay.updateConfig();
        }
    }

    public Map<WorldPoint, HunterTrap> getTraps() {
        return this.traps;
    }
}

