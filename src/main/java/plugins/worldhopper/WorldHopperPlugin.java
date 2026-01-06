/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.ImmutableList
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.ChatPlayer
 *  net.runelite.api.Client
 *  net.runelite.api.EnumComposition
 *  net.runelite.api.FriendContainer
 *  net.runelite.api.FriendsChatManager
 *  net.runelite.api.FriendsChatMember
 *  net.runelite.api.GameState
 *  net.runelite.api.MenuAction
 *  net.runelite.api.World
 *  net.runelite.api.clan.ClanChannel
 *  net.runelite.api.clan.ClanChannelMember
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.CommandExecuted
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.MenuEntryAdded
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.events.WorldListLoad
 *  net.runelite.api.widgets.WidgetUtil
 *  net.runelite.http.api.worlds.World
 *  net.runelite.http.api.worlds.WorldResult
 *  net.runelite.http.api.worlds.WorldType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.worldhopper;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ChatPlayer;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.FriendContainer;
import net.runelite.api.FriendsChatManager;
import net.runelite.api.FriendsChatMember;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.World;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WorldListLoad;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.WorldsFetch;
import net.runelite.client.game.WorldService;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.worldhopper.RegionFilterMode;
import net.runelite.client.plugins.worldhopper.WorldHopperConfig;
import net.runelite.client.plugins.worldhopper.WorldHopperPingOverlay;
import net.runelite.client.plugins.worldhopper.WorldSwitcherPanel;
import net.runelite.client.plugins.worldhopper.ping.Ping;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ExecutorServiceExceptionLogger;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="World Hopper", description="Allows you to quickly hop worlds", tags={"ping", "switcher"}, forceDisabled=true)
public class WorldHopperPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(WorldHopperPlugin.class);
    private static final int REFRESH_THROTTLE = 60000;
    private static final int MAX_PLAYER_COUNT = 1950;
    private static final int DISPLAY_SWITCHER_MAX_ATTEMPTS = 3;
    private static final String HOP_TO = "Hop-to";
    private static final String KICK_OPTION = "Kick";
    private static final ImmutableList<String> BEFORE_OPTIONS = ImmutableList.of((Object)"Add friend", (Object)"Remove friend", (Object)"Kick");
    private static final ImmutableList<String> AFTER_OPTIONS = ImmutableList.of((Object)"Message");
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private WorldHopperConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WorldHopperPingOverlay worldHopperOverlay;
    @Inject
    private WorldService worldService;
    private ScheduledExecutorService hopperExecutorService;
    private NavigationButton navButton;
    private WorldSwitcherPanel panel;
    private World quickHopTargetWorld;
    private int displaySwitcherAttempts = 0;
    private int lastWorld;
    private int favoriteWorld1;
    private int favoriteWorld2;
    private ScheduledFuture<?> pingFuture;
    private ScheduledFuture<?> currPingFuture;
    private int currentWorld;
    private Instant lastFetch;
    private int currentPing;
    private final Map<Integer, Integer> storedPings = new HashMap<Integer, Integer>();
    private final HotkeyListener previousKeyListener = new HotkeyListener(() -> this.config.previousKey()){

        @Override
        public void hotkeyPressed() {
            WorldHopperPlugin.this.clientThread.invoke(() -> WorldHopperPlugin.this.hop(true));
        }
    };
    private final HotkeyListener nextKeyListener = new HotkeyListener(() -> this.config.nextKey()){

        @Override
        public void hotkeyPressed() {
            WorldHopperPlugin.this.clientThread.invoke(() -> WorldHopperPlugin.this.hop(false));
        }
    };

    @Provides
    WorldHopperConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(WorldHopperConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.currentPing = -1;
        this.keyManager.registerKeyListener(this.previousKeyListener);
        this.keyManager.registerKeyListener(this.nextKeyListener);
        this.panel = new WorldSwitcherPanel(this);
        BufferedImage icon = ImageUtil.loadImageResource(WorldHopperPlugin.class, "icon.png");
        this.navButton = NavigationButton.builder().tooltip("World Switcher").icon(icon).priority(3).panel(this.panel).build();
        if (this.config.showSidebar()) {
            this.clientToolbar.addNavigation(this.navButton);
        }
        this.overlayManager.add(this.worldHopperOverlay);
        this.panel.setSubscriptionFilterMode(this.config.subscriptionFilter());
        this.panel.setRegionFilterMode(this.config.regionFilter());
        this.panel.setWorldTypeFilters(this.config.worldTypeFilter());
        this.hopperExecutorService = new ExecutorServiceExceptionLogger(Executors.newSingleThreadScheduledExecutor());
        this.hopperExecutorService.execute(this::updateList);
        this.hopperExecutorService.execute(this::pingInitialWorlds);
        this.pingFuture = this.hopperExecutorService.scheduleWithFixedDelay(this::pingNextWorld, 15L, 3L, TimeUnit.SECONDS);
        this.currPingFuture = this.hopperExecutorService.scheduleWithFixedDelay(this::pingCurrentWorld, 15L, 1L, TimeUnit.SECONDS);
    }

    @Override
    protected void shutDown() throws Exception {
        this.pingFuture.cancel(true);
        this.pingFuture = null;
        this.currPingFuture.cancel(true);
        this.currPingFuture = null;
        this.overlayManager.remove(this.worldHopperOverlay);
        this.keyManager.unregisterKeyListener(this.previousKeyListener);
        this.keyManager.unregisterKeyListener(this.nextKeyListener);
        this.clientToolbar.removeNavigation(this.navButton);
        this.hopperExecutorService.shutdown();
        this.hopperExecutorService = null;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("worldhopper")) {
            switch (event.getKey()) {
                case "showSidebar": {
                    if (this.config.showSidebar()) {
                        this.clientToolbar.addNavigation(this.navButton);
                        break;
                    }
                    this.clientToolbar.removeNavigation(this.navButton);
                    break;
                }
                case "ping": {
                    if (this.config.ping()) {
                        SwingUtilities.invokeLater(() -> this.panel.showPing());
                        break;
                    }
                    SwingUtilities.invokeLater(() -> this.panel.hidePing());
                    break;
                }
                case "subscriptionFilter": {
                    this.panel.setSubscriptionFilterMode(this.config.subscriptionFilter());
                    this.updateList();
                    break;
                }
                case "regionFilter": {
                    this.panel.setRegionFilterMode(this.config.regionFilter());
                    this.updateList();
                    break;
                }
                case "worldTypeFilter": {
                    this.panel.setWorldTypeFilters(this.config.worldTypeFilter());
                    this.updateList();
                }
            }
        }
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted commandExecuted) {
        if ("hop".equalsIgnoreCase(commandExecuted.getCommand())) {
            int worldNumber;
            try {
                String[] arguments = commandExecuted.getArguments();
                worldNumber = Integer.parseInt(arguments[0]);
            }
            catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
                this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).value("Usage: ::hop world").build());
                return;
            }
            net.runelite.http.api.worlds.World world = this.worldService.getWorlds().findWorld(worldNumber);
            if (world == null) {
                this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).value("Unknown world " + worldNumber).build());
                return;
            }
            this.hop(world);
        }
    }

    private void setFavoriteConfig(int world) {
        this.configManager.setConfiguration("worldhopper", "favorite_" + world, true);
    }

    private boolean isFavoriteConfig(int world) {
        Boolean favorite = (Boolean)this.configManager.getConfiguration("worldhopper", "favorite_" + world, (Type)((Object)Boolean.class));
        return favorite != null && favorite != false;
    }

    private void clearFavoriteConfig(int world) {
        this.configManager.unsetConfiguration("worldhopper", "favorite_" + world);
    }

    boolean isFavorite(net.runelite.http.api.worlds.World world) {
        int id = world.getId();
        return id == this.favoriteWorld1 || id == this.favoriteWorld2 || this.isFavoriteConfig(id);
    }

    int getCurrentWorld() {
        return this.client.getWorld();
    }

    void addToFavorites(net.runelite.http.api.worlds.World world) {
        log.debug("Adding world {} to favorites", (Object)world.getId());
        this.setFavoriteConfig(world.getId());
        this.panel.updateFavoriteMenu(world.getId(), true);
    }

    void removeFromFavorites(net.runelite.http.api.worlds.World world) {
        log.debug("Removing world {} from favorites", (Object)world.getId());
        this.clearFavoriteConfig(world.getId());
        this.panel.updateFavoriteMenu(world.getId(), false);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (varbitChanged.getVarbitId() == 4597 || varbitChanged.getVarbitId() == 4598) {
            this.favoriteWorld1 = this.client.getVarbitValue(4597);
            this.favoriteWorld2 = this.client.getVarbitValue(4598);
            SwingUtilities.invokeLater(this.panel::updateList);
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (!this.config.menuOption()) {
            return;
        }
        int componentId = event.getActionParam1();
        int groupId = WidgetUtil.componentToInterface((int)componentId);
        String option = event.getOption();
        if (groupId == 429 || groupId == 7 || componentId == 45940742 || componentId == 46006278) {
            boolean after;
            if (AFTER_OPTIONS.contains((Object)option)) {
                after = true;
            } else if (BEFORE_OPTIONS.contains((Object)option)) {
                after = false;
            } else {
                return;
            }
            ChatPlayer player = this.getChatPlayerFromName(event.getTarget());
            WorldResult worldResult = this.worldService.getWorlds();
            if (player == null || player.getWorld() == 0 || player.getWorld() == this.client.getWorld() || worldResult == null) {
                return;
            }
            net.runelite.http.api.worlds.World currentWorld = worldResult.findWorld(this.client.getWorld());
            net.runelite.http.api.worlds.World targetWorld = worldResult.findWorld(player.getWorld());
            if (targetWorld == null || currentWorld == null || !currentWorld.getTypes().contains(WorldType.PVP) && targetWorld.getTypes().contains(WorldType.PVP)) {
                return;
            }
            this.client.createMenuEntry(after ? -2 : -1).setOption(HOP_TO).setTarget(event.getTarget()).setType(MenuAction.RUNELITE).onClick(e -> {
                ChatPlayer p = this.getChatPlayerFromName(e.getTarget());
                if (p != null) {
                    this.hop(p.getWorld());
                }
            });
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (this.config.showSidebar() && gameStateChanged.getGameState() == GameState.LOGGED_IN && this.lastWorld != this.client.getWorld()) {
            int newWorld = this.client.getWorld();
            this.panel.switchCurrentHighlight(newWorld, this.lastWorld);
            this.lastWorld = newWorld;
        }
    }

    @Subscribe
    public void onWorldListLoad(WorldListLoad worldListLoad) {
        if (!this.config.showSidebar()) {
            return;
        }
        HashMap<Integer, Integer> worldData = new HashMap<Integer, Integer>();
        for (World w : worldListLoad.getWorlds()) {
            worldData.put(w.getId(), w.getPlayerCount());
        }
        this.panel.updateListData(worldData);
        this.lastFetch = Instant.now();
    }

    void refresh() {
        Instant now = Instant.now();
        if (this.lastFetch != null && now.toEpochMilli() - this.lastFetch.toEpochMilli() < 60000L) {
            log.debug("Throttling world refresh");
            return;
        }
        this.lastFetch = now;
        this.worldService.refresh();
    }

    @Subscribe
    public void onWorldsFetch(WorldsFetch worldsFetch) {
        this.updateList();
    }

    private void updateList() {
        WorldResult worldResult = this.worldService.getWorlds();
        if (worldResult != null) {
            this.clientThread.invokeLater(() -> {
                EnumComposition locationEnum = this.client.getGameState().getState() >= GameState.LOGIN_SCREEN.getState() ? this.client.getEnum(4992) : null;
                SwingUtilities.invokeLater(() -> this.panel.populate(worldResult.getWorlds(), locationEnum));
            });
        }
    }

    private void hop(boolean previous) {
        net.runelite.http.api.worlds.World world;
        WorldResult worldResult = this.worldService.getWorlds();
        if (worldResult == null || this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        net.runelite.http.api.worlds.World currentWorld = worldResult.findWorld(this.client.getWorld());
        if (currentWorld == null) {
            return;
        }
        Object currentWorldTypes = currentWorld.getTypes().clone();
        if (this.config.quickhopOutOfDanger()) {
            ((AbstractCollection)currentWorldTypes).remove(WorldType.PVP);
            ((AbstractCollection)currentWorldTypes).remove(WorldType.HIGH_RISK);
        }
        ((AbstractCollection)currentWorldTypes).remove(WorldType.BOUNTY);
        ((AbstractCollection)currentWorldTypes).remove(WorldType.SKILL_TOTAL);
        ((AbstractCollection)currentWorldTypes).remove(WorldType.LAST_MAN_STANDING);
        List worlds = worldResult.getWorlds();
        int worldIdx = worlds.indexOf(currentWorld);
        int totalLevel = this.client.getTotalLevel();
        Set<RegionFilterMode> regionFilter = this.config.regionFilter();
        do {
            if (previous) {
                if (--worldIdx < 0) {
                    worldIdx = worlds.size() - 1;
                }
            } else if (++worldIdx >= worlds.size()) {
                worldIdx = 0;
            }
            world = (net.runelite.http.api.worlds.World)worlds.get(worldIdx);
            if (!regionFilter.isEmpty() && !regionFilter.contains((Object)RegionFilterMode.of(world.getRegion()))) continue;
            Object types = world.getTypes().clone();
            ((AbstractCollection)types).remove(WorldType.BOUNTY);
            ((AbstractCollection)types).remove(WorldType.LAST_MAN_STANDING);
            if (((AbstractCollection)types).contains(WorldType.SKILL_TOTAL)) {
                try {
                    int totalRequirement = Integer.parseInt(world.getActivity().substring(0, world.getActivity().indexOf(" ")));
                    if (totalLevel >= totalRequirement) {
                        ((AbstractCollection)types).remove(WorldType.SKILL_TOTAL);
                    }
                }
                catch (NumberFormatException ex) {
                    log.warn("Failed to parse total level requirement for target world", (Throwable)ex);
                }
            }
            if (world.getPlayers() < 1950 && world.getPlayers() >= 0 && ((AbstractSet)currentWorldTypes).equals(types)) break;
        } while (world != currentWorld);
        if (world == currentWorld) {
            String chatMessage = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Couldn't find a world to quick-hop to.").build();
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(chatMessage).build());
        } else {
            this.hop(world.getId());
        }
    }

    private void hop(int worldId) {
        WorldResult worldResult = this.worldService.getWorlds();
        net.runelite.http.api.worlds.World world = worldResult.findWorld(worldId);
        if (world == null) {
            return;
        }
        this.hop(world);
    }

    void hopTo(net.runelite.http.api.worlds.World world) {
        this.clientThread.invoke(() -> this.hop(world));
    }

    private void hop(net.runelite.http.api.worlds.World world) {
        assert (this.client.isClientThread());
        World rsWorld = this.client.createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));
        if (this.client.getGameState() == GameState.LOGIN_SCREEN) {
            this.client.changeWorld(rsWorld);
            return;
        }
        if (this.config.showWorldHopMessage()) {
            String chatMessage = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Quick-hopping to World ").append(ChatColorType.HIGHLIGHT).append(Integer.toString(world.getId())).append(ChatColorType.NORMAL).append("..").build();
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(chatMessage).build());
        }
        this.quickHopTargetWorld = rsWorld;
        this.displaySwitcherAttempts = 0;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (this.quickHopTargetWorld == null) {
            return;
        }
        if (this.client.getWidget(4522002) == null) {
            this.client.openWorldHopper();
            if (++this.displaySwitcherAttempts >= 3) {
                String chatMessage = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Failed to quick-hop after ").append(ChatColorType.HIGHLIGHT).append(Integer.toString(this.displaySwitcherAttempts)).append(ChatColorType.NORMAL).append(" attempts.").build();
                this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(chatMessage).build());
                this.resetQuickHopper();
            }
        } else {
            this.client.hopToWorld(this.quickHopTargetWorld);
            this.resetQuickHopper();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }
        if (event.getMessage().equals("Please finish what you're doing before using the World Switcher.")) {
            this.resetQuickHopper();
        }
    }

    private void resetQuickHopper() {
        this.displaySwitcherAttempts = 0;
        this.quickHopTargetWorld = null;
    }

    private ChatPlayer getChatPlayerFromName(String name) {
        ClanChannelMember member;
        FriendsChatMember member2;
        String cleanName = Text.removeTags(name);
        FriendsChatManager friendsChatManager = this.client.getFriendsChatManager();
        if (friendsChatManager != null && (member2 = (FriendsChatMember)friendsChatManager.findByName(cleanName)) != null) {
            return member2;
        }
        ClanChannel clanChannel = this.client.getClanChannel();
        if (clanChannel != null && (member = clanChannel.findMember(cleanName)) != null) {
            return member;
        }
        clanChannel = this.client.getGuestClanChannel();
        if (clanChannel != null && (member = clanChannel.findMember(cleanName)) != null) {
            return member;
        }
        FriendContainer friendContainer = this.client.getFriendContainer();
        if (friendContainer != null) {
            return (ChatPlayer)friendContainer.findByName(cleanName);
        }
        return null;
    }

    private void pingInitialWorlds() {
        WorldResult worldResult = this.worldService.getWorlds();
        if (worldResult == null || !this.config.showSidebar() || !this.config.ping()) {
            return;
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (net.runelite.http.api.worlds.World world : worldResult.getWorlds()) {
            int ping = this.ping(world);
            SwingUtilities.invokeLater(() -> this.panel.updatePing(world.getId(), ping));
        }
        stopwatch.stop();
        log.debug("Done pinging worlds in {}", (Object)stopwatch.elapsed());
    }

    private void pingNextWorld() {
        boolean displayPing;
        WorldResult worldResult = this.worldService.getWorlds();
        if (worldResult == null || !this.config.showSidebar() || !this.config.ping()) {
            return;
        }
        List worlds = worldResult.getWorlds();
        if (worlds.isEmpty()) {
            return;
        }
        if (this.currentWorld >= worlds.size()) {
            this.currentWorld = 0;
        }
        net.runelite.http.api.worlds.World world = (net.runelite.http.api.worlds.World)worlds.get(this.currentWorld++);
        boolean bl = displayPing = this.config.displayPing() && this.client.getGameState() == GameState.LOGGED_IN;
        if (displayPing && this.client.getWorld() == world.getId()) {
            return;
        }
        int ping = this.ping(world);
        log.trace("Ping for world {} is: {}", (Object)world.getId(), (Object)ping);
        if (this.panel.isActive()) {
            SwingUtilities.invokeLater(() -> this.panel.updatePing(world.getId(), ping));
        }
    }

    private void pingCurrentWorld() {
        WorldResult worldResult = this.worldService.getWorlds();
        if (worldResult == null || !this.config.displayPing() || this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        net.runelite.http.api.worlds.World currentWorld = worldResult.findWorld(this.client.getWorld());
        if (currentWorld == null) {
            log.trace("unable to find current world: {}", (Object)this.client.getWorld());
            return;
        }
        int ping = this.ping(currentWorld);
        log.trace("Ping for current world is: {}", (Object)this.currentPing);
        if (ping < 0) {
            return;
        }
        this.currentPing = ping;
        if (this.panel.isActive()) {
            SwingUtilities.invokeLater(() -> this.panel.updatePing(currentWorld.getId(), this.currentPing));
        }
    }

    Integer getStoredPing(net.runelite.http.api.worlds.World world) {
        if (!this.config.ping()) {
            return null;
        }
        return this.storedPings.get(world.getId());
    }

    private int ping(net.runelite.http.api.worlds.World world) {
        int ping = Ping.ping(world);
        this.storedPings.put(world.getId(), ping);
        return ping;
    }

    public int getLastWorld() {
        return this.lastWorld;
    }

    int getCurrentPing() {
        return this.currentPing;
    }
}

