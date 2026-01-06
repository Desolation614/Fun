/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  com.google.common.base.Strings
 *  com.google.inject.Inject
 *  com.google.inject.Provides
 *  javax.inject.Named
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Skill
 *  net.runelite.api.WorldType
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.StatChanged
 *  net.runelite.discord.DiscordUser
 *  okhttp3.Call
 *  okhttp3.Callback
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.discord;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.inject.Named;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.discord.DiscordService;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.party.messages.UserSync;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.discord.DiscordConfig;
import net.runelite.client.plugins.discord.DiscordGameEventType;
import net.runelite.client.plugins.discord.DiscordState;
import net.runelite.client.plugins.discord.DiscordUserInfo;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.discord.DiscordUser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Discord", description="Show your status and activity in the Discord user panel", tags={"action", "activity", "external", "integration", "status"})
public class DiscordPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(DiscordPlugin.class);
    @Inject
    private Client client;
    @Inject
    private DiscordConfig config;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private DiscordState discordState;
    @Inject
    private PartyService partyService;
    @Inject
    private DiscordService discordService;
    @Inject
    private WSClient wsClient;
    @Inject
    private OkHttpClient okHttpClient;
    @Inject
    @Named(value="runelite.discord.invite")
    private String discordInvite;
    private final Map<Skill, Integer> skillExp = new HashMap<Skill, Integer>();
    private NavigationButton discordButton;
    private boolean loginFlag;

    @Provides
    private DiscordConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DiscordConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        BufferedImage icon = ImageUtil.loadImageResource(this.getClass(), "discord.png");
        this.discordButton = NavigationButton.builder().tooltip("Join Discord").icon(icon).onClick(() -> LinkBrowser.browse(this.discordInvite)).build();
        this.clientToolbar.addNavigation(this.discordButton);
        this.resetState();
        this.checkForGameStateUpdate();
        this.checkForAreaUpdate();
        this.wsClient.registerMessage(DiscordUserInfo.class);
    }

    @Override
    protected void shutDown() throws Exception {
        this.clientToolbar.removeNavigation(this.discordButton);
        this.resetState();
        this.wsClient.unregisterMessage(DiscordUserInfo.class);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case LOGIN_SCREEN: {
                this.resetState();
                this.checkForGameStateUpdate();
                return;
            }
            case LOGGING_IN: {
                this.loginFlag = true;
                break;
            }
            case LOGGED_IN: {
                if (this.loginFlag) {
                    this.loginFlag = false;
                    this.resetState();
                    this.checkForGameStateUpdate();
                }
                this.checkForAreaUpdate();
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equalsIgnoreCase("discord")) {
            this.resetState();
            this.checkForGameStateUpdate();
            this.checkForAreaUpdate();
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        int exp;
        Skill skill = statChanged.getSkill();
        Integer previous = this.skillExp.put(skill, exp = statChanged.getXp());
        if (previous == null || previous >= exp) {
            return;
        }
        DiscordGameEventType discordGameEventType = DiscordGameEventType.fromSkill(skill);
        if (discordGameEventType != null && this.config.showSkillingActivity()) {
            this.discordState.triggerEvent(discordGameEventType);
        }
    }

    @Subscribe
    public void onDiscordUserInfo(final DiscordUserInfo event) {
        String url;
        CharMatcher matcher = CharMatcher.anyOf((CharSequence)"abcdef0123456789");
        if (!matcher.matchesAllOf((CharSequence)event.getUserId()) || !matcher.matchesAllOf((CharSequence)event.getAvatarId().replace("a_", ""))) {
            return;
        }
        if (Strings.isNullOrEmpty((String)event.getAvatarId())) {
            int disc = Integer.parseInt(event.getDiscriminator());
            int avatarId = disc % 5;
            url = "https://cdn.discordapp.com/embed/avatars/" + avatarId + ".png";
        } else {
            url = "https://cdn.discordapp.com/avatars/" + event.getUserId() + "/" + event.getAvatarId() + ".png";
        }
        log.debug("Got user avatar {}", (Object)url);
        Request request = new Request.Builder().url(url).build();
        this.okHttpClient.newCall(request).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void onResponse(Call call, Response response) throws IOException {
                try (Response response2 = response;){
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + String.valueOf(response));
                    }
                    InputStream inputStream = response.body().byteStream();
                    Class<ImageIO> clazz = ImageIO.class;
                    synchronized (ImageIO.class) {
                        BufferedImage image = ImageIO.read(inputStream);
                        // ** MonitorExit[var6_6] (shouldn't be in output)
                        DiscordPlugin.this.partyService.setPartyMemberAvatar(event.getMemberId(), image);
                    }
                }
                {
                    return;
                }
            }
        });
    }

    @Subscribe
    public void onUserSync(UserSync event) {
        DiscordUser discordUser = this.discordService.getCurrentUser();
        if (discordUser != null) {
            DiscordUserInfo userInfo = new DiscordUserInfo(discordUser.userId, discordUser.username, discordUser.discriminator, discordUser.avatar);
            this.partyService.send(userInfo);
        }
    }

    @Schedule(period=1L, unit=ChronoUnit.MINUTES)
    public void checkForValidStatus() {
        this.discordState.checkForTimeout();
    }

    private void resetState() {
        this.discordState.reset();
    }

    private void checkForGameStateUpdate() {
        boolean isLoggedIn;
        boolean bl = isLoggedIn = this.client.getGameState() == GameState.LOGGED_IN;
        if (this.config.showMainMenu() || isLoggedIn) {
            this.discordState.triggerEvent(isLoggedIn ? DiscordGameEventType.IN_GAME : DiscordGameEventType.IN_MENU);
        }
    }

    private void checkForAreaUpdate() {
        if (this.client.getLocalPlayer() == null) {
            return;
        }
        int playerRegionID = WorldPoint.fromLocalInstance((Client)this.client, (LocalPoint)this.client.getLocalPlayer().getLocalLocation()).getRegionID();
        if (playerRegionID == 0) {
            return;
        }
        EnumSet worldType = this.client.getWorldType();
        if (worldType.contains(WorldType.DEADMAN)) {
            this.discordState.triggerEvent(DiscordGameEventType.PLAYING_DEADMAN);
            return;
        }
        if (WorldType.isPvpWorld((Collection)worldType)) {
            this.discordState.triggerEvent(DiscordGameEventType.PLAYING_PVP);
            return;
        }
        DiscordGameEventType discordGameEventType = DiscordGameEventType.fromRegion(playerRegionID);
        if (DiscordGameEventType.MG_NIGHTMARE_ZONE == discordGameEventType && this.client.getLocalPlayer().getWorldLocation().getPlane() == 0) {
            discordGameEventType = null;
        }
        if (discordGameEventType == null) {
            this.discordState.triggerEvent(DiscordGameEventType.IN_GAME);
            return;
        }
        if (!this.showArea(discordGameEventType)) {
            return;
        }
        this.discordState.triggerEvent(discordGameEventType);
    }

    private boolean showArea(DiscordGameEventType event) {
        if (event == null) {
            return false;
        }
        switch (event.getDiscordAreaType()) {
            case BOSSES: {
                return this.config.showBossActivity();
            }
            case CITIES: {
                return this.config.showCityActivity();
            }
            case DUNGEONS: {
                return this.config.showDungeonActivity();
            }
            case MINIGAMES: {
                return this.config.showMinigameActivity();
            }
            case REGIONS: {
                return this.config.showRegionsActivity();
            }
            case RAIDS: {
                return this.config.showRaidingActivity();
            }
        }
        return false;
    }
}

