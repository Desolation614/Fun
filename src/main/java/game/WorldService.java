/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.http.api.worlds.World
 *  net.runelite.http.api.worlds.WorldResult
 *  okhttp3.HttpUrl
 *  okhttp3.OkHttpClient
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.game;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.WorldsFetch;
import net.runelite.client.game.WorldClient;
import net.runelite.client.util.RunnableExceptionLogger;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class WorldService {
    private static final Logger log = LoggerFactory.getLogger(WorldService.class);
    private static final int WORLD_FETCH_TIMER = 10;
    private final Client client;
    private final ScheduledExecutorService scheduledExecutorService;
    private final WorldClient worldClient;
    private final EventBus eventBus;
    private final CompletableFuture<WorldResult> firstRunFuture = new CompletableFuture();
    private WorldResult worlds;

    @Inject
    private WorldService(Client client, ScheduledExecutorService scheduledExecutorService, OkHttpClient okHttpClient, @Named(value="runelite.api.base") HttpUrl apiBase, EventBus eventBus) {
        this.client = client;
        this.scheduledExecutorService = scheduledExecutorService;
        this.worldClient = new WorldClient(okHttpClient, apiBase);
        this.eventBus = eventBus;
        scheduledExecutorService.scheduleWithFixedDelay(RunnableExceptionLogger.wrap(this::tick), 0L, 10L, TimeUnit.MINUTES);
    }

    private void tick() {
        try {
            if (this.worlds == null || this.client.getGameState() == GameState.LOGGED_IN) {
                this.fetch();
            }
        }
        finally {
            this.firstRunFuture.complete(this.worlds);
        }
    }

    private void fetch() {
        log.debug("Fetching worlds");
        try {
            WorldResult worldResult = this.worldClient.lookupWorlds();
            worldResult.getWorlds().sort(Comparator.comparingInt(World::getId));
            this.worlds = worldResult;
            this.eventBus.post(new WorldsFetch(worldResult));
        }
        catch (IOException ex) {
            log.warn("Error looking up worlds", (Throwable)ex);
        }
    }

    public void refresh() {
        this.scheduledExecutorService.execute(this::fetch);
    }

    @Nullable
    public WorldResult getWorlds() {
        if (!this.firstRunFuture.isDone()) {
            try {
                return this.firstRunFuture.get(10L, TimeUnit.SECONDS);
            }
            catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.warn("Failed to retrieve worlds on first run", (Throwable)e);
            }
        }
        return this.worlds;
    }
}

