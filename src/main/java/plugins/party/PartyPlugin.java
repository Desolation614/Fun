/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.inject.Binder
 *  com.google.inject.Provides
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.MenuEntry
 *  net.runelite.api.Player
 *  net.runelite.api.Skill
 *  net.runelite.api.Tile
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.CommandExecuted
 *  net.runelite.api.events.FocusChanged
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.MenuOptionClicked
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.party;

import com.google.common.base.Strings;
import com.google.inject.Binder;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.SwingUtilities;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.events.PartyMemberAvatar;
import net.runelite.client.input.KeyManager;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.party.events.UserJoin;
import net.runelite.client.party.events.UserPart;
import net.runelite.client.party.messages.UserSync;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.party.PartyConfig;
import net.runelite.client.plugins.party.PartyPanel;
import net.runelite.client.plugins.party.PartyPingOverlay;
import net.runelite.client.plugins.party.PartyPluginService;
import net.runelite.client.plugins.party.PartyPluginServiceImpl;
import net.runelite.client.plugins.party.PartyStatusOverlay;
import net.runelite.client.plugins.party.PartyWorldMapPoint;
import net.runelite.client.plugins.party.data.PartyData;
import net.runelite.client.plugins.party.data.PartyTilePingData;
import net.runelite.client.plugins.party.messages.LocationUpdate;
import net.runelite.client.plugins.party.messages.StatusUpdate;
import net.runelite.client.plugins.party.messages.TilePing;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Party", configName="PartyPlugin2", description="Party management and basic info", enabledByDefault=false, forceDisabled=true)
public class PartyPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(PartyPlugin.class);
    @Inject
    private Client client;
    @Inject
    private PartyService party;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PartyPingOverlay partyPingOverlay;
    @Inject
    private PartyStatusOverlay partyStatusOverlay;
    @Inject
    private WSClient wsClient;
    @Inject
    private WorldMapPointManager worldMapManager;
    @Inject
    private PartyConfig config;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private KeyManager keyManager;
    @Inject
    @Named(value="developerMode")
    boolean developerMode;
    private final Map<Long, PartyData> partyDataMap = Collections.synchronizedMap(new HashMap());
    private final List<PartyTilePingData> pendingTilePings = Collections.synchronizedList(new ArrayList());
    private Instant lastLogout;
    private PartyPanel panel;
    private NavigationButton navButton;
    private WorldPoint lastLocation;
    private StatusUpdate lastStatus;
    private final HotkeyListener hotkeyListener = new HotkeyListener(() -> this.config.pingHotkey()){

        @Override
        public void hotkeyPressed() {
            PartyPlugin.this.hotkeyPressed = true;
        }

        @Override
        public void hotkeyReleased() {
            PartyPlugin.this.hotkeyPressed = false;
        }
    };
    private boolean hotkeyPressed = false;

    @Override
    public void configure(Binder binder) {
        binder.bind(PartyPluginService.class).to(PartyPluginServiceImpl.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.lastLogout = Instant.now();
        this.panel = (PartyPanel)this.injector.getInstance(PartyPanel.class);
        BufferedImage icon = ImageUtil.loadImageResource(PartyPlugin.class, "panel_icon.png");
        this.navButton = NavigationButton.builder().tooltip("Party").icon(icon).priority(9).panel(this.panel).build();
        this.clientToolbar.addNavigation(this.navButton);
        this.overlayManager.add(this.partyPingOverlay);
        this.overlayManager.add(this.partyStatusOverlay);
        this.keyManager.registerKeyListener(this.hotkeyListener);
        this.wsClient.registerMessage(TilePing.class);
        this.wsClient.registerMessage(LocationUpdate.class);
        this.wsClient.registerMessage(StatusUpdate.class);
        SwingUtilities.invokeLater(this::requestSync);
    }

    @Override
    protected void shutDown() throws Exception {
        this.lastLogout = null;
        this.clientToolbar.removeNavigation(this.navButton);
        this.panel = null;
        this.partyDataMap.clear();
        this.pendingTilePings.clear();
        this.worldMapManager.removeIf(PartyWorldMapPoint.class::isInstance);
        this.overlayManager.remove(this.partyPingOverlay);
        this.overlayManager.remove(this.partyStatusOverlay);
        this.keyManager.unregisterKeyListener(this.hotkeyListener);
        this.wsClient.unregisterMessage(TilePing.class);
        this.wsClient.unregisterMessage(LocationUpdate.class);
        this.wsClient.unregisterMessage(StatusUpdate.class);
        this.lastLocation = null;
        this.lastStatus = null;
    }

    @Provides
    public PartyConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PartyConfig.class);
    }

    @Subscribe
    public void onFocusChanged(FocusChanged focusChanged) {
        if (!focusChanged.isFocused()) {
            this.hotkeyPressed = false;
        }
    }

    void leaveParty() {
        this.party.changeParty(null);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("party")) {
            this.partyStatusOverlay.updateConfig();
            SwingUtilities.invokeLater(this.panel::updateAll);
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (!this.hotkeyPressed || this.client.isMenuOpen() || !this.party.isInParty() || !this.config.pings()) {
            return;
        }
        Tile selectedSceneTile = this.client.getSelectedSceneTile();
        if (selectedSceneTile == null) {
            return;
        }
        boolean isOnCanvas = false;
        for (MenuEntry menuEntry : this.client.getMenuEntries()) {
            if (menuEntry == null || !"walk here".equalsIgnoreCase(menuEntry.getOption())) continue;
            isOnCanvas = true;
        }
        if (!isOnCanvas) {
            return;
        }
        event.consume();
        TilePing tilePing = new TilePing(selectedSceneTile.getWorldLocation());
        this.party.send(tilePing);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN) {
            this.lastLogout = Instant.now();
        }
        this.checkStateChanged(false);
    }

    @Subscribe
    public void onTilePing(TilePing event) {
        if (this.config.pings()) {
            PartyData partyData = this.getPartyData(event.getMemberId());
            Color color = partyData != null ? partyData.getColor() : Color.RED;
            this.pendingTilePings.add(new PartyTilePingData(event.getPoint(), color));
        }
        if (this.config.sounds()) {
            WorldPoint point = event.getPoint();
            if (point.getPlane() != this.client.getPlane() || !WorldPoint.isInScene((Client)this.client, (int)point.getX(), (int)point.getY())) {
                return;
            }
            this.clientThread.invoke(() -> this.client.playSoundEffect(3790));
        }
    }

    @Schedule(period=10L, unit=ChronoUnit.SECONDS)
    public void scheduledTick() {
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.shareLocation();
        } else if (this.client.getGameState() == GameState.LOGIN_SCREEN) {
            this.checkIdle();
        }
    }

    private void shareLocation() {
        if (!this.party.isInParty()) {
            return;
        }
        WorldPoint location = this.client.getLocalPlayer().getWorldLocation();
        if (location.equals((Object)this.lastLocation)) {
            return;
        }
        this.lastLocation = location;
        LocationUpdate locationUpdate = new LocationUpdate(location);
        this.party.send(locationUpdate);
    }

    private void checkIdle() {
        if (this.lastLogout != null && this.lastLogout.isBefore(Instant.now().minus(30L, ChronoUnit.MINUTES)) && this.party.isInParty()) {
            log.info("Leaving party due to inactivity");
            this.party.changeParty(null);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        this.checkStateChanged(false);
    }

    void requestSync() {
        if (this.party.isInParty()) {
            UserSync userSync = new UserSync();
            this.party.send(userSync);
        }
    }

    @Subscribe
    public void onStatusUpdate(StatusUpdate event) {
        PartyData partyData = this.getPartyData(event.getMemberId());
        if (partyData == null) {
            return;
        }
        if (event.getHealthCurrent() != null) {
            partyData.setHitpoints(event.getHealthCurrent());
        }
        if (event.getHealthMax() != null) {
            partyData.setMaxHitpoints(event.getHealthMax());
        }
        if (event.getPrayerCurrent() != null) {
            partyData.setPrayer(event.getPrayerCurrent());
        }
        if (event.getPrayerMax() != null) {
            partyData.setMaxPrayer(event.getPrayerMax());
        }
        if (event.getRunEnergy() != null) {
            partyData.setRunEnergy(event.getRunEnergy());
        }
        if (event.getSpecEnergy() != null) {
            partyData.setSpecEnergy(event.getSpecEnergy());
        }
        if (event.getVengeanceActive() != null) {
            partyData.setVengeanceActive(event.getVengeanceActive());
        }
        if (event.getMemberColor() != null) {
            partyData.setColor(event.getMemberColor());
        }
        PartyMember member = this.party.getMemberById(event.getMemberId());
        if (event.getCharacterName() != null) {
            String name = Text.removeTags(Text.toJagexName(event.getCharacterName()));
            if (!name.isEmpty()) {
                member.setDisplayName(name);
                member.setLoggedIn(true);
            } else {
                member.setLoggedIn(false);
            }
        }
        SwingUtilities.invokeLater(() -> this.panel.updateMember(event.getMemberId()));
    }

    @Subscribe
    public void onLocationUpdate(LocationUpdate event) {
        PartyData partyData = this.getPartyData(event.getMemberId());
        if (partyData == null) {
            return;
        }
        partyData.getWorldMapPoint().setWorldPoint(event.getWorldPoint());
    }

    @Subscribe
    public void onUserJoin(UserJoin event) {
        this.getPartyData(event.getMemberId());
    }

    @Subscribe
    public void onUserSync(UserSync event) {
        this.clientThread.invokeLater(() -> this.checkStateChanged(true));
        this.lastLocation = null;
    }

    private void checkStateChanged(boolean forceSend) {
        if (this.lastStatus == null) {
            forceSend = true;
        }
        if (!this.party.isInParty()) {
            return;
        }
        int healthCurrent = this.client.getBoostedSkillLevel(Skill.HITPOINTS);
        int prayerCurrent = this.client.getBoostedSkillLevel(Skill.PRAYER);
        int healthMax = this.client.getRealSkillLevel(Skill.HITPOINTS);
        int prayerMax = this.client.getRealSkillLevel(Skill.PRAYER);
        int runEnergy = (int)Math.ceil((double)this.client.getEnergy() / 1000.0) * 10;
        int specEnergy = this.client.getVarpValue(300) / 10;
        boolean vengActive = this.client.getVarbitValue(2450) == 1;
        Color memberColor = this.getLocalMemberColor();
        Player localPlayer = this.client.getLocalPlayer();
        String characterName = Strings.nullToEmpty(localPlayer != null && this.client.getGameState().getState() >= GameState.LOADING.getState() ? localPlayer.getName() : null);
        boolean hasChange = false;
        boolean canDelay = !forceSend;
        StatusUpdate update = new StatusUpdate();
        if (forceSend || !characterName.equals(this.lastStatus.getCharacterName())) {
            hasChange = true;
            update.setCharacterName(characterName);
        }
        if (forceSend || healthCurrent != this.lastStatus.getHealthCurrent()) {
            hasChange = true;
            update.setHealthCurrent(healthCurrent);
        }
        if (forceSend || healthMax != this.lastStatus.getHealthMax()) {
            hasChange = true;
            update.setHealthMax(healthMax);
        }
        if (forceSend || prayerCurrent != this.lastStatus.getPrayerCurrent()) {
            hasChange = true;
            update.setPrayerCurrent(prayerCurrent);
        }
        if (forceSend || prayerMax != this.lastStatus.getPrayerMax()) {
            hasChange = true;
            update.setPrayerMax(prayerMax);
        }
        if (forceSend || runEnergy != this.lastStatus.getRunEnergy()) {
            hasChange = true;
            update.setRunEnergy(runEnergy);
        }
        if (forceSend || specEnergy != this.lastStatus.getSpecEnergy()) {
            hasChange = true;
            canDelay = !forceSend && specEnergy - this.lastStatus.getSpecEnergy() == 10;
            update.setSpecEnergy(specEnergy);
        }
        if (forceSend || vengActive != this.lastStatus.getVengeanceActive()) {
            hasChange = true;
            update.setVengeanceActive(vengActive);
        }
        if (forceSend || !Objects.equals(memberColor, this.lastStatus.getMemberColor())) {
            hasChange = true;
            update.setMemberColor(memberColor);
        }
        if (canDelay && this.client.getTickCount() % PartyPlugin.messageFreq(this.party.getMembers().size()) != 0) {
            return;
        }
        if (hasChange) {
            this.party.send(update);
            this.lastStatus = new StatusUpdate(characterName, healthCurrent, healthMax, prayerCurrent, prayerMax, runEnergy, specEnergy, vengActive, memberColor);
        }
    }

    @Subscribe
    public void onUserPart(UserPart event) {
        PartyData removed = this.partyDataMap.remove(event.getMemberId());
        if (removed != null) {
            this.worldMapManager.remove(removed.getWorldMapPoint());
            SwingUtilities.invokeLater(() -> this.panel.removeMember(event.getMemberId()));
        }
    }

    @Subscribe
    public void onPartyChanged(PartyChanged event) {
        this.partyDataMap.clear();
        this.pendingTilePings.clear();
        this.worldMapManager.removeIf(PartyWorldMapPoint.class::isInstance);
        if (event.getPartyId() != null) {
            this.config.setPreviousPartyId(event.getPassphrase());
        }
        SwingUtilities.invokeLater(this.panel::removeAllMembers);
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted commandExecuted) {
        if (!this.developerMode || !commandExecuted.getCommand().equalsIgnoreCase("partyinfo")) {
            return;
        }
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.GAMEMESSAGE).value("Party " + this.party.getPartyPassphrase() + " ID " + this.party.getPartyId()).build());
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.GAMEMESSAGE).value("Local ID " + this.party.getLocalMember().getMemberId()).build());
        for (PartyMember partyMember : this.party.getMembers()) {
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.GAMEMESSAGE).value("Member " + partyMember.getDisplayName() + " " + partyMember.getMemberId()).build());
        }
    }

    @Subscribe
    public void onPartyMemberAvatar(PartyMemberAvatar event) {
        SwingUtilities.invokeLater(() -> this.panel.updateMember(event.getMemberId()));
    }

    @Nullable
    PartyData getPartyData(long uuid) {
        PartyMember memberById = this.party.getMemberById(uuid);
        if (memberById == null) {
            return null;
        }
        return this.partyDataMap.computeIfAbsent(uuid, u -> {
            boolean isSelf;
            PartyWorldMapPoint worldMapPoint = new PartyWorldMapPoint(new WorldPoint(0, 0, 0), memberById);
            PartyMember partyMember = this.party.getLocalMember();
            boolean bl = isSelf = partyMember != null && u.equals(partyMember.getMemberId());
            if (!isSelf) {
                this.worldMapManager.add(worldMapPoint);
            }
            PartyData partyData = new PartyData(uuid, worldMapPoint);
            SwingUtilities.invokeLater(() -> this.panel.addMember(partyData));
            return partyData;
        });
    }

    private Color getLocalMemberColor() {
        Color memberColor = this.config.memberColor();
        if (memberColor == null) {
            PartyMember local = this.party.getLocalMember();
            if (local == null) {
                return null;
            }
            String localName = local.getDisplayName();
            memberColor = ColorUtil.fromObject(localName);
            log.debug("Computed member color {} for {}", (Object)memberColor, (Object)localName);
            this.config.setMemberColor(memberColor);
        }
        return memberColor;
    }

    private static int messageFreq(int partySize) {
        return Math.max(1, partySize - 6);
    }

    public Map<Long, PartyData> getPartyDataMap() {
        return this.partyDataMap;
    }

    public List<PartyTilePingData> getPendingTilePings() {
        return this.pendingTilePings;
    }
}

