/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ListMultimap
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Actor
 *  net.runelite.api.Client
 *  net.runelite.api.NPC
 *  net.runelite.api.NPCComposition
 *  net.runelite.api.Player
 *  net.runelite.api.ScriptEvent
 *  net.runelite.api.Tile
 *  net.runelite.api.TileItem
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldArea
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.ActorDeath
 *  net.runelite.api.events.AnimationChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.InteractingChanged
 *  net.runelite.api.events.ItemDespawned
 *  net.runelite.api.events.ItemSpawned
 *  net.runelite.api.events.NpcChanged
 *  net.runelite.api.events.NpcDespawned
 *  net.runelite.api.events.PlayerDespawned
 *  net.runelite.api.events.ScriptPreFired
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.game;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Player;
import net.runelite.api.ScriptEvent;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.events.ServerNpcLoot;
import net.runelite.client.game.ItemStack;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LootManager {
    private static final Logger log = LoggerFactory.getLogger(LootManager.class);
    private static final Map<Integer, Integer> NPC_DEATH_ANIMATIONS = ImmutableMap.of((Object)492, (Object)3993, (Object)14012, (Object)11679);
    private static final int EDGEVILLE_PVP_REGION_ID = 6025;
    private final EventBus eventBus;
    private final Client client;
    private final NpcUtil npcUtil;
    private final ListMultimap<Integer, TileItem> itemSpawns = ArrayListMultimap.create();
    private final Set<WorldPoint> killPoints = new HashSet<WorldPoint>();
    private WorldPoint playerLocationLastTick;
    private WorldPoint krakenPlayerLocation;
    private NPC delayedLootNpc;
    private int delayedLootTick;
    private List<WorldArea> delayedLootAreas;
    private NPCComposition scriptNpc;
    private int scriptEventId;
    private final List<ItemStack> scriptItems = new ArrayList<ItemStack>(4);
    private Player lastPlayerTarget;
    private Player delayedLootPlayer;
    private WorldPoint delayedLootPlayerLocation;
    private int delayedLootPlayerTickLimit;

    @Inject
    private LootManager(EventBus eventBus, Client client, NpcUtil npcUtil) {
        this.eventBus = eventBus;
        this.client = client;
        this.npcUtil = npcUtil;
        eventBus.register(this);
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        if (npc == this.delayedLootNpc) {
            this.clearDelayedLootNpc();
        }
        if (!this.npcUtil.isDying(npc)) {
            int id = npc.getId();
            switch (id) {
                case 412: 
                case 413: 
                case 421: 
                case 422: 
                case 458: 
                case 459: 
                case 460: 
                case 461: 
                case 462: 
                case 463: 
                case 537: 
                case 1024: 
                case 1543: 
                case 7392: 
                case 7407: 
                case 7408: 
                case 7797: 
                case 7888: 
                case 7889: {
                    break;
                }
                default: {
                    return;
                }
            }
        }
        this.processNpcLoot(npc);
    }

    @Subscribe
    public void onPlayerDespawned(PlayerDespawned playerDespawned) {
        int y;
        Player player = playerDespawned.getPlayer();
        if (player.getHealthRatio() != 0) {
            return;
        }
        WorldPoint worldPoint = player.getWorldLocation();
        LocalPoint location = LocalPoint.fromWorld((Client)this.client, (WorldPoint)worldPoint);
        if (location == null || this.killPoints.contains(worldPoint)) {
            return;
        }
        int x = location.getSceneX();
        int packed = x << 8 | (y = location.getSceneY());
        Collection items = this.itemSpawns.get((Object)packed).stream().map(i -> new ItemStack(i.getId(), i.getQuantity())).collect(Collectors.toList());
        if (items.isEmpty()) {
            return;
        }
        this.killPoints.add(worldPoint);
        this.eventBus.post(new PlayerLootReceived(player, items));
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged interactingChanged) {
        if (interactingChanged.getSource() != this.client.getLocalPlayer()) {
            return;
        }
        Actor target = interactingChanged.getTarget();
        if (target instanceof Player) {
            this.lastPlayerTarget = (Player)target;
        }
    }

    @Subscribe
    public void onActorDeath(ActorDeath actorDeath) {
        Actor actor = actorDeath.getActor();
        if (!(actor instanceof Player)) {
            return;
        }
        Player player = (Player)actor;
        if (player == this.lastPlayerTarget) {
            this.delayedLootPlayer = player;
            this.delayedLootPlayerLocation = player.getWorldLocation();
            this.delayedLootPlayerTickLimit = 6;
        }
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned itemSpawned) {
        TileItem item = itemSpawned.getItem();
        Tile tile = itemSpawned.getTile();
        LocalPoint location = tile.getLocalLocation();
        int packed = location.getSceneX() << 8 | location.getSceneY();
        this.itemSpawns.put((Object)packed, (Object)item);
        log.debug("Item spawn {} ({}) location {}", new Object[]{item.getId(), item.getQuantity(), location});
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned itemDespawned) {
        TileItem item = itemDespawned.getItem();
        LocalPoint location = itemDespawned.getTile().getLocalLocation();
        log.debug("Item despawn {} ({}) location {}", new Object[]{item.getId(), item.getQuantity(), location});
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged e) {
        if (!(e.getActor() instanceof NPC)) {
            return;
        }
        NPC npc = (NPC)e.getActor();
        int id = npc.getId();
        Integer deathAnim = NPC_DEATH_ANIMATIONS.get(id);
        if (deathAnim != null && deathAnim.intValue() == npc.getAnimation()) {
            if (id == 492) {
                this.krakenPlayerLocation = this.client.getLocalPlayer().getWorldLocation();
            } else {
                this.processNpcLoot(npc);
            }
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged) {
        NPC npc = npcChanged.getNpc();
        if (npc.getId() == 9433 || npc.getId() == 9424) {
            this.delayedLootNpc = npc;
            this.delayedLootTick = 10;
        } else if (npc.getId() == 475) {
            this.delayedLootNpc = npc;
            this.delayedLootTick = 1;
            this.delayedLootAreas = this.getDropLocations(npc);
        } else if (npc.getId() == 12192 || npc.getId() == 12196) {
            this.delayedLootNpc = npc;
            this.delayedLootTick = 5;
            this.delayedLootAreas = this.getDropLocations(npc);
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (this.delayedLootNpc != null && --this.delayedLootTick == 0) {
            this.processDelayedLoot();
            this.clearDelayedLootNpc();
        }
        if (this.delayedLootPlayer != null && this.delayedLootPlayerTickLimit-- > 0) {
            this.processDelayedPlayerLoot();
        }
        this.playerLocationLastTick = this.client.getLocalPlayer().getWorldLocation();
        this.itemSpawns.clear();
        this.killPoints.clear();
        this.processScriptLoot();
    }

    @Subscribe
    private void onScriptPreFired(ScriptPreFired event) {
        if (event.getScriptId() == 7192) {
            NPCComposition npcComposition;
            String name;
            ScriptEvent scriptEvent = event.getScriptEvent();
            int npcId = (Integer)scriptEvent.getArguments()[1];
            int eventId = (Integer)scriptEvent.getArguments()[2];
            int itemId = (Integer)scriptEvent.getArguments()[3];
            int qty = (Integer)scriptEvent.getArguments()[4];
            log.debug("loottracker_add_loot npc={} event={} item={} qty={}", new Object[]{npcId, eventId, itemId, qty});
            if (this.scriptEventId != eventId) {
                this.processScriptLoot();
            }
            if (!(name = Text.removeTags((npcComposition = this.client.getNpcDefinition(npcId)).getName())).isEmpty() && !"null".equals(name)) {
                this.scriptNpc = npcComposition;
            }
            this.scriptEventId = eventId;
            this.scriptItems.add(new ItemStack(itemId, qty));
        }
    }

    private void processScriptLoot() {
        if (this.scriptNpc != null && !this.scriptItems.isEmpty()) {
            this.eventBus.post(new ServerNpcLoot(this.scriptNpc, this.scriptItems));
        }
        this.scriptNpc = null;
        this.scriptItems.clear();
    }

    private void processDelayedPlayerLoot() {
        int sceneY;
        if (this.client.getLocalPlayer().getWorldLocation().getRegionID() != 6025) {
            this.delayedLootPlayer = null;
            this.delayedLootPlayerLocation = null;
            this.delayedLootPlayerTickLimit = 0;
            log.debug("Delayed loot information reset due to player not being in edgeville pvp anymore.");
            return;
        }
        LocalPoint localPoint = LocalPoint.fromWorld((Client)this.client, (WorldPoint)this.delayedLootPlayerLocation);
        if (localPoint == null) {
            this.delayedLootPlayer = null;
            this.delayedLootPlayerLocation = null;
            this.delayedLootPlayerTickLimit = 0;
            log.debug("Scene changed away from delayed player loot location");
            return;
        }
        int sceneX = localPoint.getSceneX();
        int packed = sceneX << 8 | (sceneY = localPoint.getSceneY());
        List<ItemStack> itemStacks = this.itemSpawns.get((Object)packed).stream().map(i -> new ItemStack(i.getId(), i.getQuantity())).collect(Collectors.toList());
        if (itemStacks.isEmpty()) {
            return;
        }
        log.debug("Got delayed player loot stack from {}: {}", (Object)this.delayedLootPlayer.getName(), itemStacks);
        this.eventBus.post(new PlayerLootReceived(this.delayedLootPlayer, itemStacks));
    }

    private void processDelayedLoot() {
        List<ItemStack> itemStacks;
        if (this.delayedLootAreas == null) {
            this.delayedLootAreas = List.of(this.getAdjacentSquareLootTile(this.delayedLootNpc).toWorldArea());
        }
        if (!(itemStacks = this.getItemStacksFromAreas(this.delayedLootAreas)).isEmpty()) {
            log.debug("Got delayed loot stack from {}: {}", (Object)this.delayedLootNpc.getName(), itemStacks);
            this.eventBus.post(new NpcLootReceived(this.delayedLootNpc, itemStacks));
        } else {
            log.debug("Delayed loot expired with no loot");
        }
    }

    private void processNpcLoot(NPC npc) {
        List<ItemStack> allItems = this.getItemStacksFromAreas(this.getDropLocations(npc));
        if (!allItems.isEmpty()) {
            this.eventBus.post(new NpcLootReceived(npc, allItems));
        }
    }

    private List<ItemStack> getItemStacksFromAreas(List<WorldArea> areas) {
        ArrayList<ItemStack> allItems = new ArrayList<ItemStack>();
        for (WorldArea dropLocation : areas) {
            WorldPoint worldPoint = dropLocation.toWorldPoint();
            LocalPoint location = LocalPoint.fromWorld((Client)this.client, (WorldPoint)worldPoint);
            if (location == null) continue;
            int x = location.getSceneX();
            int y = location.getSceneY();
            for (int i = 0; i < dropLocation.getWidth(); ++i) {
                for (int j = 0; j < dropLocation.getHeight(); ++j) {
                    WorldPoint dropPoint = new WorldPoint(worldPoint.getX() + i, worldPoint.getY() + j, worldPoint.getPlane());
                    if (!this.killPoints.add(dropPoint)) continue;
                    int packed = x + i << 8 | y + j;
                    List items = this.itemSpawns.get((Object)packed);
                    items.forEach(item -> allItems.add(new ItemStack(item.getId(), item.getQuantity())));
                }
            }
        }
        return allItems;
    }

    private List<WorldArea> getDropLocations(NPC npc) {
        switch (npc.getId()) {
            case 494: 
            case 6640: 
            case 6656: {
                return Collections.singletonList(this.playerLocationLastTick.toWorldArea());
            }
            case 492: {
                return Collections.singletonList(this.krakenPlayerLocation.toWorldArea());
            }
            case 2042: 
            case 2043: 
            case 2044: {
                for (Map.Entry entry : this.itemSpawns.entries()) {
                    if (((TileItem)entry.getValue()).getId() != 12934) continue;
                    int packed = (Integer)entry.getKey();
                    int unpackedX = packed >> 8;
                    int unpackedY = packed & 0xFF;
                    WorldPoint lootPoint = WorldPoint.fromScene((Client)this.client, (int)unpackedX, (int)unpackedY, (int)npc.getWorldLocation().getPlane());
                    return Collections.singletonList(lootPoint.toWorldArea());
                }
                break;
            }
            case 8026: 
            case 8058: 
            case 8059: 
            case 8060: 
            case 8061: {
                WorldPoint bossLocation = npc.getWorldLocation();
                int x = bossLocation.getX() + 3;
                int y = bossLocation.getY() + 3;
                if (this.playerLocationLastTick.getX() < x) {
                    x -= 4;
                } else if (this.playerLocationLastTick.getX() > x) {
                    x += 4;
                }
                if (this.playerLocationLastTick.getY() < y) {
                    y -= 4;
                } else if (this.playerLocationLastTick.getY() > y) {
                    y += 4;
                }
                return Collections.singletonList(new WorldArea(x, y, 1, 1, bossLocation.getPlane()));
            }
            case 11278: 
            case 11279: 
            case 11280: 
            case 11281: 
            case 11282: {
                int y;
                int x;
                int packed;
                LocalPoint localPoint = LocalPoint.fromWorld((Client)this.client, (WorldPoint)this.playerLocationLastTick);
                if (localPoint == null || !this.itemSpawns.containsKey((Object)(packed = (x = localPoint.getSceneX()) << 8 | (y = localPoint.getSceneY())))) break;
                return Collections.singletonList(this.playerLocationLastTick.toWorldArea());
            }
            case 6503: 
            case 6504: 
            case 6609: 
            case 6610: 
            case 6612: 
            case 11992: 
            case 11994: 
            case 11998: {
                return ImmutableList.of((Object)npc.getWorldArea(), (Object)this.playerLocationLastTick.toWorldArea());
            }
            case 12192: 
            case 12196: {
                WorldPoint bossLocation = npc.getWorldLocation();
                int x = bossLocation.getX() + npc.getComposition().getSize() / 2;
                int y = bossLocation.getY() - 1;
                return List.of(new WorldPoint(x, y, bossLocation.getPlane()).toWorldArea());
            }
            case 12223: 
            case 12224: {
                WorldArea bossArea = npc.getWorldArea();
                return List.of(new WorldArea(bossArea.getX() - 2, bossArea.getY() - 2, bossArea.getWidth() + 4, bossArea.getHeight() + 4, bossArea.getPlane()));
            }
            case 12214: 
            case 12215: {
                WorldArea bossArea = npc.getWorldArea();
                int expand = 8;
                WorldArea expandedArea = new WorldArea(bossArea.getX() - 8, bossArea.getY() - 8, bossArea.getWidth() + 16, bossArea.getHeight() + 16, bossArea.getPlane());
                return List.of(expandedArea);
            }
            case 475: {
                WorldArea bossArea = npc.getWorldArea();
                return List.of(new WorldArea(bossArea.getX() - 1, bossArea.getY() - 1, 3, 3, bossArea.getPlane()));
            }
            case 14012: {
                WorldArea bossArea = npc.getWorldArea();
                return List.of(new WorldArea(bossArea.getX() - 2, bossArea.getY() - 10, 10, 10, bossArea.getPlane()));
            }
        }
        return Collections.singletonList(npc.getWorldArea());
    }

    private WorldPoint getAdjacentSquareLootTile(NPC npc) {
        NPCComposition composition = npc.getComposition();
        WorldPoint worldLocation = npc.getWorldLocation();
        int x = worldLocation.getX();
        int y = worldLocation.getY();
        x = this.playerLocationLastTick.getX() < x ? --x : (x += Math.min(this.playerLocationLastTick.getX() - x, composition.getSize()));
        y = this.playerLocationLastTick.getY() < y ? --y : (y += Math.min(this.playerLocationLastTick.getY() - y, composition.getSize()));
        return new WorldPoint(x, y, worldLocation.getPlane());
    }

    public Collection<ItemStack> getItemSpawns(WorldPoint worldPoint) {
        LocalPoint localPoint = LocalPoint.fromWorld((Client)this.client, (WorldPoint)worldPoint);
        if (localPoint == null) {
            return Collections.emptyList();
        }
        int sceneX = localPoint.getSceneX();
        int sceneY = localPoint.getSceneY();
        int packed = sceneX << 8 | sceneY;
        List itemStacks = this.itemSpawns.get((Object)packed);
        return itemStacks.stream().map(ti -> new ItemStack(ti.getId(), ti.getQuantity())).collect(Collectors.toList());
    }

    private void clearDelayedLootNpc() {
        this.delayedLootNpc = null;
        this.delayedLootTick = 0;
        this.delayedLootAreas = null;
    }
}

