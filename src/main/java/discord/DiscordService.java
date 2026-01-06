/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.inject.Singleton
 *  net.runelite.discord.DiscordEventHandlers
 *  net.runelite.discord.DiscordRPC
 *  net.runelite.discord.DiscordRichPresence
 *  net.runelite.discord.DiscordUser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.discord;

import com.google.common.base.Strings;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import net.runelite.client.discord.DiscordPresence;
import net.runelite.client.discord.events.DiscordDisconnected;
import net.runelite.client.discord.events.DiscordErrored;
import net.runelite.client.discord.events.DiscordJoinGame;
import net.runelite.client.discord.events.DiscordJoinRequest;
import net.runelite.client.discord.events.DiscordReady;
import net.runelite.client.discord.events.DiscordSpectateGame;
import net.runelite.client.eventbus.EventBus;
import net.runelite.discord.DiscordEventHandlers;
import net.runelite.discord.DiscordRPC;
import net.runelite.discord.DiscordRichPresence;
import net.runelite.discord.DiscordUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DiscordService
implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(DiscordService.class);
    private final EventBus eventBus;
    private final ScheduledExecutorService executorService;
    private final String discordAppId;
    private final DiscordRPC discordRPC;
    private DiscordEventHandlers discordEventHandlers;
    private DiscordUser currentUser;

    @Inject
    private DiscordService(EventBus eventBus, ScheduledExecutorService executorService, @Named(value="runelite.discord.enable") boolean enable, @Named(value="runelite.discord.appid") String discordAppId) {
        DiscordRPC discordRPC;
        this.eventBus = eventBus;
        this.executorService = executorService;
        this.discordAppId = discordAppId;
        if (!enable) {
            this.discordRPC = null;
            return;
        }
        try {
            discordRPC = DiscordRPC.INSTANCE;
        }
        catch (Error e) {
            log.warn("Failed to load Discord library, Discord support will be disabled.");
            this.discordRPC = null;
            return;
        }
        this.discordRPC = discordRPC;
    }

    public void init() {
        if (this.discordRPC == null) {
            return;
        }
        log.info("Initializing Discord RPC service.");
        this.discordEventHandlers = new DiscordEventHandlers();
        this.discordEventHandlers.ready = this::ready;
        this.discordEventHandlers.disconnected = this::disconnected;
        this.discordEventHandlers.errored = this::errored;
        this.discordEventHandlers.joinGame = this::joinGame;
        this.discordEventHandlers.spectateGame = this::spectateGame;
        this.discordEventHandlers.joinRequest = this::joinRequest;
        this.discordRPC.Discord_Initialize(this.discordAppId, this.discordEventHandlers, true, null);
        this.executorService.scheduleAtFixedRate(() -> ((DiscordRPC)this.discordRPC).Discord_RunCallbacks(), 0L, 2L, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        if (this.discordRPC != null) {
            this.discordRPC.Discord_Shutdown();
        }
    }

    public void updatePresence(DiscordPresence discordPresence) {
        if (this.discordRPC == null) {
            return;
        }
        DiscordRichPresence discordRichPresence = new DiscordRichPresence();
        discordRichPresence.state = discordPresence.getState();
        discordRichPresence.details = discordPresence.getDetails();
        discordRichPresence.startTimestamp = discordPresence.getStartTimestamp() != null ? discordPresence.getStartTimestamp().getEpochSecond() : 0L;
        discordRichPresence.endTimestamp = discordPresence.getEndTimestamp() != null ? discordPresence.getEndTimestamp().getEpochSecond() : 0L;
        discordRichPresence.largeImageKey = Strings.isNullOrEmpty((String)discordPresence.getLargeImageKey()) ? "default" : discordPresence.getLargeImageKey();
        discordRichPresence.largeImageText = discordPresence.getLargeImageText();
        if (!Strings.isNullOrEmpty((String)discordPresence.getSmallImageKey())) {
            discordRichPresence.smallImageKey = discordPresence.getSmallImageKey();
        }
        discordRichPresence.smallImageText = discordPresence.getSmallImageText();
        discordRichPresence.partyId = discordPresence.getPartyId();
        discordRichPresence.partySize = discordPresence.getPartySize();
        discordRichPresence.partyMax = discordPresence.getPartyMax();
        discordRichPresence.matchSecret = discordPresence.getMatchSecret();
        discordRichPresence.joinSecret = discordPresence.getJoinSecret();
        discordRichPresence.spectateSecret = discordPresence.getSpectateSecret();
        discordRichPresence.instance = (byte)(discordPresence.isInstance() ? 1 : 0);
        log.debug("Sending presence update {}", (Object)discordPresence);
        this.discordRPC.Discord_UpdatePresence(discordRichPresence);
    }

    public void clearPresence() {
        if (this.discordRPC != null) {
            this.discordRPC.Discord_ClearPresence();
        }
    }

    public void respondToRequest(String userId, int reply) {
        if (this.discordRPC != null) {
            this.discordRPC.Discord_Respond(userId, reply);
        }
    }

    private void ready(DiscordUser user) {
        log.info("Discord RPC service is ready with user {}.", (Object)user.username);
        this.currentUser = user;
        this.eventBus.post(new DiscordReady(user.userId, user.username, user.discriminator, user.avatar));
    }

    private void disconnected(int errorCode, String message) {
        log.debug("Discord disconnected {}: {}", (Object)errorCode, (Object)message);
        this.eventBus.post(new DiscordDisconnected(errorCode, message));
    }

    private void errored(int errorCode, String message) {
        log.warn("Discord error: {} - {}", (Object)errorCode, (Object)message);
        this.eventBus.post(new DiscordErrored(errorCode, message));
    }

    private void joinGame(String joinSecret) {
        log.debug("Discord join game: {}", (Object)joinSecret);
        this.eventBus.post(new DiscordJoinGame(joinSecret));
    }

    private void spectateGame(String spectateSecret) {
        log.debug("Discord spectate game: {}", (Object)spectateSecret);
        this.eventBus.post(new DiscordSpectateGame(spectateSecret));
    }

    private void joinRequest(DiscordUser user) {
        log.debug("Discord join request: {}", (Object)user);
        this.eventBus.post(new DiscordJoinRequest(user.userId, user.username, user.discriminator, user.avatar));
    }

    public DiscordUser getCurrentUser() {
        return this.currentUser;
    }
}

