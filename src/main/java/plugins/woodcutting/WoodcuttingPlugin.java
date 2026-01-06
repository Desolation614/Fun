/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Provides
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  net.runelite.api.Actor
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameObject
 *  net.runelite.api.NPC
 *  net.runelite.api.Tile
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.AnimationChanged
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameObjectDespawned
 *  net.runelite.api.events.GameObjectSpawned
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.InteractingChanged
 *  net.runelite.api.events.ItemSpawned
 *  net.runelite.api.events.NpcDespawned
 *  net.runelite.api.events.NpcSpawned
 *  net.runelite.api.events.ScriptPreFired
 *  net.runelite.api.events.VarbitChanged
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.woodcutting;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.woodcutting.TreeRespawn;
import net.runelite.client.plugins.woodcutting.WoodcuttingConfig;
import net.runelite.client.plugins.woodcutting.WoodcuttingOverlay;
import net.runelite.client.plugins.woodcutting.WoodcuttingSceneOverlay;
import net.runelite.client.plugins.woodcutting.WoodcuttingSession;
import net.runelite.client.plugins.woodcutting.config.ClueNestTier;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Woodcutting", description="Show woodcutting statistics and/or bird nest notifications", tags={"birds", "nest", "notifications", "overlay", "skilling", "wc", "forestry"}, enabledByDefault=false)
public class WoodcuttingPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(WoodcuttingPlugin.class);
    static final Set<Integer> WOODCUTTING_ANIMS = ImmutableSet.of((Object)879, (Object)877, (Object)875, (Object)873, (Object)871, (Object)869, (Object[])new Integer[]{867, 8303, 2846, 24, 2117, 7264, 8324, 8778, 11940, 12026, 11939, 10064, 10065, 10066, 10067, 10068, 10069, 10070, 10071, 10072, 10073, 10074});
    private static final Pattern WOOD_CUT_PATTERN = Pattern.compile("You get (?:some|an)[\\w ]+(?:logs?|mushrooms)\\.");
    private static final Pattern ANIMA_BARK_PATTERN = Pattern.compile("You've been awarded <col=[0-9a-f]+>(\\d+) Anima-infused bark</col>\\.");
    @Inject
    private Notifier notifier;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WoodcuttingOverlay overlay;
    @Inject
    private WoodcuttingSceneOverlay treesOverlay;
    @Inject
    private WoodcuttingConfig config;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ItemManager itemManager;
    @Nullable
    private WoodcuttingSession session;
    private final Set<GameObject> redwoods = new HashSet<GameObject>();
    private final List<GameObject> roots = new ArrayList<GameObject>();
    private final List<NPC> flowers = new ArrayList<NPC>();
    private NPC lastInteractFlower;
    private final List<NPC> activeFlowers = new ArrayList<NPC>(2);
    private final List<GameObject> saplingIngredients = new ArrayList<GameObject>(5);
    private final GameObject[] saplingOrder = new GameObject[3];
    private NPC foxTrap;
    private final List<GameObject> pheasantNests = new ArrayList<GameObject>(4);
    private final List<GameObject> endOfRainbows = new ArrayList<GameObject>(1);
    private NPC freakyForester;
    private NPC unfinishedBeeHive;
    private final List<NPC> circles = new ArrayList<NPC>(5);
    private final List<NPC> entlings = new ArrayList<NPC>(0);
    private final List<TreeRespawn> respawns = new ArrayList<TreeRespawn>();
    private ClueNestTier clueTierSpawned;
    private Counter leprechaunsLuckInfoBox;

    void resetSession() {
        this.session = null;
        this.infoBoxManager.removeInfoBox(this.leprechaunsLuckInfoBox);
        this.leprechaunsLuckInfoBox = null;
    }

    @Provides
    WoodcuttingConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(WoodcuttingConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.treesOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.treesOverlay);
        this.respawns.clear();
        this.redwoods.clear();
        this.roots.clear();
        this.flowers.clear();
        this.saplingIngredients.clear();
        Arrays.fill(this.saplingOrder, null);
        this.session = null;
        this.clueTierSpawned = null;
        this.infoBoxManager.removeInfoBox(this.leprechaunsLuckInfoBox);
        this.leprechaunsLuckInfoBox = null;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("woodcutting")) {
            this.clientThread.invokeLater(this::updateLeprechaunsLuck);
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        this.clueTierSpawned = null;
        this.respawns.removeIf(TreeRespawn::isExpired);
        if (this.session == null || this.session.getLastChopping() == null) {
            return;
        }
        if (WOODCUTTING_ANIMS.contains(this.client.getLocalPlayer().getAnimation())) {
            this.session.setLastChopping();
            return;
        }
        Duration statTimeout = Duration.ofMinutes(this.config.statTimeout());
        Duration sinceCut = Duration.between(this.session.getLastChopping(), Instant.now());
        if (sinceCut.compareTo(statTimeout) >= 0) {
            this.session.setActive(false);
            this.infoBoxManager.removeInfoBox(this.leprechaunsLuckInfoBox);
            this.leprechaunsLuckInfoBox = null;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        Matcher matcher;
        if (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.MESBOX) {
            return;
        }
        String msg = event.getMessage();
        if (WOOD_CUT_PATTERN.matcher(msg).matches()) {
            if (this.session == null) {
                this.session = new WoodcuttingSession();
            }
            this.session.setLastChopping();
            this.session.incrementLogsCut();
        }
        if ((matcher = ANIMA_BARK_PATTERN.matcher(msg)).matches()) {
            if (this.session == null) {
                this.session = new WoodcuttingSession();
            }
            this.session.setLastChopping();
            int num = Integer.parseInt(matcher.group(1));
            this.session.incrementBark(num);
        }
        if (msg.contains("A bird's nest falls out of the tree")) {
            if (this.clueTierSpawned == null || this.clueTierSpawned.ordinal() >= this.config.clueNestNotifyTier().ordinal()) {
                this.notifier.notify(this.config.showNestNotification(), "A bird nest has spawned!");
            }
            this.clueTierSpawned = null;
        }
        if (msg.startsWith("The sapling seems to love")) {
            int ingredientNum;
            int n = msg.contains("first") ? 1 : (msg.contains("second") ? 2 : (ingredientNum = msg.contains("third") ? 3 : -1));
            if (ingredientNum == -1) {
                log.debug("unable to find ingredient index from message: {}", (Object)msg);
                return;
            }
            GameObject ingredientObj = this.saplingIngredients.stream().filter(obj -> msg.contains(this.client.getObjectDefinition(obj.getId()).getName().toLowerCase())).findAny().orElse(null);
            if (ingredientObj == null) {
                log.debug("unable to find ingredient from message: {}", (Object)msg);
                return;
            }
            this.saplingOrder[ingredientNum - 1] = ingredientObj;
        }
        if ((msg.equals("There are no open, unpollinated flowers on this bush yet.") || msg.equals("The flowers on this bush have not yet opened enough to harvest pollen.") || msg.equals("<col=06600c>The bush is already fruiting and won't benefit from <col=06600c>any more pollen.</col>")) && this.activeFlowers.contains(this.lastInteractFlower)) {
            log.debug("Flowers reset");
            this.activeFlowers.clear();
        }
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned itemSpawned) {
        if (this.clueTierSpawned == null) {
            this.clueTierSpawned = ClueNestTier.getTierFromItem(itemSpawned.getItem().getId());
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        switch (gameObject.getId()) {
            case 29668: 
            case 29670: 
            case 34284: 
            case 34286: 
            case 34288: 
            case 34290: 
            case 34633: 
            case 34635: 
            case 34637: 
            case 34639: {
                this.redwoods.add(gameObject);
                break;
            }
            case 47482: 
            case 47483: {
                if (this.roots.isEmpty()) {
                    this.notifier.notify(this.config.forestryRisingRootsNotification(), "A Rising Roots Forestry event spawned!");
                }
                this.roots.add(gameObject);
                break;
            }
            case 47484: 
            case 47485: 
            case 47487: 
            case 47488: 
            case 47490: 
            case 47491: {
                this.notifier.notify(this.config.forestryStrugglingSaplingNotification(), "A Struggling Sapling Forestry event spawned!");
                break;
            }
            case 47493: 
            case 47494: 
            case 47495: 
            case 47496: 
            case 47497: 
            case 47498: 
            case 47499: {
                this.saplingIngredients.add(gameObject);
                break;
            }
            case 49936: 
            case 49937: {
                this.pheasantNests.add(gameObject);
                break;
            }
            case 49934: {
                this.endOfRainbows.add(gameObject);
            }
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject object = event.getGameObject();
        switch (object.getId()) {
            case 29668: 
            case 29670: 
            case 34284: 
            case 34286: 
            case 34288: 
            case 34290: 
            case 34633: 
            case 34635: 
            case 34637: 
            case 34639: {
                this.redwoods.remove(object);
                break;
            }
            case 47482: 
            case 47483: {
                this.roots.remove(object);
                break;
            }
            case 47493: 
            case 47494: 
            case 47495: 
            case 47496: 
            case 47497: 
            case 47498: 
            case 47499: {
                this.saplingIngredients.remove(object);
                if (!this.saplingIngredients.isEmpty()) break;
                Arrays.fill(this.saplingOrder, null);
                log.debug("Struggling Sapling event is over");
                break;
            }
            case 49936: 
            case 49937: {
                this.pheasantNests.remove(object);
                if (!this.pheasantNests.isEmpty()) break;
                log.debug("Pheasant event is over");
                break;
            }
            case 49934: {
                this.endOfRainbows.remove(object);
            }
        }
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired) {
        if (scriptPreFired.getScriptId() == 5474) {
            Object[] args = scriptPreFired.getScriptEvent().getArguments();
            int locCoord = (Integer)args[1];
            int locId = (Integer)args[2];
            int ticks = (Integer)args[5];
            log.debug("Add overlay loc={} coord={} ticks={}", new Object[]{locId, locCoord, ticks});
            switch (locId) {
                case 1341: 
                case 1342: 
                case 1343: 
                case 1344: 
                case 1345: 
                case 1346: 
                case 1347: 
                case 1348: 
                case 1349: 
                case 1350: 
                case 1351: 
                case 1352: 
                case 1353: 
                case 1354: 
                case 1355: 
                case 1356: 
                case 1357: 
                case 1358: 
                case 1359: 
                case 2310: 
                case 2891: 
                case 3371: 
                case 3649: 
                case 3880: 
                case 3884: 
                case 4061: 
                case 4328: 
                case 4329: 
                case 4819: 
                case 4821: 
                case 4822: 
                case 5905: 
                case 6212: 
                case 7961: 
                case 8019: 
                case 8046: 
                case 8077: 
                case 8104: 
                case 8131: 
                case 8357: 
                case 8410: 
                case 8445: 
                case 8468: 
                case 8489: 
                case 8514: 
                case 9035: 
                case 9037: 
                case 9471: 
                case 9661: 
                case 9711: 
                case 9712: 
                case 9713: 
                case 9714: 
                case 10057: 
                case 12894: 
                case 14516: 
                case 14517: 
                case 14567: 
                case 14596: 
                case 14638: 
                case 14667: 
                case 14697: 
                case 16266: 
                case 16605: 
                case 21274: 
                case 23054: 
                case 25186: 
                case 26834: 
                case 27061: 
                case 29669: 
                case 29670: 
                case 29671: 
                case 30418: 
                case 30446: 
                case 30855: 
                case 30856: 
                case 33583: 
                case 33584: 
                case 33721: 
                case 34034: 
                case 34633: 
                case 34635: 
                case 34637: 
                case 34639: 
                case 36673: 
                case 36675: 
                case 36678: 
                case 36680: 
                case 36684: 
                case 36687: 
                case 36689: 
                case 40751: 
                case 40753: 
                case 40757: 
                case 40759: 
                case 40761: 
                case 42392: 
                case 42394: 
                case 42396: 
                case 46582: 
                case 46583: 
                case 50035: {
                    WorldPoint worldPoint = WorldPoint.fromCoord((int)locCoord);
                    GameObject gameObject = this.findObject(worldPoint);
                    if (gameObject == null) {
                        return;
                    }
                    TreeRespawn treeRespawn = new TreeRespawn(worldPoint, gameObject.sizeX() - 1, gameObject.sizeY() - 1, Instant.now(), ticks * 600);
                    this.respawns.add(treeRespawn);
                }
            }
        }
    }

    private GameObject findObject(WorldPoint point) {
        LocalPoint localPoint = LocalPoint.fromWorld((Client)this.client, (WorldPoint)point);
        if (localPoint == null) {
            return null;
        }
        Tile tile = this.client.getScene().getTiles()[point.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
        if (tile == null) {
            return null;
        }
        for (GameObject gameObject : tile.getGameObjects()) {
            if (gameObject == null) continue;
            return gameObject;
        }
        return null;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case LOGIN_SCREEN: 
            case HOPPING: {
                this.respawns.clear();
                this.flowers.clear();
                this.activeFlowers.clear();
                this.foxTrap = null;
                this.freakyForester = null;
                this.unfinishedBeeHive = null;
                this.circles.clear();
                this.entlings.clear();
            }
            case LOADING: {
                this.redwoods.clear();
                this.roots.clear();
                this.saplingIngredients.clear();
                Arrays.fill(this.saplingOrder, null);
                this.pheasantNests.clear();
                this.endOfRainbows.clear();
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() == 15344) {
            this.updateLeprechaunsLuck();
        }
    }

    private void updateLeprechaunsLuck() {
        int leprechaunsLuck = this.client.getVarbitValue(15344) / 5;
        if (leprechaunsLuck < 1 || this.session == null || !this.session.isActive() || !this.config.showLeprechaunLuck()) {
            if (this.leprechaunsLuckInfoBox != null) {
                this.infoBoxManager.removeInfoBox(this.leprechaunsLuckInfoBox);
                this.leprechaunsLuckInfoBox = null;
            }
            return;
        }
        if (this.leprechaunsLuckInfoBox == null) {
            this.leprechaunsLuckInfoBox = new Counter(this.itemManager.getImage(28181), this, leprechaunsLuck);
            this.infoBoxManager.addInfoBox(this.leprechaunsLuckInfoBox);
        }
        this.leprechaunsLuckInfoBox.setCount(leprechaunsLuck);
        this.leprechaunsLuckInfoBox.setTooltip("Leprechaun's Luck: " + leprechaunsLuck);
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        NPC flower;
        Actor actor = event.getActor();
        if (actor.getAnimation() == 832 && this.flowers.contains(actor.getInteracting()) && !this.activeFlowers.contains(flower = (NPC)actor.getInteracting())) {
            if (this.activeFlowers.size() == 2) {
                log.debug("Flowers reset");
                this.activeFlowers.clear();
            }
            log.debug("Tracked flower {}", (Object)flower);
            this.activeFlowers.add(flower);
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        int id = npc.getId();
        if (WoodcuttingPlugin.isFloweringBush(id)) {
            if (this.flowers.isEmpty()) {
                this.notifier.notify(this.config.forestryFloweringTreeNotification(), "A Flowering Tree Forestry event spawned!");
            }
            this.flowers.add(npc);
        } else if (id == 12163) {
            this.notifier.notify(this.config.forestryLeprechaunNotification(), "A Leprechaun event spawned!");
        } else if (id == 12559 || id == 12560) {
            this.notifier.notify(this.config.forestryPoachersNotification(), "A Poachers event spawned!");
        } else if (id == 12541) {
            this.foxTrap = npc;
        } else if (id == 12536) {
            this.freakyForester = npc;
            this.notifier.notify(this.config.forestryPheasantControlNotification(), "A Pheasant Control event has spawned!");
        } else if (id == 12518) {
            this.notifier.notify(this.config.forestryBeeHiveNotification(), "A Bee Hive event has spawned!");
        } else if (id == 12515 || id == 12516) {
            this.unfinishedBeeHive = npc;
        } else if (id >= 12520 && id <= 12535) {
            this.circles.add(npc);
        } else if (id == 12519) {
            this.notifier.notify(this.config.forestryEnchantmentRitualNotification(), "An Enchantment Ritual event has spawned!");
        } else if (id == 12543) {
            this.entlings.add(npc);
            if (this.entlings.size() == 1) {
                this.notifier.notify(this.config.forestryFriendlyEntNotification(), "A Friendly Ent event has spawned!");
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        if (this.flowers.remove(npc)) {
            if (this.activeFlowers.remove(npc)) {
                this.activeFlowers.clear();
            }
            if (npc == this.lastInteractFlower) {
                this.lastInteractFlower = null;
            }
        }
        if (this.foxTrap == npc) {
            this.foxTrap = null;
        }
        if (this.freakyForester == npc) {
            this.freakyForester = null;
        }
        if (this.unfinishedBeeHive == npc) {
            this.unfinishedBeeHive = null;
        }
        this.circles.remove(npc);
        this.entlings.remove(npc);
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (event.getSource() != this.client.getLocalPlayer() || !(event.getTarget() instanceof NPC) || !WoodcuttingPlugin.isFloweringBush(((NPC)event.getTarget()).getId())) {
            return;
        }
        this.lastInteractFlower = (NPC)event.getTarget();
    }

    private static boolean isFloweringBush(int npcId) {
        return npcId == 12120 || npcId == 12134 || npcId == 12141 || npcId == 12143 || npcId == 12145 || npcId == 12147 || npcId == 12149 || npcId == 12151;
    }

    NPC solveCircles() {
        int id;
        int color;
        int shape;
        int off;
        if (this.circles.size() != 5) {
            return null;
        }
        int s = 0;
        for (NPC npc : this.circles) {
            off = npc.getId() - 12520;
            shape = off / 4;
            color = off % 4;
            id = 16 << shape | 1 << color;
            s ^= id;
        }
        for (NPC npc : this.circles) {
            off = npc.getId() - 12520;
            shape = off / 4;
            id = 16 << shape | 1 << (color = off % 4);
            if ((id & s) != id) continue;
            return npc;
        }
        return null;
    }

    @Nullable
    public WoodcuttingSession getSession() {
        return this.session;
    }

    public Set<GameObject> getRedwoods() {
        return this.redwoods;
    }

    List<GameObject> getRoots() {
        return this.roots;
    }

    List<NPC> getFlowers() {
        return this.flowers;
    }

    List<NPC> getActiveFlowers() {
        return this.activeFlowers;
    }

    List<GameObject> getSaplingIngredients() {
        return this.saplingIngredients;
    }

    GameObject[] getSaplingOrder() {
        return this.saplingOrder;
    }

    NPC getFoxTrap() {
        return this.foxTrap;
    }

    List<GameObject> getPheasantNests() {
        return this.pheasantNests;
    }

    List<GameObject> getEndOfRainbows() {
        return this.endOfRainbows;
    }

    NPC getFreakyForester() {
        return this.freakyForester;
    }

    NPC getUnfinishedBeeHive() {
        return this.unfinishedBeeHive;
    }

    List<TreeRespawn> getRespawns() {
        return this.respawns;
    }
}

