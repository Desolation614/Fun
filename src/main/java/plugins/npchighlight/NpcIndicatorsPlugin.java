/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.GraphicsObject
 *  net.runelite.api.Menu
 *  net.runelite.api.MenuAction
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.NPC
 *  net.runelite.api.NPCComposition
 *  net.runelite.api.WorldView
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.GraphicsObjectCreated
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.NpcChanged
 *  net.runelite.api.events.NpcDespawned
 *  net.runelite.api.events.NpcSpawned
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.npchighlight;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.inject.Provides;
import java.awt.Color;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.npchighlight.MemorizedNpc;
import net.runelite.client.plugins.npchighlight.NpcIndicatorsConfig;
import net.runelite.client.plugins.npchighlight.NpcRespawnOverlay;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="NPC Indicators", description="Highlight NPCs on-screen and/or on the minimap", tags={"highlight", "minimap", "npcs", "overlay", "respawn", "tags"})
public class NpcIndicatorsPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(NpcIndicatorsPlugin.class);
    private static final int MAX_ACTOR_VIEW_RANGE = 15;
    private static final int MAX_RESPAWN_TIME_TICKS = 500;
    private static final String TAG = "Tag";
    private static final String UNTAG = "Un-tag";
    private static final String TAG_ALL = "Tag-All";
    private static final String UNTAG_ALL = "Un-tag-All";
    private static final String STYLE_HULL = "hull";
    private static final String STYLE_TILE = "tile";
    private static final String STYLE_TRUE_TILE = "truetile";
    private static final String STYLE_SW_TILE = "swtile";
    private static final String STYLE_SW_TRUE_TILE = "swtruetile";
    private static final String STYLE_OUTLINE = "outline";
    @Inject
    private Client client;
    @Inject
    private NpcIndicatorsConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private NpcRespawnOverlay npcRespawnOverlay;
    @Inject
    private ClientThread clientThread;
    @Inject
    private NpcOverlayService npcOverlayService;
    @Inject
    private NpcUtil npcUtil;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ColorPickerManager colorPickerManager;
    private final Map<NPC, HighlightedNpc> highlightedNpcs = new HashMap<NPC, HighlightedNpc>();
    private final Map<Integer, MemorizedNpc> deadNpcsToDisplay = new HashMap<Integer, MemorizedNpc>();
    private Instant lastTickUpdate;
    private final Map<Integer, MemorizedNpc> memorizedNpcs = new HashMap<Integer, MemorizedNpc>();
    private List<String> highlights = new ArrayList<String>();
    private final Set<Integer> npcTags = new HashSet<Integer>();
    private final List<NPC> spawnedNpcsThisTick = new ArrayList<NPC>();
    private final List<NPC> despawnedNpcsThisTick = new ArrayList<NPC>();
    private final Set<WorldPoint> teleportGraphicsObjectSpawnedThisTick = new HashSet<WorldPoint>();
    private WorldPoint lastPlayerLocation;
    private boolean skipNextSpawnCheck = false;
    private final Function<NPC, HighlightedNpc> isHighlighted = this.highlightedNpcs::get;

    @Provides
    NpcIndicatorsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(NpcIndicatorsConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.npcOverlayService.registerHighlighter(this.isHighlighted);
        this.overlayManager.add(this.npcRespawnOverlay);
        this.clientThread.invoke(() -> {
            this.skipNextSpawnCheck = true;
            this.rebuild();
        });
        this.migrate();
    }

    @Override
    protected void shutDown() throws Exception {
        this.npcOverlayService.unregisterHighlighter(this.isHighlighted);
        this.overlayManager.remove(this.npcRespawnOverlay);
        this.clientThread.invoke(() -> {
            this.deadNpcsToDisplay.clear();
            this.memorizedNpcs.clear();
            this.spawnedNpcsThisTick.clear();
            this.despawnedNpcsThisTick.clear();
            this.teleportGraphicsObjectSpawnedThisTick.clear();
            this.npcTags.clear();
            this.highlightedNpcs.clear();
        });
    }

    private void migrate() {
        Boolean b = (Boolean)this.configManager.getConfiguration("npcindicators", "highlightMenuNames", (Type)((Object)Boolean.class));
        if (b != null) {
            log.debug("Migrated highlightMenuNames");
            this.configManager.unsetConfiguration("npcindicators", "highlightMenuNames");
            this.configManager.setConfiguration("npcindicators", "highlightMenuStyle", b != false ? NpcIndicatorsConfig.MenuHighlightStyle.BOTH : NpcIndicatorsConfig.MenuHighlightStyle.NONE);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING) {
            this.highlightedNpcs.clear();
            this.deadNpcsToDisplay.clear();
            this.memorizedNpcs.forEach((id, npc) -> npc.setDiedOnTick(-1));
            this.lastPlayerLocation = null;
            this.skipNextSpawnCheck = true;
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (!configChanged.getGroup().equals("npcindicators")) {
            return;
        }
        this.clientThread.invoke(this::rebuild);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        MenuEntry menuEntry = event.getMenuEntry();
        MenuAction menuAction = menuEntry.getType();
        NPC npc = menuEntry.getNpc();
        if (npc == null) {
            return;
        }
        if (menuAction == MenuAction.EXAMINE_NPC && this.client.isKeyPressed(81)) {
            if (npc.getName() == null) {
                return;
            }
            String npcName = npc.getName();
            boolean nameMatch = this.highlights.stream().anyMatch(npcName::equalsIgnoreCase);
            boolean idMatch = this.npcTags.contains(npc.getIndex());
            boolean wildcardMatch = this.highlights.stream().filter(highlight -> !highlight.equalsIgnoreCase(npcName)).anyMatch(highlight -> WildcardMatcher.matches(highlight, npcName));
            int idx = -1;
            this.client.createMenuEntry(idx--).setOption(idMatch ? UNTAG : TAG).setTarget(event.getTarget()).setIdentifier(event.getIdentifier()).setType(MenuAction.RUNELITE).onClick(this::tag);
            if (!wildcardMatch) {
                this.client.createMenuEntry(idx--).setOption(nameMatch ? UNTAG_ALL : TAG_ALL).setTarget(event.getTarget()).setIdentifier(event.getIdentifier()).setType(MenuAction.RUNELITE).onClick(this::tag);
            }
            if (idMatch || nameMatch || wildcardMatch) {
                idx = this.createTagColorMenu(idx, event.getTarget(), npc);
                idx = this.createTagStyleMenu(idx, event.getTarget(), npc);
            }
        } else {
            Color color;
            if (this.npcUtil.isDying(npc) && (color = this.config.deadNpcMenuColor()) != null) {
                String target = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), color);
                menuEntry.setTarget(target);
                return;
            }
            if (!(!this.highlightedNpcs.containsKey(npc) || this.npcUtil.isDying(npc) && this.config.ignoreDeadNpcs())) {
                color = (Color)MoreObjects.firstNonNull((Object)this.getNpcHighlightColor(npc.getId()), (Object)this.config.highlightColor());
                String target = NpcIndicatorsPlugin.recolorMenuTarget(event.getTarget(), this.config.highlightMenuStyle(), color);
                menuEntry.setTarget(target);
                return;
            }
        }
    }

    private static String recolorMenuTarget(String target, NpcIndicatorsConfig.MenuHighlightStyle style, Color color) {
        String level;
        String name;
        if (style == NpcIndicatorsConfig.MenuHighlightStyle.NONE) {
            return target;
        }
        if (style == NpcIndicatorsConfig.MenuHighlightStyle.BOTH) {
            return ColorUtil.prependColorTag(Text.removeTags(target), color);
        }
        if (target.contains("  (level-")) {
            int c = target.lastIndexOf(60);
            name = target.substring(0, c);
            level = target.substring(c);
        } else {
            name = target;
            level = null;
        }
        if (style == NpcIndicatorsConfig.MenuHighlightStyle.NAME) {
            Object t = ColorUtil.prependColorTag(Text.removeTags(name), color);
            if (level != null) {
                t = (String)t + level;
            }
            return t;
        }
        if (style == NpcIndicatorsConfig.MenuHighlightStyle.LEVEL) {
            Object t = name;
            if (level != null) {
                t = (String)t + ColorUtil.prependColorTag(Text.removeTags(level), color);
            }
            return t;
        }
        throw new IllegalArgumentException();
    }

    private int createTagColorMenu(int idx, String target, NPC npc) {
        List<Color> colors = this.getUsedColors();
        for (Color default_ : new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA}) {
            if (colors.size() >= 5 || colors.contains(default_)) continue;
            colors.add(default_);
        }
        MenuEntry parent = this.client.createMenuEntry(idx--).setOption("Tag color").setTarget(target).setType(MenuAction.RUNELITE);
        Menu submenu = parent.createSubMenu();
        for (Color c : colors) {
            submenu.createMenuEntry(0).setOption(ColorUtil.prependColorTag("Set color", c)).setType(MenuAction.RUNELITE).onClick(e -> {
                this.setNpcHighlightColor(npc.getId(), c);
                this.clientThread.invokeLater(this::rebuild);
            });
        }
        submenu.createMenuEntry(0).setOption("Pick color").setType(MenuAction.RUNELITE).onClick(e -> SwingUtilities.invokeLater(() -> {
            RuneliteColorPicker colorPicker = this.colorPickerManager.create(this.client, Color.WHITE, "Tag Color", false);
            colorPicker.setOnClose(c -> {
                this.setNpcHighlightColor(npc.getId(), (Color)c);
                this.clientThread.invokeLater(this::rebuild);
            });
            colorPicker.setVisible(true);
        }));
        if (this.getNpcHighlightColor(npc.getId()) != null) {
            submenu.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(e -> {
                this.unsetNpcHighlightColor(npc.getId());
                this.clientThread.invokeLater(this::rebuild);
            });
        }
        return idx;
    }

    private int createTagStyleMenu(int idx, String target, NPC npc) {
        MenuEntry parent = this.client.createMenuEntry(idx--).setOption("Tag style").setTarget(target).setType(MenuAction.RUNELITE);
        Menu submenu = parent.createSubMenu();
        String[] names = new String[]{"Hull", "Tile", "True tile", "South-west tile", "South-west true tile", "Outline"};
        String[] styles = new String[]{STYLE_HULL, STYLE_TILE, STYLE_TRUE_TILE, STYLE_SW_TILE, STYLE_SW_TRUE_TILE, STYLE_OUTLINE};
        assert (names.length == styles.length);
        for (int i = 0; i < names.length; ++i) {
            String style = styles[i];
            submenu.createMenuEntry(0).setOption(names[i]).setType(MenuAction.RUNELITE).onClick(e -> {
                this.setNpcTagStyle(npc.getId(), style);
                this.clientThread.invokeLater(this::rebuild);
            });
        }
        if (this.getNpcTagStyle(npc.getId()) != null) {
            submenu.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick(e -> {
                this.unsetNpcTagStyle(npc.getId());
                this.clientThread.invokeLater(this::rebuild);
            });
        }
        return idx;
    }

    private void tag(MenuEntry entry) {
        int id = entry.getIdentifier();
        WorldView wv = this.client.getTopLevelWorldView();
        NPC npc = (NPC)wv.npcs().byIndex(id);
        if (npc == null || npc.getName() == null) {
            return;
        }
        if (entry.getOption().equals(TAG) || entry.getOption().equals(UNTAG)) {
            boolean removed = this.npcTags.remove(id);
            if (removed) {
                if (!this.highlightMatchesNPCName(npc.getName())) {
                    this.highlightedNpcs.remove(npc);
                    this.memorizedNpcs.remove(npc.getIndex());
                }
            } else {
                if (!wv.isInstance()) {
                    this.memorizeNpc(npc);
                    this.npcTags.add(id);
                }
                this.highlightedNpcs.put(npc, this.highlightedNpc(npc));
            }
            this.npcOverlayService.rebuild();
        } else {
            String name = npc.getName();
            ArrayList<String> highlightedNpcs = new ArrayList<String>(this.highlights);
            if (!highlightedNpcs.removeIf(name::equalsIgnoreCase)) {
                highlightedNpcs.add(name);
            }
            this.config.setNpcToHighlight(Text.toCSV(highlightedNpcs));
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        String npcName = npc.getName();
        if (npcName == null) {
            return;
        }
        if (this.npcTags.contains(npc.getIndex())) {
            this.memorizeNpc(npc);
            this.highlightedNpcs.put(npc, this.highlightedNpc(npc));
            this.spawnedNpcsThisTick.add(npc);
            return;
        }
        if (this.highlightMatchesNPCName(npcName)) {
            this.highlightedNpcs.put(npc, this.highlightedNpc(npc));
            if (!this.client.isInInstancedRegion()) {
                this.memorizeNpc(npc);
                this.spawnedNpcsThisTick.add(npc);
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        if (this.memorizedNpcs.containsKey(npc.getIndex())) {
            this.despawnedNpcsThisTick.add(npc);
        }
        this.highlightedNpcs.remove(npc);
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        NPC npc = event.getNpc();
        String npcName = npc.getName();
        this.highlightedNpcs.remove(npc);
        if (npcName == null) {
            return;
        }
        if (this.npcTags.contains(npc.getIndex()) || this.highlightMatchesNPCName(npcName)) {
            this.highlightedNpcs.put(npc, this.highlightedNpc(npc));
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        GraphicsObject go = event.getGraphicsObject();
        if (go.getId() == 86) {
            this.teleportGraphicsObjectSpawnedThisTick.add(WorldPoint.fromLocal((Client)this.client, (LocalPoint)go.getLocation()));
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        this.removeOldHighlightedRespawns();
        this.validateSpawnedNpcs();
        this.lastTickUpdate = Instant.now();
        this.lastPlayerLocation = this.client.getLocalPlayer().getWorldLocation();
    }

    private static boolean isInViewRange(WorldPoint wp1, WorldPoint wp2) {
        int distance = wp1.distanceTo(wp2);
        return distance < 15;
    }

    private static WorldPoint getWorldLocationBehind(NPC npc) {
        int orientation = npc.getOrientation() / 256;
        int dx = 0;
        int dy = 0;
        switch (orientation) {
            case 0: {
                dy = -1;
                break;
            }
            case 1: {
                dx = -1;
                dy = -1;
                break;
            }
            case 2: {
                dx = -1;
                break;
            }
            case 3: {
                dx = -1;
                dy = 1;
                break;
            }
            case 4: {
                dy = 1;
                break;
            }
            case 5: {
                dx = 1;
                dy = 1;
                break;
            }
            case 6: {
                dx = 1;
                break;
            }
            case 7: {
                dx = 1;
                dy = -1;
            }
        }
        WorldPoint currWP = npc.getWorldLocation();
        return new WorldPoint(currWP.getX() - dx, currWP.getY() - dy, currWP.getPlane());
    }

    private void memorizeNpc(NPC npc) {
        int npcIndex = npc.getIndex();
        this.memorizedNpcs.putIfAbsent(npcIndex, new MemorizedNpc(npc));
    }

    private void removeOldHighlightedRespawns() {
        this.deadNpcsToDisplay.values().removeIf(x -> x.getDiedOnTick() + x.getRespawnTime() <= this.client.getTickCount() + 1);
    }

    @VisibleForTesting
    List<String> getHighlights() {
        String configNpcs = this.config.getNpcToHighlight();
        if (configNpcs.isEmpty()) {
            return Collections.emptyList();
        }
        return Text.fromCSV(configNpcs);
    }

    void rebuild() {
        this.highlights = this.getHighlights();
        this.highlightedNpcs.clear();
        if (this.client.getGameState() != GameState.LOGGED_IN && this.client.getGameState() != GameState.LOADING) {
            return;
        }
        for (NPC npc : this.client.getNpcs()) {
            String npcName = npc.getName();
            if (npcName == null) continue;
            if (this.npcTags.contains(npc.getIndex())) {
                this.highlightedNpcs.put(npc, this.highlightedNpc(npc));
                continue;
            }
            if (this.highlightMatchesNPCName(npcName)) {
                if (!this.client.isInInstancedRegion()) {
                    this.memorizeNpc(npc);
                }
                this.highlightedNpcs.put(npc, this.highlightedNpc(npc));
                continue;
            }
            this.memorizedNpcs.remove(npc.getIndex());
        }
        this.npcOverlayService.rebuild();
    }

    private boolean highlightMatchesNPCName(String npcName) {
        for (String highlight : this.highlights) {
            if (!WildcardMatcher.matches(highlight, npcName)) continue;
            return true;
        }
        return false;
    }

    private void validateSpawnedNpcs() {
        if (this.skipNextSpawnCheck) {
            this.skipNextSpawnCheck = false;
        } else {
            MemorizedNpc mn;
            for (NPC npc : this.despawnedNpcsThisTick) {
                if (!this.teleportGraphicsObjectSpawnedThisTick.isEmpty() && this.teleportGraphicsObjectSpawnedThisTick.contains(npc.getWorldLocation()) || !NpcIndicatorsPlugin.isInViewRange(this.client.getLocalPlayer().getWorldLocation(), npc.getWorldLocation()) || (mn = this.memorizedNpcs.get(npc.getIndex())) == null) continue;
                mn.setDiedOnTick(this.client.getTickCount() + 1);
                if (mn.getPossibleRespawnLocations().isEmpty()) continue;
                log.debug("Starting {} tick countdown for {}", (Object)mn.getRespawnTime(), (Object)mn.getNpcName());
                this.deadNpcsToDisplay.put(mn.getNpcIndex(), mn);
            }
            for (NPC npc : this.spawnedNpcsThisTick) {
                if (!this.teleportGraphicsObjectSpawnedThisTick.isEmpty() && (this.teleportGraphicsObjectSpawnedThisTick.contains(npc.getWorldLocation()) || this.teleportGraphicsObjectSpawnedThisTick.contains(NpcIndicatorsPlugin.getWorldLocationBehind(npc))) || this.lastPlayerLocation == null || !NpcIndicatorsPlugin.isInViewRange(this.lastPlayerLocation, npc.getWorldLocation())) continue;
                mn = this.memorizedNpcs.get(npc.getIndex());
                if (mn.getDiedOnTick() != -1) {
                    int respawnTime = this.client.getTickCount() + 1 - mn.getDiedOnTick();
                    if ((mn.getRespawnTime() == -1 || respawnTime < mn.getRespawnTime()) && respawnTime <= 500) {
                        mn.setRespawnTime(respawnTime);
                    }
                    mn.setDiedOnTick(-1);
                }
                WorldPoint npcLocation = npc.getWorldLocation();
                WorldPoint possibleOtherNpcLocation = NpcIndicatorsPlugin.getWorldLocationBehind(npc);
                mn.getPossibleRespawnLocations().removeIf(x -> !x.equals((Object)npcLocation) && !x.equals((Object)possibleOtherNpcLocation));
                if (!mn.getPossibleRespawnLocations().isEmpty()) continue;
                mn.getPossibleRespawnLocations().add(npcLocation);
                mn.getPossibleRespawnLocations().add(possibleOtherNpcLocation);
            }
        }
        this.spawnedNpcsThisTick.clear();
        this.despawnedNpcsThisTick.clear();
        this.teleportGraphicsObjectSpawnedThisTick.clear();
    }

    private HighlightedNpc highlightedNpc(NPC npc) {
        boolean outline;
        boolean swTrueTile;
        boolean swTile;
        boolean trueTile;
        boolean tile;
        boolean hull;
        int npcId = npc.getId();
        String style = this.getNpcTagStyle(npcId);
        if (style != null) {
            hull = STYLE_HULL.equals(style);
            tile = STYLE_TILE.equals(style);
            trueTile = STYLE_TRUE_TILE.equals(style);
            swTile = STYLE_SW_TILE.equals(style);
            swTrueTile = STYLE_SW_TRUE_TILE.equals(style);
            outline = STYLE_OUTLINE.equals(style);
        } else {
            hull = this.config.highlightHull();
            tile = this.config.highlightTile();
            trueTile = this.config.highlightTrueTile();
            swTile = this.config.highlightSouthWestTile();
            swTrueTile = this.config.highlightSouthWestTrueTile();
            outline = this.config.highlightOutline();
        }
        return HighlightedNpc.builder().npc(npc).highlightColor((Color)MoreObjects.firstNonNull((Object)this.getNpcHighlightColor(npcId), (Object)this.config.highlightColor())).fillColor(this.config.fillColor()).hull(hull).tile(tile).trueTile(trueTile).swTile(swTile).swTrueTile(swTrueTile).outline(outline).name(this.config.drawNames()).nameOnMinimap(this.config.drawMinimapNames()).borderWidth((float)this.config.borderWidth()).outlineFeather(this.config.outlineFeather()).render(this::render).build();
    }

    private boolean render(NPC n) {
        if (this.npcUtil.isDying(n) && this.config.ignoreDeadNpcs()) {
            return false;
        }
        NPCComposition c = n.getTransformedComposition();
        return c == null || !c.isFollower() || !this.config.ignorePets();
    }

    private void setNpcHighlightColor(int npcId, Color color) {
        this.configManager.setConfiguration("npcindicators", "highlightcolor_" + npcId, color);
    }

    private void unsetNpcHighlightColor(int npcId) {
        this.configManager.unsetConfiguration("npcindicators", "highlightcolor_" + npcId);
    }

    private Color getNpcHighlightColor(int npcId) {
        return (Color)this.configManager.getConfiguration("npcindicators", "highlightcolor_" + npcId, (Type)((Object)Color.class));
    }

    private void setNpcTagStyle(int npcId, String style) {
        this.configManager.setConfiguration("npcindicators", "tagstyle_" + npcId, style);
    }

    private void unsetNpcTagStyle(int npcId) {
        this.configManager.unsetConfiguration("npcindicators", "tagstyle_" + npcId);
    }

    private String getNpcTagStyle(int npcId) {
        return this.configManager.getConfiguration("npcindicators", "tagstyle_" + npcId);
    }

    private List<Color> getUsedColors() {
        ArrayList<Color> colors = new ArrayList<Color>();
        for (NPC npc : this.client.getNpcs()) {
            Color c = this.getNpcHighlightColor(npc.getId());
            if (c == null || colors.contains(c)) continue;
            colors.add(c);
            if (colors.size() < 5) continue;
            break;
        }
        return colors;
    }

    Map<NPC, HighlightedNpc> getHighlightedNpcs() {
        return this.highlightedNpcs;
    }

    Map<Integer, MemorizedNpc> getDeadNpcsToDisplay() {
        return this.deadNpcsToDisplay;
    }

    Instant getLastTickUpdate() {
        return this.lastTickUpdate;
    }
}

