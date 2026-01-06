/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.inject.Binder
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.InstanceTemplates
 *  net.runelite.api.MessageNode
 *  net.runelite.api.Point
 *  net.runelite.api.Tile
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.http.api.chat.LayoutRoom
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.raids;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.inject.Binder;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InstanceTemplates;
import net.runelite.api.MessageNode;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatClient;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ChatInput;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.raids.Raid;
import net.runelite.client.plugins.raids.RaidRoom;
import net.runelite.client.plugins.raids.RaidsConfig;
import net.runelite.client.plugins.raids.RaidsOverlay;
import net.runelite.client.plugins.raids.RaidsTimer;
import net.runelite.client.plugins.raids.RoomType;
import net.runelite.client.plugins.raids.RotationSolver;
import net.runelite.client.plugins.raids.events.RaidReset;
import net.runelite.client.plugins.raids.events.RaidScouted;
import net.runelite.client.plugins.raids.solver.Layout;
import net.runelite.client.plugins.raids.solver.LayoutSolver;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageCapture;
import net.runelite.client.util.Text;
import net.runelite.http.api.chat.LayoutRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Chambers Of Xeric", description="Show helpful information for the Chambers of Xeric raid", tags={"combat", "raid", "overlay", "pve", "pvm", "bosses", "cox"})
public class RaidsPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(RaidsPlugin.class);
    private static final int LOBBY_PLANE = 3;
    private static final int SECOND_FLOOR_PLANE = 2;
    private static final int ROOMS_PER_PLANE = 8;
    private static final int AMOUNT_OF_ROOMS_PER_X_AXIS_PER_PLANE = 4;
    private static final String RAID_START_MESSAGE = "The raid has begun!";
    private static final String LEVEL_COMPLETE_MESSAGE = "level complete!";
    private static final String RAID_COMPLETE_MESSAGE = "Congratulations - your raid is complete!";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.##");
    private static final DecimalFormat POINTS_FORMAT = new DecimalFormat("#,###");
    private static final String LAYOUT_COMMAND = "!layout";
    private static final int MAX_LAYOUT_LEN = 300;
    @Inject
    private RuneLiteConfig runeLiteConfig;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private Client client;
    @Inject
    private RaidsConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private RaidsOverlay overlay;
    @Inject
    private LayoutSolver layoutSolver;
    @Inject
    private SpriteManager spriteManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ChatCommandManager chatCommandManager;
    @Inject
    private ChatClient chatClient;
    @Inject
    private ScheduledExecutorService scheduledExecutorService;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ImageCapture imageCapture;
    @Inject
    private EventBus eventBus;
    private final Set<String> roomWhitelist = new HashSet<String>();
    private final Set<String> roomBlacklist = new HashSet<String>();
    private final Set<String> rotationWhitelist = new HashSet<String>();
    private final Set<String> layoutWhitelist = new HashSet<String>();
    private Raid raid;
    private boolean inRaidChambers;
    private int raidPartyID;
    private RaidsTimer timer;
    private final HotkeyListener screenshotHotkeyListener = new HotkeyListener(() -> this.config.screenshotHotkey()){

        @Override
        public void hotkeyPressed() {
            RaidsPlugin.this.clientThread.invoke(RaidsPlugin.this::screenshotScoutOverlay);
        }
    };

    @Provides
    RaidsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(RaidsConfig.class);
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(RaidsOverlay.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.updateLists();
        this.clientThread.invokeLater(this::scoutRaid);
        this.chatCommandManager.registerCommandAsync(LAYOUT_COMMAND, this::lookupRaid, this::submitRaid);
        this.keyManager.registerKeyListener(this.screenshotHotkeyListener);
    }

    @Override
    protected void shutDown() throws Exception {
        this.chatCommandManager.unregisterCommand(LAYOUT_COMMAND);
        this.overlayManager.remove(this.overlay);
        this.infoBoxManager.removeInfoBox(this.timer);
        this.timer = null;
        this.inRaidChambers = false;
        this.reset();
        this.keyManager.unregisterKeyListener(this.screenshotHotkeyListener);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("raids")) {
            return;
        }
        if (event.getKey().equals("raidsTimer")) {
            this.updateInfoBoxState();
            return;
        }
        this.updateLists();
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        boolean inRaid;
        if (event.getVarpId() == 1427) {
            inRaid = this.inRaidChambers;
            int prevRaidID = this.raidPartyID;
            this.raidPartyID = event.getValue();
            if (this.client.getGameState() == GameState.LOGGED_IN && (!inRaid || prevRaidID != -1 && this.raidPartyID != -1 && prevRaidID != this.raidPartyID)) {
                log.debug("Raid party has been dissolved");
                this.reset();
            }
        }
        if (event.getVarbitId() == 5432) {
            this.inRaidChambers = inRaid = event.getValue() == 1;
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                if (inRaid) {
                    this.scoutRaid();
                } else if (this.raidPartyID == -1) {
                    log.debug("Raid has ended");
                    this.reset();
                }
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (this.inRaidChambers && event.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION) {
            String message = Text.removeTags(event.getMessage());
            if (this.config.raidsTimer() && message.startsWith(RAID_START_MESSAGE)) {
                this.timer = new RaidsTimer(this, Instant.now(), this.config);
                this.spriteManager.getSpriteAsync(1582, 0, this.timer);
                this.infoBoxManager.addInfoBox(this.timer);
            }
            if (this.timer != null && message.contains(LEVEL_COMPLETE_MESSAGE)) {
                this.timer.timeFloor();
            }
            if (message.startsWith(RAID_COMPLETE_MESSAGE)) {
                if (this.timer != null) {
                    this.timer.timeOlm();
                    this.timer.setStopped(true);
                }
                if (this.config.pointsMessage()) {
                    int totalPoints = this.client.getVarbitValue(5431);
                    int personalPoints = this.client.getVarpValue(4609);
                    double percentage = (double)personalPoints / ((double)totalPoints / 100.0);
                    String chatMessage = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Total points: ").append(ChatColorType.HIGHLIGHT).append(POINTS_FORMAT.format(totalPoints)).append(ChatColorType.NORMAL).append(", Personal points: ").append(ChatColorType.HIGHLIGHT).append(POINTS_FORMAT.format(personalPoints)).append(ChatColorType.NORMAL).append(" (").append(ChatColorType.HIGHLIGHT).append(DECIMAL_FORMAT.format(percentage)).append(ChatColorType.NORMAL).append("%)").build();
                    this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.FRIENDSCHATNOTIFICATION).runeLiteFormattedMessage(chatMessage).build());
                }
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.scoutRaid();
        }
    }

    private void scoutRaid() {
        if (this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        boolean bl = this.inRaidChambers = this.client.getVarbitValue(5432) == 1;
        if (!this.inRaidChambers) {
            return;
        }
        boolean firstSolve = this.raid == null;
        this.raid = this.buildRaid(this.raid);
        if (this.raid == null) {
            log.debug("Failed to build raid");
            return;
        }
        if (this.raid.getLayout() == null) {
            Layout layout = this.layoutSolver.findLayout(this.raid.toCode());
            if (layout == null) {
                log.debug("Could not find layout match");
                this.raid = null;
                return;
            }
            this.raid.updateLayout(layout);
        }
        RaidRoom[] rooms = this.raid.getCombatRooms();
        RotationSolver.solve(rooms);
        this.raid.setCombatRooms(rooms);
        if (this.config.layoutMessage() && firstSolve) {
            this.sendRaidLayoutMessage();
        }
        this.eventBus.post(new RaidScouted(this.raid, firstSolve));
    }

    private void sendRaidLayoutMessage() {
        String layout = this.getRaid().getLayout().toCodeString();
        String rooms = this.getRaid().toRoomString();
        String raidData = "[" + layout + "]: " + rooms;
        String layoutMessage = new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append("Layout: ").append(ChatColorType.NORMAL).append(raidData).build();
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.FRIENDSCHATNOTIFICATION).runeLiteFormattedMessage(layoutMessage).build());
    }

    private void updateInfoBoxState() {
        if (this.timer != null && !this.inRaidChambers) {
            this.infoBoxManager.removeInfoBox(this.timer);
            this.timer = null;
        }
    }

    @VisibleForTesting
    void updateLists() {
        this.updateList(this.roomWhitelist, this.config.whitelistedRooms());
        this.updateList(this.roomBlacklist, this.config.blacklistedRooms());
        this.updateList(this.layoutWhitelist, this.config.whitelistedLayouts());
        this.rotationWhitelist.clear();
        for (String line : this.config.whitelistedRotations().split("\\n")) {
            this.rotationWhitelist.add(line.toLowerCase().replace(" ", ""));
        }
    }

    private void updateList(Collection<String> list, String input) {
        list.clear();
        for (String s : Text.fromCSV(input.toLowerCase())) {
            if (s.equals("unknown")) {
                list.add("unknown (combat)");
                list.add("unknown (puzzle)");
                continue;
            }
            list.add(s);
        }
    }

    boolean getRotationMatches() {
        RaidRoom[] combatRooms = this.raid.getCombatRooms();
        String rotation = Arrays.stream(combatRooms).map(RaidRoom::getName).map(String::toLowerCase).collect(Collectors.joining(","));
        return this.rotationWhitelist.contains(rotation);
    }

    private Point findLobbyBase() {
        Tile[][] tiles = this.client.getScene().getTiles()[3];
        for (int x = 0; x < 104; ++x) {
            for (int y = 0; y < 104; ++y) {
                if (tiles[x][y] == null || tiles[x][y].getWallObject() == null || tiles[x][y].getWallObject().getId() != 12231) continue;
                return tiles[x][y].getSceneLocation();
            }
        }
        return null;
    }

    private Raid buildRaid(Raid from) {
        Raid raid = from;
        if (raid == null) {
            Point gridBase = this.findLobbyBase();
            if (gridBase == null) {
                return null;
            }
            Integer lobbyIndex = this.findLobbyIndex(gridBase);
            if (lobbyIndex == null) {
                return null;
            }
            raid = new Raid(new WorldPoint(this.client.getBaseX() + gridBase.getX(), this.client.getBaseY() + gridBase.getY(), 3), lobbyIndex);
        }
        int baseX = raid.getLobbyIndex() % 4;
        int baseY = raid.getLobbyIndex() % 8 > 3 ? 1 : 0;
        for (int i = 0; i < raid.getRooms().length; ++i) {
            Tile tile;
            int x = i % 4;
            int y = i % 8 > 3 ? 1 : 0;
            int plane = i > 7 ? 2 : 3;
            x -= baseX;
            y -= baseY;
            x = raid.getGridBase().getX() + x * 32;
            y = raid.getGridBase().getY() - y * 32;
            y -= this.client.getBaseY();
            if ((x -= this.client.getBaseX()) < -31 || x >= 104) continue;
            if (x < 1) {
                x = 1;
            }
            if (y < 1) {
                y = 1;
            }
            if ((tile = this.client.getScene().getTiles()[plane][x][y]) == null) continue;
            RaidRoom room = this.determineRoom(tile);
            raid.setRoom(room, i);
        }
        return raid;
    }

    private RaidRoom determineRoom(Tile base) {
        int chunkData = this.client.getInstanceTemplateChunks()[base.getPlane()][base.getSceneLocation().getX() / 8][base.getSceneLocation().getY() / 8];
        InstanceTemplates template = InstanceTemplates.findMatch((int)chunkData);
        if (template == null) {
            return RaidRoom.EMPTY;
        }
        switch (template) {
            case RAIDS_LOBBY: 
            case RAIDS_START: {
                return RaidRoom.START;
            }
            case RAIDS_END: {
                return RaidRoom.END;
            }
            case RAIDS_SCAVENGERS: 
            case RAIDS_SCAVENGERS2: {
                return RaidRoom.SCAVENGERS;
            }
            case RAIDS_SHAMANS: {
                return RaidRoom.SHAMANS;
            }
            case RAIDS_VASA: {
                return RaidRoom.VASA;
            }
            case RAIDS_VANGUARDS: {
                return RaidRoom.VANGUARDS;
            }
            case RAIDS_ICE_DEMON: {
                return RaidRoom.ICE_DEMON;
            }
            case RAIDS_THIEVING: {
                return RaidRoom.THIEVING;
            }
            case RAIDS_FARMING: 
            case RAIDS_FARMING2: {
                return RaidRoom.FARMING;
            }
            case RAIDS_MUTTADILES: {
                return RaidRoom.MUTTADILES;
            }
            case RAIDS_MYSTICS: {
                return RaidRoom.MYSTICS;
            }
            case RAIDS_TEKTON: {
                return RaidRoom.TEKTON;
            }
            case RAIDS_TIGHTROPE: {
                return RaidRoom.TIGHTROPE;
            }
            case RAIDS_GUARDIANS: {
                return RaidRoom.GUARDIANS;
            }
            case RAIDS_CRABS: {
                return RaidRoom.CRABS;
            }
            case RAIDS_VESPULA: {
                return RaidRoom.VESPULA;
            }
        }
        return RaidRoom.EMPTY;
    }

    private void lookupRaid(ChatMessage chatMessage, String s) {
        LayoutRoom[] layout;
        ChatMessageType type = chatMessage.getType();
        String player = type.equals((Object)ChatMessageType.PRIVATECHATOUT) ? this.client.getLocalPlayer().getName() : Text.sanitize(chatMessage.getName());
        try {
            layout = this.chatClient.getLayout(player);
        }
        catch (IOException ex) {
            log.debug("unable to lookup layout", (Throwable)ex);
            return;
        }
        if (layout == null || layout.length == 0) {
            return;
        }
        String layoutMessage = Joiner.on((String)", ").join(Arrays.stream(layout).map(l -> RaidRoom.valueOf(l.name())).filter(room -> room.getType() == RoomType.COMBAT || room.getType() == RoomType.PUZZLE).map(RaidRoom::getName).toArray());
        if (layoutMessage.length() > 300) {
            log.debug("layout message too long! {}", (Object)layoutMessage.length());
            return;
        }
        String response = new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append("Layout: ").append(ChatColorType.NORMAL).append(layoutMessage).build();
        log.debug("Setting response {}", (Object)response);
        MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setRuneLiteFormatMessage(response);
        this.client.refreshChat();
    }

    private boolean submitRaid(ChatInput chatInput, String s) {
        if (this.raid == null) {
            return false;
        }
        String playerName = this.client.getLocalPlayer().getName();
        List<RaidRoom> orderedRooms = this.raid.getOrderedRooms();
        LayoutRoom[] layoutRooms = (LayoutRoom[])orderedRooms.stream().map(room -> LayoutRoom.valueOf((String)room.name())).toArray(LayoutRoom[]::new);
        this.scheduledExecutorService.execute(() -> {
            try {
                this.chatClient.submitLayout(playerName, layoutRooms);
            }
            catch (Exception ex) {
                log.warn("unable to submit layout", (Throwable)ex);
            }
            finally {
                chatInput.resume();
            }
        });
        return true;
    }

    void screenshotScoutOverlay() {
        if (!this.overlay.isScoutOverlayShown()) {
            return;
        }
        Rectangle overlayDimensions = this.overlay.getBounds();
        BufferedImage overlayImage = new BufferedImage(overlayDimensions.width, overlayDimensions.height, 2);
        Graphics2D graphic = overlayImage.createGraphics();
        graphic.setFont(this.runeLiteConfig.interfaceFontType().getFont());
        graphic.setColor(Color.BLACK);
        graphic.fillRect(0, 0, overlayDimensions.width, overlayDimensions.height);
        this.overlay.render(graphic);
        this.imageCapture.saveScreenshot(overlayImage, "CoX_scout", null, false, this.config.copyToClipboard());
        graphic.dispose();
    }

    private Integer findLobbyIndex(Point gridBase) {
        int x;
        if (104 <= gridBase.getX() + 32 || 104 <= gridBase.getY() + 32) {
            return null;
        }
        Tile[][] tiles = this.client.getScene().getTiles()[3];
        int y = tiles[gridBase.getX()][gridBase.getY() + 32] == null ? 0 : 1;
        if (tiles[gridBase.getX() + 32][gridBase.getY()] == null) {
            x = 3;
        } else {
            int sceneX;
            for (x = 0; x < 3 && (sceneX = gridBase.getX() - 1 - 32 * x) >= 0 && tiles[sceneX][gridBase.getY()] != null; ++x) {
            }
        }
        return x + y * 4;
    }

    private void reset() {
        this.raid = null;
        this.updateInfoBoxState();
        this.eventBus.post(new RaidReset());
    }

    public Set<String> getRoomWhitelist() {
        return this.roomWhitelist;
    }

    public Set<String> getRoomBlacklist() {
        return this.roomBlacklist;
    }

    public Set<String> getRotationWhitelist() {
        return this.rotationWhitelist;
    }

    public Set<String> getLayoutWhitelist() {
        return this.layoutWhitelist;
    }

    void setRaid(Raid raid) {
        this.raid = raid;
    }

    public Raid getRaid() {
        return this.raid;
    }

    public boolean isInRaidChambers() {
        return this.inRaidChambers;
    }

    public int getRaidPartyID() {
        return this.raidPartyID;
    }
}

