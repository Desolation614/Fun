/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.DecorativeObject
 *  net.runelite.api.GameObject
 *  net.runelite.api.GameState
 *  net.runelite.api.GroundObject
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.ItemLayer
 *  net.runelite.api.NPC
 *  net.runelite.api.Player
 *  net.runelite.api.Renderable
 *  net.runelite.api.Scene
 *  net.runelite.api.Tile
 *  net.runelite.api.TileItem
 *  net.runelite.api.WallObject
 *  net.runelite.api.events.DecorativeObjectSpawned
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GroundObjectSpawned
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.events.ItemSpawned
 *  net.runelite.api.events.NpcSpawned
 *  net.runelite.api.events.PlayerSpawned
 *  net.runelite.api.events.WallObjectSpawned
 */
package net.runelite.client.util;

import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemLayer;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.WallObject;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;

@Singleton
public class GameEventManager {
    private final EventBus eventBus = new EventBus();
    private final Client client;
    private final ClientThread clientThread;

    @Inject
    private GameEventManager(Client client2, ClientThread clientThread) {
        this.client = client2;
        this.clientThread = clientThread;
    }

    private void forEachTile(Consumer<Tile> consumer) {
        Scene scene = this.client.getScene();
        Tile[][][] tiles = scene.getTiles();
        for (int z = 0; z < 4; ++z) {
            for (int x = 0; x < 104; ++x) {
                for (int y = 0; y < 104; ++y) {
                    Tile tile = tiles[z][x][y];
                    if (tile == null) continue;
                    consumer.accept(tile);
                    if (tile.getBridge() == null) continue;
                    consumer.accept(tile.getBridge());
                }
            }
        }
    }

    public void simulateGameEvents(Object subscriber) {
        if (this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        this.clientThread.invoke(() -> {
            this.eventBus.register(subscriber);
            for (ItemContainer itemContainer : this.client.getItemContainers()) {
                this.eventBus.post(new ItemContainerChanged(itemContainer.getId(), itemContainer));
            }
            for (NPC npc : this.client.getTopLevelWorldView().npcs()) {
                if (npc == null) continue;
                NpcSpawned npcSpawned = new NpcSpawned(npc);
                this.eventBus.post(npcSpawned);
            }
            for (Player player : this.client.getTopLevelWorldView().players()) {
                if (player == null) continue;
                PlayerSpawned playerSpawned = new PlayerSpawned(player);
                this.eventBus.post(playerSpawned);
            }
            this.forEachTile(tile -> {
                GroundObject groundObject;
                DecorativeObject decorativeObject;
                WallObject wallObject = tile.getWallObject();
                if (wallObject != null) {
                    WallObjectSpawned objectSpawned = new WallObjectSpawned();
                    objectSpawned.setTile(tile);
                    objectSpawned.setWallObject(wallObject);
                    this.eventBus.post(objectSpawned);
                }
                if ((decorativeObject = tile.getDecorativeObject()) != null) {
                    DecorativeObjectSpawned objectSpawned = new DecorativeObjectSpawned();
                    objectSpawned.setTile(tile);
                    objectSpawned.setDecorativeObject(decorativeObject);
                    this.eventBus.post(objectSpawned);
                }
                if ((groundObject = tile.getGroundObject()) != null) {
                    GroundObjectSpawned objectSpawned = new GroundObjectSpawned();
                    objectSpawned.setTile(tile);
                    objectSpawned.setGroundObject(groundObject);
                    this.eventBus.post(objectSpawned);
                }
                for (GroundObjectSpawned object : tile.getGameObjects()) {
                    if (object == null || !object.getSceneMinLocation().equals((Object)tile.getSceneLocation())) continue;
                    GameObjectSpawned objectSpawned = new GameObjectSpawned();
                    objectSpawned.setTile(tile);
                    objectSpawned.setGameObject((GameObject)object);
                    this.eventBus.post(objectSpawned);
                }
                ItemLayer itemLayer = tile.getItemLayer();
                if (itemLayer != null) {
                    Renderable current = itemLayer.getTop();
                    while (current instanceof TileItem) {
                        TileItem item = (TileItem)current;
                        current = current.getNext();
                        ItemSpawned itemSpawned = new ItemSpawned(tile, item);
                        this.eventBus.post(itemSpawned);
                    }
                }
            });
            this.eventBus.unregister(subscriber);
        });
    }
}

