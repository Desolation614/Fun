/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Player
 *  net.runelite.api.Tile
 *  net.runelite.api.TileItem
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ItemDespawned
 *  net.runelite.api.events.ItemSpawned
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.mta.enchantment;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mta.MTAConfig;
import net.runelite.client.plugins.mta.MTARoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnchantmentRoom
extends MTARoom {
    private static final Logger log = LoggerFactory.getLogger(EnchantmentRoom.class);
    private static final int MTA_ENCHANT_REGION = 13462;
    private final Client client;
    private final List<WorldPoint> dragonstones = new ArrayList<WorldPoint>();
    private boolean hintSet;

    @Inject
    private EnchantmentRoom(MTAConfig config, Client client) {
        super(config);
        this.client = client;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOADING) {
            this.dragonstones.clear();
            if (this.hintSet) {
                this.client.clearHintArrow();
                this.hintSet = false;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!this.inside() || !this.config.enchantment()) {
            return;
        }
        WorldPoint nearest = this.findNearestStone();
        if (nearest != null) {
            this.client.setHintArrow(nearest);
            this.hintSet = true;
        } else {
            this.client.clearHintArrow();
            this.hintSet = false;
        }
    }

    private WorldPoint findNearestStone() {
        WorldPoint nearest = null;
        double dist = Double.MAX_VALUE;
        WorldPoint local = this.client.getLocalPlayer().getWorldLocation();
        for (WorldPoint worldPoint : this.dragonstones) {
            double currDist = local.distanceTo(worldPoint);
            if (nearest != null && !(currDist < dist)) continue;
            dist = currDist;
            nearest = worldPoint;
        }
        return nearest;
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned itemSpawned) {
        TileItem item = itemSpawned.getItem();
        Tile tile = itemSpawned.getTile();
        if (item.getId() == 6903) {
            WorldPoint location = tile.getWorldLocation();
            log.debug("Adding dragonstone at {}", (Object)location);
            this.dragonstones.add(location);
        }
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned itemDespawned) {
        TileItem item = itemDespawned.getItem();
        Tile tile = itemDespawned.getTile();
        if (item.getId() == 6903) {
            WorldPoint location = tile.getWorldLocation();
            log.debug("Removed dragonstone at {}", (Object)location);
            this.dragonstones.remove(location);
        }
    }

    @Override
    public boolean inside() {
        Player player = this.client.getLocalPlayer();
        return player != null && player.getWorldLocation().getRegionID() == 13462 && player.getWorldLocation().getPlane() == 0;
    }
}

