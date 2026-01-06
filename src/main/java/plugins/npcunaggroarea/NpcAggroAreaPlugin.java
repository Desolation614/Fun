/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.base.Strings
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.NPC
 *  net.runelite.api.NPCComposition
 *  net.runelite.api.WorldView
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldArea
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.NpcSpawned
 *  net.runelite.api.geometry.Geometry
 */
package net.runelite.client.plugins.npcunaggroarea;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.inject.Provides;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.geometry.Geometry;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.npcunaggroarea.AggressionTimer;
import net.runelite.client.plugins.npcunaggroarea.NpcAggroAreaConfig;
import net.runelite.client.plugins.npcunaggroarea.NpcAggroAreaOverlay;
import net.runelite.client.plugins.npcunaggroarea.UncalibratedInfobox;
import net.runelite.client.plugins.slayer.SlayerPlugin;
import net.runelite.client.plugins.slayer.SlayerPluginService;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.WildcardMatcher;

@PluginDescriptor(name="NPC Aggression Timer", description="Highlights the unaggressive area of NPCs nearby and timer until it becomes active", tags={"highlight", "lines", "unaggro", "aggro", "aggressive", "npcs", "area", "slayer"}, enabledByDefault=false)
@PluginDependency(value=SlayerPlugin.class)
public class NpcAggroAreaPlugin
extends Plugin {
    private static final int SAFE_AREA_RADIUS = 10;
    private static final int UNKNOWN_AREA_RADIUS = 20;
    private static final Duration AGGRESSIVE_TIME_DURATION = Duration.ofSeconds(600L);
    private static final Splitter NAME_SPLITTER = Splitter.on((char)',').omitEmptyStrings().trimResults();
    private static final WorldArea WILDERNESS_ABOVE_GROUND = new WorldArea(2944, 3523, 448, 448, 0);
    private static final WorldArea WILDERNESS_UNDERGROUND = new WorldArea(2944, 9918, 320, 442, 0);
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private NpcAggroAreaConfig config;
    @Inject
    private NpcAggroAreaOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ItemManager itemManager;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private Notifier notifier;
    @Inject
    private SlayerPluginService slayerPluginService;
    private final WorldPoint[] safeCenters = new WorldPoint[2];
    private final GeneralPath[] linesToDisplay = new GeneralPath[4];
    private boolean active;
    private Instant endTime;
    private WorldPoint lastPlayerLocation;
    private WorldPoint previousUnknownCenter;
    private boolean loggingIn;
    private boolean notifyOnce;
    private List<String> npcNamePatterns;

    @Provides
    NpcAggroAreaConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(NpcAggroAreaConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.npcNamePatterns = NAME_SPLITTER.splitToList((CharSequence)this.config.npcNamePatterns());
        this.infoBoxManager.addInfoBox(new UncalibratedInfobox((BufferedImage)this.itemManager.getImage(13501), this));
        this.clientThread.invokeLater(this::scanNpcs);
    }

    @Override
    protected void shutDown() throws Exception {
        this.removeTimer();
        this.overlayManager.remove(this.overlay);
        this.infoBoxManager.removeIf(UncalibratedInfobox.class::isInstance);
        Arrays.fill(this.safeCenters, null);
        this.lastPlayerLocation = null;
        this.endTime = null;
        this.loggingIn = false;
        this.npcNamePatterns = null;
        this.active = false;
        Arrays.fill(this.linesToDisplay, null);
    }

    private Area generateSafeArea() {
        Area area = new Area();
        for (WorldPoint wp : this.safeCenters) {
            if (wp == null) continue;
            Polygon poly = new Polygon();
            poly.addPoint(wp.getX() - 10, wp.getY() - 10);
            poly.addPoint(wp.getX() - 10, wp.getY() + 10 + 1);
            poly.addPoint(wp.getX() + 10 + 1, wp.getY() + 10 + 1);
            poly.addPoint(wp.getX() + 10 + 1, wp.getY() - 10);
            area.add(new Area(poly));
        }
        return area;
    }

    private void transformWorldToLocal(float[] coords) {
        LocalPoint lp = LocalPoint.fromWorld((Client)this.client, (int)((int)coords[0]), (int)((int)coords[1]));
        coords[0] = (float)lp.getX() - 64.0f;
        coords[1] = (float)lp.getY() - 64.0f;
    }

    private void calculateLinesToDisplay() {
        if (!this.active || !this.config.showAreaLines()) {
            Arrays.fill(this.linesToDisplay, null);
            return;
        }
        Rectangle sceneRect = new Rectangle(this.client.getBaseX() + 1, this.client.getBaseY() + 1, 102, 102);
        for (int i = 0; i < this.linesToDisplay.length; ++i) {
            GeneralPath lines = new GeneralPath(this.generateSafeArea());
            lines = Geometry.clipPath((GeneralPath)lines, (Shape)sceneRect);
            lines = Geometry.splitIntoSegments((GeneralPath)lines, (float)1.0f);
            this.linesToDisplay[i] = lines = Geometry.transformPath((GeneralPath)lines, this::transformWorldToLocal);
        }
    }

    private void removeTimer() {
        this.infoBoxManager.removeIf(t -> t instanceof AggressionTimer);
        this.endTime = null;
        this.notifyOnce = false;
    }

    private void createTimer(Duration duration) {
        this.removeTimer();
        this.endTime = Instant.now().plus(duration);
        this.notifyOnce = true;
        if (duration.isNegative()) {
            return;
        }
        AsyncBufferedImage image = this.itemManager.getImage(13501);
        this.infoBoxManager.addInfoBox(new AggressionTimer(duration, image, this));
    }

    private void resetTimer() {
        this.createTimer(AGGRESSIVE_TIME_DURATION);
    }

    private static boolean isInWilderness(WorldPoint location) {
        return location.isInArea2D(new WorldArea[]{WILDERNESS_ABOVE_GROUND, WILDERNESS_UNDERGROUND});
    }

    private boolean isNpcMatch(NPC npc) {
        List<NPC> targets;
        NPCComposition composition = npc.getTransformedComposition();
        if (composition == null) {
            return false;
        }
        if (Strings.isNullOrEmpty((String)composition.getName())) {
            return false;
        }
        int playerLvl = this.client.getLocalPlayer().getCombatLevel();
        int npcLvl = composition.getCombatLevel();
        String npcName = composition.getName().toLowerCase();
        if (npcLvl > 0 && playerLvl > npcLvl * 2 && !NpcAggroAreaPlugin.isInWilderness(npc.getWorldLocation())) {
            return false;
        }
        if (this.config.showOnSlayerTask() && (targets = this.slayerPluginService.getTargets()).contains(npc)) {
            return true;
        }
        for (String pattern : this.npcNamePatterns) {
            if (!WildcardMatcher.matches(pattern, npcName)) continue;
            return true;
        }
        return false;
    }

    private void scanNpcs() {
        WorldView wv = this.client.getTopLevelWorldView();
        if (wv == null) {
            return;
        }
        this.active = this.config.alwaysActive();
        if (!this.active) {
            for (NPC npc : wv.npcs()) {
                if (npc == null || !this.isNpcMatch(npc)) continue;
                this.active = true;
                break;
            }
        }
        this.calculateLinesToDisplay();
    }

    @Subscribe(priority=-1.0f)
    public void onNpcSpawned(NpcSpawned event) {
        if (this.active) {
            return;
        }
        if (this.isNpcMatch(event.getNpc())) {
            this.active = true;
            this.calculateLinesToDisplay();
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        WorldPoint newLocation = this.client.getLocalPlayer().getWorldLocation();
        if (this.active && this.notifyOnce && Instant.now().isAfter(this.endTime)) {
            this.notifier.notify(this.config.notifyExpire(), "NPC aggression has expired!");
            this.notifyOnce = false;
        }
        if (this.lastPlayerLocation != null && this.safeCenters[1] == null && newLocation.distanceTo2D(this.lastPlayerLocation) > 40) {
            this.safeCenters[0] = null;
            this.safeCenters[1] = newLocation;
            this.resetTimer();
            this.calculateLinesToDisplay();
            this.previousUnknownCenter = this.lastPlayerLocation;
        }
        if (this.safeCenters[0] == null && this.previousUnknownCenter != null && this.previousUnknownCenter.distanceTo2D(newLocation) <= 20) {
            this.safeCenters[1] = null;
            this.removeTimer();
            this.calculateLinesToDisplay();
        }
        if (this.safeCenters[1] != null && Arrays.stream(this.safeCenters).noneMatch(x -> x != null && x.distanceTo2D(newLocation) <= 10)) {
            this.safeCenters[0] = this.safeCenters[1];
            this.safeCenters[1] = newLocation;
            this.resetTimer();
            this.calculateLinesToDisplay();
            this.previousUnknownCenter = null;
        }
        this.lastPlayerLocation = newLocation;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        String key;
        switch (key = event.getKey()) {
            case "npcUnaggroAlwaysActive": 
            case "showOnSlayerTask": {
                this.clientThread.invokeLater(this::scanNpcs);
                break;
            }
            case "npcUnaggroCollisionDetection": 
            case "npcUnaggroShowAreaLines": {
                this.calculateLinesToDisplay();
                break;
            }
            case "npcUnaggroNames": {
                this.npcNamePatterns = NAME_SPLITTER.splitToList((CharSequence)this.config.npcNamePatterns());
                this.clientThread.invokeLater(this::scanNpcs);
            }
        }
    }

    boolean shouldDisplayTimer() {
        return this.active && this.config.showTimer();
    }

    private void loadConfig() {
        this.safeCenters[0] = (WorldPoint)this.configManager.getRSProfileConfiguration("npcUnaggroArea", "center1", (Type)((Object)WorldPoint.class));
        this.safeCenters[1] = (WorldPoint)this.configManager.getRSProfileConfiguration("npcUnaggroArea", "center2", (Type)((Object)WorldPoint.class));
        this.lastPlayerLocation = (WorldPoint)this.configManager.getRSProfileConfiguration("npcUnaggroArea", "location", (Type)((Object)WorldPoint.class));
        Duration timeLeft = (Duration)this.configManager.getRSProfileConfiguration("npcUnaggroArea", "duration", (Type)((Object)Duration.class));
        if (timeLeft != null) {
            this.createTimer(timeLeft);
        }
    }

    private void resetConfig() {
        this.configManager.unsetRSProfileConfiguration("npcUnaggroArea", "center1");
        this.configManager.unsetRSProfileConfiguration("npcUnaggroArea", "center2");
        this.configManager.unsetRSProfileConfiguration("npcUnaggroArea", "location");
        this.configManager.unsetRSProfileConfiguration("npcUnaggroArea", "duration");
    }

    private void saveConfig() {
        if (this.safeCenters[0] == null || this.safeCenters[1] == null || this.lastPlayerLocation == null || this.endTime == null) {
            this.resetConfig();
        } else {
            this.configManager.setRSProfileConfiguration("npcUnaggroArea", "center1", this.safeCenters[0]);
            this.configManager.setRSProfileConfiguration("npcUnaggroArea", "center2", this.safeCenters[1]);
            this.configManager.setRSProfileConfiguration("npcUnaggroArea", "location", this.lastPlayerLocation);
            this.configManager.setRSProfileConfiguration("npcUnaggroArea", "duration", Duration.between(Instant.now(), this.endTime));
        }
    }

    private void onLogin() {
        this.loadConfig();
        this.resetConfig();
        WorldPoint newLocation = this.client.getLocalPlayer().getWorldLocation();
        assert (newLocation != null);
        if (this.lastPlayerLocation == null || newLocation.distanceTo(this.lastPlayerLocation) != 0) {
            this.safeCenters[0] = null;
            this.safeCenters[1] = null;
            this.lastPlayerLocation = newLocation;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case LOGGED_IN: {
                if (this.loggingIn) {
                    this.loggingIn = false;
                    this.onLogin();
                }
                this.scanNpcs();
                break;
            }
            case LOGGING_IN: {
                this.loggingIn = true;
                break;
            }
            case LOGIN_SCREEN: {
                if (this.lastPlayerLocation != null) {
                    this.saveConfig();
                }
                this.safeCenters[0] = null;
                this.safeCenters[1] = null;
                this.lastPlayerLocation = null;
                this.endTime = null;
            }
        }
    }

    public WorldPoint[] getSafeCenters() {
        return this.safeCenters;
    }

    public GeneralPath[] getLinesToDisplay() {
        return this.linesToDisplay;
    }

    public boolean isActive() {
        return this.active;
    }

    public Instant getEndTime() {
        return this.endTime;
    }
}

