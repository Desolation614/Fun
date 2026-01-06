/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameTick
 */
package net.runelite.client.plugins.smelting;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.smelting.SmeltingConfig;
import net.runelite.client.plugins.smelting.SmeltingOverlay;
import net.runelite.client.plugins.smelting.SmeltingSession;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name="Smelting", description="Show Smelting stats", tags={"overlay", "skilling"})
@PluginDependency(value=XpTrackerPlugin.class)
public class SmeltingPlugin
extends Plugin {
    @Inject
    private SmeltingConfig config;
    @Inject
    private SmeltingOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    private SmeltingSession session;
    private int cannonBallsMade;

    @Provides
    SmeltingConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SmeltingConfig.class);
    }

    @Override
    protected void startUp() {
        this.session = null;
        this.overlayManager.add(this.overlay);
        this.cannonBallsMade = 0;
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.session = null;
        this.cannonBallsMade = 0;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.SPAM) {
            return;
        }
        if (event.getMessage().startsWith("You retrieve a bar of") || event.getMessage().startsWith("The Varrock platebody enabled you to smelt your next ore")) {
            if (this.session == null) {
                this.session = new SmeltingSession();
            }
            this.session.increaseBarsSmelted();
        } else if (event.getMessage().endsWith(" to form 8 cannonballs.")) {
            this.cannonBallsMade = 8;
        } else if (event.getMessage().endsWith(" to form 4 cannonballs.")) {
            this.cannonBallsMade = 4;
        } else if (event.getMessage().startsWith("You remove the cannonballs from the mould")) {
            if (this.session == null) {
                this.session = new SmeltingSession();
            }
            this.session.increaseCannonBallsSmelted(this.cannonBallsMade);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (this.session != null) {
            Duration statTimeout = Duration.ofMinutes(this.config.statTimeout());
            Duration sinceCaught = Duration.between(this.session.getLastItemSmelted(), Instant.now());
            if (sinceCaught.compareTo(statTimeout) >= 0) {
                this.session = null;
            }
        }
    }

    SmeltingSession getSession() {
        return this.session;
    }

    void setSession(SmeltingSession session) {
        this.session = session;
    }
}

