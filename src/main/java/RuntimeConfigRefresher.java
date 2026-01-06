/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.GameState
 *  net.runelite.api.events.GameStateChanged
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.RuntimeConfig;
import net.runelite.client.RuntimeConfigLoader;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class RuntimeConfigRefresher {
    private static final Logger log = LoggerFactory.getLogger(RuntimeConfigRefresher.class);
    @Nullable
    private final RuntimeConfig managedRuntimeConfig;
    private final RuntimeConfigLoader configLoader;
    private Instant nextRefreshAt = this.nextRefreshTime();

    @Inject
    private RuntimeConfigRefresher(@Nullable RuntimeConfig managedRuntimeConfig, RuntimeConfigLoader configLoader, EventBus eventBus) {
        this.managedRuntimeConfig = managedRuntimeConfig;
        this.configLoader = configLoader;
        eventBus.register(this);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.LOGIN_SCREEN) {
            return;
        }
        if (Instant.now().isBefore(this.nextRefreshAt)) {
            log.debug("Skipping runtimeConfig refresh, next refresh at {}", (Object)this.nextRefreshAt);
            return;
        }
        if (this.managedRuntimeConfig == null) {
            log.debug("Skipping runtimeConfig refresh, current one is null");
            return;
        }
        this.configLoader.fetch().thenAccept(this::refreshConfig);
        this.nextRefreshAt = this.nextRefreshTime();
    }

    private Instant nextRefreshTime() {
        return Instant.now().plus(10L, ChronoUnit.MINUTES);
    }

    private void refreshConfig(RuntimeConfig runtimeConfig) {
        this.managedRuntimeConfig.refresh(runtimeConfig);
        log.debug("Successfully refreshed the runtimeConfig");
    }
}

