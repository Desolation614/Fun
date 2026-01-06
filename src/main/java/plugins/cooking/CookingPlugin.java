/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.Player
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.GraphicChanged
 */
package net.runelite.client.plugins.cooking;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.cooking.CookingConfig;
import net.runelite.client.plugins.cooking.CookingOverlay;
import net.runelite.client.plugins.cooking.CookingSession;
import net.runelite.client.plugins.cooking.FermentTimer;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@PluginDescriptor(name="Cooking", description="Show cooking statistics", tags={"overlay", "skilling", "cook"})
@PluginDependency(value=XpTrackerPlugin.class)
public class CookingPlugin
extends Plugin {
    @Inject
    private Client client;
    @Inject
    private CookingConfig config;
    @Inject
    private CookingOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ItemManager itemManager;
    private CookingSession session;

    @Provides
    CookingConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(CookingConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.session = null;
        this.overlayManager.add(this.overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.infoBoxManager.removeIf(FermentTimer.class::isInstance);
        this.overlayManager.remove(this.overlay);
        this.session = null;
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (this.session == null || this.config.statTimeout() == 0) {
            return;
        }
        Duration statTimeout = Duration.ofMinutes(this.config.statTimeout());
        Duration sinceCooked = Duration.between(this.session.getLastCookingAction(), Instant.now());
        if (sinceCooked.compareTo(statTimeout) >= 0) {
            this.session = null;
        }
    }

    @Subscribe
    public void onGraphicChanged(GraphicChanged graphicChanged) {
        Player player = this.client.getLocalPlayer();
        if (graphicChanged.getActor() != player) {
            return;
        }
        if (player.getGraphic() == 47 && this.config.fermentTimer()) {
            Optional<FermentTimer> fermentTimerOpt = this.infoBoxManager.getInfoBoxes().stream().filter(FermentTimer.class::isInstance).map(FermentTimer.class::cast).findAny();
            if (fermentTimerOpt.isPresent()) {
                FermentTimer fermentTimer = fermentTimerOpt.get();
                fermentTimer.reset();
            } else {
                FermentTimer fermentTimer = new FermentTimer(this.itemManager.getImage(1993), this);
                this.infoBoxManager.addInfoBox(fermentTimer);
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.SPAM) {
            return;
        }
        String message = event.getMessage();
        if (message.startsWith("You successfully cook") || message.startsWith("You successfully bake") || message.startsWith("You successfully fry") || message.startsWith("You manage to cook") || message.startsWith("You roast a") || message.startsWith("You spit-roast") || message.startsWith("You cook") || message.startsWith("Eventually the Jubbly") || message.startsWith("You half-cook") || message.startsWith("The undead meat is now cooked") || message.startsWith("The undead chicken is now cooked") || message.startsWith("You successfully scramble") || message.startsWith("You dry a piece of meat")) {
            if (this.session == null) {
                this.session = new CookingSession();
            }
            this.session.updateLastCookingAction();
            this.session.increaseCookAmount();
        } else if (message.startsWith("You accidentally burn") || message.equals("You burn the mushroom in the fire.") || message.startsWith("Unfortunately the Jubbly") || message.startsWith("You accidentally spoil")) {
            if (this.session == null) {
                this.session = new CookingSession();
            }
            this.session.updateLastCookingAction();
            this.session.increaseBurnAmount();
        }
    }

    CookingSession getSession() {
        return this.session;
    }

    void setSession(CookingSession session) {
        this.session = session;
    }
}

