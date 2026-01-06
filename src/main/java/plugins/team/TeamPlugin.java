/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.FriendsChatMember
 *  net.runelite.api.GameState
 *  net.runelite.api.Player
 *  net.runelite.api.clan.ClanChannel
 *  net.runelite.api.clan.ClanChannelMember
 *  net.runelite.api.events.ClanChannelChanged
 *  net.runelite.api.events.ClanMemberJoined
 *  net.runelite.api.events.ClanMemberLeft
 *  net.runelite.api.events.FriendsChatChanged
 *  net.runelite.api.events.FriendsChatMemberJoined
 *  net.runelite.api.events.FriendsChatMemberLeft
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.PlayerChanged
 *  net.runelite.api.events.PlayerDespawned
 *  net.runelite.api.events.PlayerSpawned
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.team;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatMember;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.events.ClanChannelChanged;
import net.runelite.api.events.ClanMemberJoined;
import net.runelite.api.events.ClanMemberLeft;
import net.runelite.api.events.FriendsChatChanged;
import net.runelite.api.events.FriendsChatMemberJoined;
import net.runelite.api.events.FriendsChatMemberLeft;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.PlayerChanged;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.team.MembersIndicator;
import net.runelite.client.plugins.team.TeamCapesOverlay;
import net.runelite.client.plugins.team.TeamConfig;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Team", description="Shows how many team and clan mates are nearby", tags={"overlay", "players", "cape", "clan", "friend"}, configName="TeamCapesPlugin", enabledByDefault=false)
public class TeamPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(TeamPlugin.class);
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TeamConfig config;
    @Inject
    private TeamCapesOverlay overlay;
    @Inject
    private SpriteManager spriteManager;
    @Inject
    private InfoBoxManager infoBoxManager;
    private Map<Integer, Integer> teams = new LinkedHashMap<Integer, Integer>();
    private final Map<Player, Integer> playerTeam = new HashMap<Player, Integer>();
    private final BiMap<String, Player> players = HashBiMap.create();
    private int friendsChatCount;
    private int clanChatCount;
    private MembersIndicator friendsChatIndicator;
    private MembersIndicator clanChatIndicator;

    @Provides
    TeamConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TeamConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.clientThread.invokeLater(() -> this.client.getPlayers().forEach(this::updateTeam));
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.teams.clear();
        this.playerTeam.clear();
        this.players.clear();
        this.removeFriendsChatCounter();
        this.removeClanChatCounter();
        this.friendsChatCount = 0;
        this.clanChatCount = 0;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("teamCapes")) {
            if (this.config.friendsChatMemberCounter()) {
                this.clientThread.invoke(this::addFriendsChatCounter);
            } else {
                this.removeFriendsChatCounter();
            }
            if (this.config.clanChatMemberCounter()) {
                this.clientThread.invoke(this::addClanChatCounter);
            } else {
                this.removeClanChatCounter();
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged state) {
        GameState gameState = state.getGameState();
        if (gameState == GameState.LOGIN_SCREEN || gameState == GameState.CONNECTION_LOST || gameState == GameState.HOPPING) {
            this.players.clear();
            this.removeFriendsChatCounter();
            this.removeClanChatCounter();
        }
    }

    @Subscribe
    public void onPlayerSpawned(PlayerSpawned event) {
        Player local = this.client.getLocalPlayer();
        Player player = event.getPlayer();
        if (player != local) {
            this.players.put((Object)Text.removeTags(player.getName()), (Object)player);
            if (player.isFriendsChatMember()) {
                ++this.friendsChatCount;
                this.addFriendsChatCounter();
            }
            if (player.isClanMember()) {
                ++this.clanChatCount;
                this.addClanChatCounter();
            }
        }
    }

    @Subscribe
    public void onPlayerDespawned(PlayerDespawned playerDespawned) {
        Player player = playerDespawned.getPlayer();
        Integer team = this.playerTeam.remove(player);
        if (team != null) {
            this.teams.computeIfPresent(team, (key, value) -> value > 1 ? Integer.valueOf(value - 1) : null);
            this.sortTeams();
        }
        this.players.inverse().remove((Object)player);
        if (player.isFriendsChatMember() && this.friendsChatCount > 0 && --this.friendsChatCount == 0) {
            this.removeFriendsChatCounter();
        }
        if (player.isClanMember() && this.clanChatCount > 0 && --this.clanChatCount == 0) {
            this.removeClanChatCounter();
        }
    }

    @Subscribe
    public void onPlayerChanged(PlayerChanged playerChanged) {
        Player player = playerChanged.getPlayer();
        this.updateTeam(player);
    }

    private void updateTeam(Player player) {
        int oldTeam = this.playerTeam.getOrDefault(player, 0);
        if (oldTeam == player.getTeam()) {
            return;
        }
        log.debug("{} has changed teams: {} -> {}", new Object[]{player.getName(), oldTeam, player.getTeam()});
        if (oldTeam > 0) {
            this.teams.computeIfPresent(oldTeam, (key, value) -> value > 1 ? Integer.valueOf(value - 1) : null);
            this.playerTeam.remove(player);
        }
        if (player.getTeam() > 0) {
            this.teams.merge(player.getTeam(), 1, Integer::sum);
            this.playerTeam.put(player, player.getTeam());
        }
        this.sortTeams();
    }

    private void sortTeams() {
        this.teams = this.teams.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()).thenComparingInt(Map.Entry::getKey)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Subscribe
    public void onFriendsChatChanged(FriendsChatChanged event) {
        if (!event.isJoined()) {
            this.removeFriendsChatCounter();
            this.friendsChatCount = 0;
        }
    }

    @Subscribe
    public void onClanChannelChanged(ClanChannelChanged event) {
        if (event.getClanId() == 0) {
            this.removeClanChatCounter();
            this.clanChatCount = 0;
            ClanChannel clanChannel = event.getClanChannel();
            if (clanChannel != null) {
                for (ClanChannelMember member : clanChannel.getMembers()) {
                    String memberName = Text.toJagexName(member.getName());
                    Player player = (Player)this.players.get((Object)memberName);
                    if (player == null) continue;
                    ++this.clanChatCount;
                }
                this.addClanChatCounter();
            }
        }
    }

    @Subscribe
    public void onFriendsChatMemberJoined(FriendsChatMemberJoined event) {
        String memberName;
        Player player;
        FriendsChatMember member = event.getMember();
        if (member.getWorld() == this.client.getWorld() && (player = (Player)this.players.get((Object)(memberName = Text.toJagexName(member.getName())))) != null) {
            ++this.friendsChatCount;
            this.addFriendsChatCounter();
        }
    }

    @Subscribe
    public void onFriendsChatMemberLeft(FriendsChatMemberLeft event) {
        String memberName;
        Player player;
        FriendsChatMember member = event.getMember();
        if (member.getWorld() == this.client.getWorld() && (player = (Player)this.players.get((Object)(memberName = Text.toJagexName(member.getName())))) != null && this.friendsChatCount > 0 && --this.friendsChatCount == 0) {
            this.removeFriendsChatCounter();
        }
    }

    @Subscribe
    public void onClanMemberJoined(ClanMemberJoined clanMemberJoined) {
        String memberName;
        Player player;
        ClanChannelMember member = clanMemberJoined.getClanMember();
        if (member.getWorld() == this.client.getWorld() && (player = (Player)this.players.get((Object)(memberName = Text.toJagexName(member.getName())))) != null) {
            ++this.clanChatCount;
            this.addClanChatCounter();
        }
    }

    @Subscribe
    public void onClanMemberLeft(ClanMemberLeft clanMemberLeft) {
        String memberName;
        Player player;
        ClanChannelMember member = clanMemberLeft.getClanMember();
        if (member.getWorld() == this.client.getWorld() && (player = (Player)this.players.get((Object)(memberName = Text.toJagexName(member.getName())))) != null && this.clanChatCount > 0 && --this.clanChatCount == 0) {
            this.removeClanChatCounter();
        }
    }

    private void addFriendsChatCounter() {
        if (!this.config.friendsChatMemberCounter() || this.friendsChatIndicator != null || this.friendsChatCount == 0) {
            return;
        }
        BufferedImage image = this.spriteManager.getSprite(904, 0);
        this.friendsChatIndicator = new MembersIndicator(image, this){

            @Override
            public String getText() {
                return Integer.toString(TeamPlugin.this.friendsChatCount);
            }

            @Override
            public String getTooltip() {
                return TeamPlugin.this.friendsChatCount + " friends chat member(s) near you";
            }
        };
        this.infoBoxManager.addInfoBox(this.friendsChatIndicator);
    }

    private void removeFriendsChatCounter() {
        this.infoBoxManager.removeInfoBox(this.friendsChatIndicator);
        this.friendsChatIndicator = null;
    }

    private void addClanChatCounter() {
        if (!this.config.clanChatMemberCounter() || this.clanChatIndicator != null || this.clanChatCount == 0) {
            return;
        }
        BufferedImage image = this.spriteManager.getSprite(2307, 0);
        this.clanChatIndicator = new MembersIndicator(image, this){

            @Override
            public String getText() {
                return Integer.toString(TeamPlugin.this.clanChatCount);
            }

            @Override
            public String getTooltip() {
                return TeamPlugin.this.clanChatCount + " clan chat member(s) near you";
            }
        };
        this.infoBoxManager.addInfoBox(this.clanChatIndicator);
    }

    private void removeClanChatCounter() {
        this.infoBoxManager.removeInfoBox(this.clanChatIndicator);
        this.clanChatIndicator = null;
    }

    Map<Integer, Integer> getTeams() {
        return this.teams;
    }
}

