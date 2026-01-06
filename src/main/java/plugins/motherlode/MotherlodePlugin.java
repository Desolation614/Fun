/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultiset
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Multiset
 *  com.google.common.collect.Multisets
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.GameState
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.Perspective
 *  net.runelite.api.WallObject
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameObjectDespawned
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.events.ScriptPostFired
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.events.WallObjectDespawned
 *  net.runelite.api.events.WallObjectSpawned
 *  net.runelite.api.widgets.Widget
 *  net.runelite.http.api.loottracker.LootRecordType
 */
package net.runelite.client.plugins.motherlode;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.inject.Provides;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Perspective;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.PluginLootReceived;
import net.runelite.client.plugins.motherlode.MotherlodeConfig;
import net.runelite.client.plugins.motherlode.MotherlodeSceneOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.http.api.loottracker.LootRecordType;

@PluginDescriptor(name="Motherlode Mine", description="Show helpful information inside the Motherlode Mine", tags={"pay", "dirt", "mining", "mlm", "skilling", "overlay"}, enabledByDefault=false)
public class MotherlodePlugin
extends Plugin {
    private static final Set<Integer> MOTHERLODE_MAP_REGIONS = ImmutableSet.of((Object)14679, (Object)14680, (Object)14681, (Object)14935, (Object)14936, (Object)14937, (Object[])new Integer[]{15191, 15192, 15193});
    private static final Set<Integer> MINE_SPOTS = ImmutableSet.of((Object)26661, (Object)26662, (Object)26663, (Object)26664);
    private static final Set<Integer> MLM_ORE_TYPES = ImmutableSet.of((Object)451, (Object)449, (Object)447, (Object)444, (Object)453, (Object)12012, (Object[])new Integer[0]);
    private static final Set<Integer> ROCK_OBSTACLES = ImmutableSet.of((Object)26679, (Object)26680);
    private static final int SACK_LARGE_SIZE = 189;
    private static final int SACK_SIZE = 108;
    private static final int UPPER_FLOOR_HEIGHT = -490;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MotherlodeSceneOverlay sceneOverlay;
    @Inject
    private MotherlodeConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private EventBus eventBus;
    private boolean inMlm;
    private int curSackSize;
    private boolean shouldUpdateOres;
    private Multiset<Integer> inventorySnapshot;
    private final Set<WallObject> veins = new HashSet<WallObject>();
    private final Set<GameObject> rocks = new HashSet<GameObject>();
    private final Set<GameObject> brokenStruts = new HashSet<GameObject>();

    @Provides
    MotherlodeConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MotherlodeConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(this.sceneOverlay);
        this.inMlm = this.checkInMlm();
        if (this.inMlm) {
            this.clientThread.invokeLater(this::refreshSackValues);
        }
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(this.sceneOverlay);
        this.veins.clear();
        this.rocks.clear();
        this.brokenStruts.clear();
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (this.inMlm) {
            ItemContainer itemContainer;
            int lastSackValue = this.curSackSize;
            this.refreshSackValues();
            boolean bl = this.shouldUpdateOres = this.curSackSize < lastSackValue;
            if (this.shouldUpdateOres && (itemContainer = this.client.getItemContainer(93)) != null) {
                this.inventorySnapshot = HashMultiset.create();
                Arrays.stream(itemContainer.getItems()).filter(item -> MLM_ORE_TYPES.contains(item.getId())).forEach(item -> this.inventorySnapshot.add((Object)item.getId(), item.getQuantity()));
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        String chatMessage;
        if (!this.inMlm || event.getType() != ChatMessageType.SPAM) {
            return;
        }
        switch (chatMessage = event.getMessage()) {
            case "You just found a Diamond!": {
                if (!this.config.trackGemsFound()) break;
                this.broadcastLootItem(1617);
                break;
            }
            case "You just found a Ruby!": {
                if (!this.config.trackGemsFound()) break;
                this.broadcastLootItem(1619);
                break;
            }
            case "You just found an Emerald!": {
                if (!this.config.trackGemsFound()) break;
                this.broadcastLootItem(1621);
                break;
            }
            case "You just found a Sapphire!": {
                if (!this.config.trackGemsFound()) break;
                this.broadcastLootItem(1623);
            }
        }
    }

    private void broadcastLootItem(int itemId) {
        PluginLootReceived lootEvent = PluginLootReceived.builder().source(this).name("Motherlode Mine").type(LootRecordType.EVENT).items(Collections.singleton(new ItemStack(itemId, 1, this.client.getLocalPlayer().getLocalLocation()))).build();
        this.eventBus.post(lootEvent);
    }

    @Subscribe
    private void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() == 1634) {
            this.recolorSackOverlay();
        }
    }

    private void recolorSackOverlay() {
        ItemContainer inv = this.client.getItemContainer(93);
        if (inv == null) {
            return;
        }
        int sackSize = this.client.getVarbitValue(5558);
        boolean sackUpgraded = this.client.getVarbitValue(5556) == 1;
        int sackCapacity = sackUpgraded ? 189 : 108;
        int payDir = inv.count(12011);
        Widget sackSizeWidget = this.client.getWidget(25034757);
        Widget spaceTextWidget = this.client.getWidget(25034758);
        if (sackSizeWidget != null && spaceTextWidget != null) {
            if (payDir >= sackCapacity - sackSize) {
                sackSizeWidget.setTextColor(0xFF0000);
                spaceTextWidget.setTextColor(0xFF0000);
            } else {
                sackSizeWidget.setTextColor(0xC8C8C8);
                spaceTextWidget.setTextColor(0xFFFFFF);
            }
        }
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event) {
        if (!this.inMlm) {
            return;
        }
        WallObject wallObject = event.getWallObject();
        if (MINE_SPOTS.contains(wallObject.getId())) {
            this.veins.add(wallObject);
        }
    }

    @Subscribe
    public void onWallObjectDespawned(WallObjectDespawned event) {
        if (!this.inMlm) {
            return;
        }
        WallObject wallObject = event.getWallObject();
        this.veins.remove(wallObject);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (!this.inMlm) {
            return;
        }
        this.addGameObject(event.getGameObject());
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        if (!this.inMlm) {
            return;
        }
        this.removeGameObject(event.getGameObject());
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            this.veins.clear();
            this.rocks.clear();
            this.brokenStruts.clear();
            this.inMlm = this.checkInMlm();
        } else if (event.getGameState() == GameState.LOGIN_SCREEN) {
            this.inMlm = false;
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (!this.inMlm) {
            return;
        }
        this.recolorSackOverlay();
        if (!this.shouldUpdateOres || this.inventorySnapshot == null || event.getContainerId() != 93) {
            return;
        }
        ItemContainer container = event.getItemContainer();
        HashMultiset current = HashMultiset.create();
        Arrays.stream(container.getItems()).filter(item -> MLM_ORE_TYPES.contains(item.getId())).forEach(arg_0 -> MotherlodePlugin.lambda$onItemContainerChanged$3((Multiset)current, arg_0));
        Multiset delta = Multisets.difference((Multiset)current, this.inventorySnapshot);
        PluginLootReceived lootEvent = PluginLootReceived.builder().source(this).name("Motherlode Mine").type(LootRecordType.EVENT).items(delta.entrySet().stream().map(e -> new ItemStack((Integer)e.getElement(), e.getCount())).collect(Collectors.toList())).build();
        if (this.config.trackOresFound()) {
            this.eventBus.post(lootEvent);
        }
        this.inventorySnapshot = null;
        this.shouldUpdateOres = false;
    }

    private boolean checkInMlm() {
        int[] currentMapRegions;
        GameState gameState = this.client.getGameState();
        if (gameState != GameState.LOGGED_IN && gameState != GameState.LOADING) {
            return false;
        }
        for (int region : currentMapRegions = this.client.getMapRegions()) {
            if (MOTHERLODE_MAP_REGIONS.contains(region)) continue;
            return false;
        }
        return true;
    }

    private void refreshSackValues() {
        this.curSackSize = this.client.getVarbitValue(5558);
    }

    boolean isUpstairs(LocalPoint localPoint) {
        return Perspective.getTileHeight((Client)this.client, (LocalPoint)localPoint, (int)0) < -490;
    }

    private void addGameObject(GameObject gameObject) {
        if (ROCK_OBSTACLES.contains(gameObject.getId())) {
            this.rocks.add(gameObject);
        }
        if (26670 == gameObject.getId()) {
            this.brokenStruts.add(gameObject);
        }
    }

    private void removeGameObject(GameObject gameObject) {
        this.rocks.remove(gameObject);
        this.brokenStruts.remove(gameObject);
    }

    boolean isInMlm() {
        return this.inMlm;
    }

    Set<WallObject> getVeins() {
        return this.veins;
    }

    Set<GameObject> getRocks() {
        return this.rocks;
    }

    Set<GameObject> getBrokenStruts() {
        return this.brokenStruts;
    }

    private static /* synthetic */ void lambda$onItemContainerChanged$3(Multiset current, Item item) {
        current.add((Object)item.getId(), item.getQuantity());
    }
}

