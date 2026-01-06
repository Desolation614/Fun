/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.events.BeforeRender
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.nightmarezone;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.nightmarezone.NightmareZoneConfig;
import net.runelite.client.plugins.nightmarezone.NightmareZoneOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@PluginDescriptor(name="Nightmare Zone", description="Show NMZ points/absorption and/or notify about expiring potions", tags={"combat", "nmz", "minigame", "notifications"}, forceDisabled=true)
public class NightmareZonePlugin
extends Plugin {
    private static final int[] NMZ_MAP_REGION = new int[]{9033};
    private static final Duration HOUR = Duration.ofHours(1L);
    private static final Duration OVERLOAD_DURATION = Duration.ofMinutes(5L);
    @Inject
    private Notifier notifier;
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private NightmareZoneConfig config;
    @Inject
    private NightmareZoneOverlay overlay;
    private int pointsPerHour;
    private Instant nmzSessionStartTime;
    private boolean absorptionNotificationSend = true;
    private boolean overloadNotificationSend = false;
    private Instant lastOverload;

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.overlay.removeAbsorptionCounter();
        this.absorptionNotificationSend = true;
        this.overloadNotificationSend = false;
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.overlay.removeAbsorptionCounter();
        Widget nmzWidget = this.client.getWidget(0xCA0000);
        if (nmzWidget != null) {
            nmzWidget.setHidden(false);
        }
        this.resetPointsPerHour();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        this.overlay.updateConfig();
    }

    @Provides
    NightmareZoneConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(NightmareZoneConfig.class);
    }

    @Subscribe
    public void onBeforeRender(BeforeRender beforeRender) {
        if (!this.isInNightmareZone() || !this.config.moveOverlay()) {
            return;
        }
        Widget nmzWidget = this.client.getWidget(0xCA0000);
        if (nmzWidget != null) {
            nmzWidget.setHidden(true);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!this.isInNightmareZone()) {
            if (!this.absorptionNotificationSend) {
                this.absorptionNotificationSend = true;
            }
            if (this.nmzSessionStartTime != null) {
                this.resetPointsPerHour();
            }
            this.overloadNotificationSend = false;
            return;
        }
        this.checkAbsorption();
        if (this.overloadNotificationSend && this.config.overloadEarlyWarningSeconds() > 0) {
            this.checkOverload();
        }
        if (this.config.moveOverlay()) {
            this.pointsPerHour = this.calculatePointsPerHour();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!this.isInNightmareZone() || event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM) {
            return;
        }
        String msg = Text.removeTags(event.getMessage());
        if (msg.contains("The effects of overload have worn off, and you feel normal again.")) {
            this.overloadNotificationSend = false;
            this.notifier.notify(this.config.overloadNotification(), "Your overload has worn off");
        } else if (msg.contains("A power-up has spawned:")) {
            if (msg.contains("Power surge")) {
                this.notifier.notify(this.config.powerSurgeNotification(), msg);
            } else if (msg.contains("Recurrent damage")) {
                this.notifier.notify(this.config.recurrentDamageNotification(), msg);
            } else if (msg.contains("Zapper")) {
                this.notifier.notify(this.config.zapperNotification(), msg);
            } else if (msg.contains("Ultimate force")) {
                this.notifier.notify(this.config.ultimateForceNotification(), msg);
            }
        } else if (msg.contains("You drink some of your overload potion.")) {
            this.lastOverload = Instant.now();
            this.overloadNotificationSend = true;
        }
    }

    private void checkOverload() {
        if (Instant.now().isAfter(this.lastOverload.plus(OVERLOAD_DURATION).minus(Duration.ofSeconds(this.config.overloadEarlyWarningSeconds())))) {
            this.notifier.notify(this.config.overloadNotification(), "Your overload potion is about to expire!");
            this.overloadNotificationSend = false;
        }
    }

    private void checkAbsorption() {
        int absorptionPoints = this.client.getVarbitValue(3956);
        if (!this.absorptionNotificationSend) {
            if (absorptionPoints < this.config.absorptionThreshold()) {
                this.notifier.notify(this.config.absorptionNotification(), "Absorption points below: " + this.config.absorptionThreshold());
                this.absorptionNotificationSend = true;
            }
        } else if (absorptionPoints > this.config.absorptionThreshold()) {
            this.absorptionNotificationSend = false;
        }
    }

    private int calculatePointsPerHour() {
        Duration timeSinceStart;
        Instant now = Instant.now();
        int currentPoints = this.client.getVarbitValue(3949);
        if (this.nmzSessionStartTime == null) {
            this.nmzSessionStartTime = now;
        }
        if (!(timeSinceStart = Duration.between(this.nmzSessionStartTime, now)).isZero()) {
            return (int)((double)currentPoints * (double)HOUR.toMillis() / (double)timeSinceStart.toMillis());
        }
        return 0;
    }

    private void resetPointsPerHour() {
        this.nmzSessionStartTime = null;
        this.pointsPerHour = 0;
    }

    public boolean isInNightmareZone() {
        if (this.client.getLocalPlayer() == null) {
            return false;
        }
        return this.client.getLocalPlayer().getWorldLocation().getPlane() > 0 && Arrays.equals(this.client.getMapRegions(), NMZ_MAP_REGION);
    }

    public int getPointsPerHour() {
        return this.pointsPerHour;
    }
}

