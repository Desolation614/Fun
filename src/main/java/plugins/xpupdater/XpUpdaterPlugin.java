/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Player
 *  net.runelite.api.WorldType
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  okhttp3.Call
 *  okhttp3.Callback
 *  okhttp3.FormBody
 *  okhttp3.FormBody$Builder
 *  okhttp3.HttpUrl
 *  okhttp3.HttpUrl$Builder
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.xpupdater;

import com.google.inject.Provides;
import java.io.IOException;
import java.util.EnumSet;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.WorldType;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.xpupdater.XpUpdaterConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="XP Updater", description="Automatically updates your stats on external xptrackers when you log out", tags={"cml", "crystalmathlabs", "runetracker", "templeosrs", "temple", "wom", "wiseoldman", "wise old man", "external", "integration"}, enabledByDefault=false, forceDisabled=true)
public class XpUpdaterPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(XpUpdaterPlugin.class);
    private static final int XP_THRESHOLD = 10000;
    @Inject
    private Client client;
    @Inject
    private XpUpdaterConfig config;
    @Inject
    private OkHttpClient okHttpClient;
    private long lastAccount;
    private boolean fetchXp;
    private long lastXp;

    @Provides
    XpUpdaterConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(XpUpdaterConfig.class);
    }

    @Override
    protected void startUp() {
        this.fetchXp = true;
        this.lastAccount = -1L;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        GameState state = gameStateChanged.getGameState();
        if (state == GameState.LOGGED_IN) {
            if (this.lastAccount != this.client.getAccountHash()) {
                this.lastAccount = this.client.getAccountHash();
                this.fetchXp = true;
            }
        } else if (state == GameState.LOGIN_SCREEN || state == GameState.HOPPING) {
            Player local = this.client.getLocalPlayer();
            if (local == null) {
                return;
            }
            long totalXp = this.client.getOverallExperience();
            if (Math.abs(totalXp - this.lastXp) > 10000L) {
                log.debug("Submitting update for {} accountHash {}", (Object)local.getName(), (Object)this.lastAccount);
                this.update(this.lastAccount, local.getName());
                this.lastXp = totalXp;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (this.fetchXp) {
            this.lastXp = this.client.getOverallExperience();
            this.fetchXp = false;
        }
    }

    private void update(long accountHash, String username) {
        EnumSet worldTypes = this.client.getWorldType();
        username = username.replace(" ", "_");
        this.updateCml(username, worldTypes);
        this.updateRunetracker(username, worldTypes);
        this.updateTempleosrs(accountHash, username, worldTypes);
        this.updateWom(accountHash, username, worldTypes);
    }

    private void updateCml(String username, EnumSet<WorldType> worldTypes) {
        if (!(!this.config.cml() || worldTypes.contains(WorldType.SEASONAL) || worldTypes.contains(WorldType.DEADMAN) || worldTypes.contains(WorldType.NOSAVE_MODE) || worldTypes.contains(WorldType.FRESH_START_WORLD))) {
            HttpUrl url = new HttpUrl.Builder().scheme("https").host("crystalmathlabs.com").addPathSegment("tracker").addPathSegment("api.php").addQueryParameter("type", "update").addQueryParameter("player", username).build();
            Request request = new Request.Builder().header("User-Agent", "RuneLite").url(url).build();
            this.sendRequest("CrystalMathLabs", request);
        }
    }

    private void updateRunetracker(String username, EnumSet<WorldType> worldTypes) {
        if (!(!this.config.runetracker() || worldTypes.contains(WorldType.SEASONAL) || worldTypes.contains(WorldType.DEADMAN) || worldTypes.contains(WorldType.NOSAVE_MODE) || worldTypes.contains(WorldType.FRESH_START_WORLD))) {
            HttpUrl url = new HttpUrl.Builder().scheme("https").host("rscript.org").addPathSegment("lookup.php").addQueryParameter("type", "stats07").addQueryParameter("user", username).build();
            Request request = new Request.Builder().header("User-Agent", "RuneLite").url(url).build();
            this.sendRequest("RuneTracker", request);
        }
    }

    private void updateTempleosrs(long accountHash, String username, EnumSet<WorldType> worldTypes) {
        if (this.config.templeosrs() && !worldTypes.contains(WorldType.SEASONAL) && !worldTypes.contains(WorldType.DEADMAN) && !worldTypes.contains(WorldType.NOSAVE_MODE)) {
            HttpUrl.Builder url = new HttpUrl.Builder().scheme("https").host("templeosrs.com").addPathSegment("php").addPathSegment("add_datapoint.php").addQueryParameter("player", username).addQueryParameter("accountHash", Long.toString(accountHash));
            if (worldTypes.contains(WorldType.FRESH_START_WORLD)) {
                url.addQueryParameter("worldType", "fsw");
            }
            Request request = new Request.Builder().header("User-Agent", "RuneLite").url(url.build()).build();
            this.sendRequest("TempleOSRS", request);
        }
    }

    private void updateWom(long accountHash, String username, EnumSet<WorldType> worldTypes) {
        if (this.config.wiseoldman() && !worldTypes.contains(WorldType.DEADMAN) && !worldTypes.contains(WorldType.NOSAVE_MODE)) {
            HttpUrl url = new HttpUrl.Builder().scheme("https").host("api.wiseoldman.net").addPathSegment(worldTypes.contains(WorldType.SEASONAL) ? "league" : "v2").addPathSegment("players").addPathSegment(username).build();
            FormBody formBody = new FormBody.Builder().add("accountHash", Long.toString(accountHash)).build();
            Request request = new Request.Builder().header("User-Agent", "RuneLite").url(url).post((RequestBody)formBody).build();
            this.sendRequest("Wise Old Man", request);
        }
    }

    private void sendRequest(final String platform, Request request) {
        this.okHttpClient.newCall(request).enqueue(new Callback(){

            public void onFailure(Call call, IOException e) {
                log.warn("Error submitting {} update, caused by {}.", (Object)platform, (Object)e.getMessage());
            }

            public void onResponse(Call call, Response response) {
                response.close();
            }
        });
    }
}

