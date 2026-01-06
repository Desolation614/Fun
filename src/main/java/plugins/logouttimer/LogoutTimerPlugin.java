/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.logouttimer;

import com.google.inject.Provides;
import java.time.Duration;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.logouttimer.LogoutTimerConfig;

@PluginDescriptor(name="Logout Timer", description="Extends the default 5 minute logout timer", enabledByDefault=false)
public class LogoutTimerPlugin
extends Plugin {
    @Inject
    private Client client;
    @Inject
    private LogoutTimerConfig config;

    @Override
    protected void startUp() {
        this.client.setIdleTimeout((int)Duration.ofMinutes(this.config.getIdleTimeout()).toMillis() / 20);
    }

    @Override
    protected void shutDown() {
        this.client.setIdleTimeout((int)Duration.ofMinutes(5L).toMillis() / 20);
    }

    @Provides
    LogoutTimerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(LogoutTimerConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("logouttimer")) {
            this.client.setIdleTimeout((int)Duration.ofMinutes(this.config.getIdleTimeout()).toMillis() / 20);
        }
    }
}

