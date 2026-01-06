/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Actor
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.NPC
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.InteractingChanged
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.events.NpcDespawned
 *  net.runelite.api.events.NpcSpawned
 *  net.runelite.api.events.WidgetLoaded
 *  net.runelite.api.widgets.Widget
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.fishing;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.fishing.FishingConfig;
import net.runelite.client.plugins.fishing.FishingOverlay;
import net.runelite.client.plugins.fishing.FishingSession;
import net.runelite.client.plugins.fishing.FishingSpotMinimapOverlay;
import net.runelite.client.plugins.fishing.FishingSpotOverlay;
import net.runelite.client.plugins.fishing.MinnowSpot;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Fishing", description="Show fishing stats and mark fishing spots", tags={"overlay", "skilling"})
@PluginDependency(value=XpTrackerPlugin.class)
@Singleton
public class FishingPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(FishingPlugin.class);
    private static final int TRAWLER_SHIP_REGION_NORMAL = 7499;
    private static final int TRAWLER_SHIP_REGION_SINKING = 8011;
    private static final int TRAWLER_TIME_LIMIT_IN_SECONDS = 314;
    private static final Pattern FISHING_CATCH_REGEX = Pattern.compile("You catch (?:a|an|some) |Your cormorant returns with its catch.|You catch .* Karambwanji");
    private Instant trawlerStartTime;
    private final FishingSession session = new FishingSession();
    private final Map<Integer, MinnowSpot> minnowSpots = new HashMap<Integer, MinnowSpot>();
    private final List<NPC> fishingSpots = new ArrayList<NPC>();
    private FishingSpot currentSpot;
    @Inject
    private Client client;
    @Inject
    private Notifier notifier;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private FishingConfig config;
    @Inject
    private FishingOverlay overlay;
    @Inject
    private FishingSpotOverlay spotOverlay;
    @Inject
    private FishingSpotMinimapOverlay fishingSpotMinimapOverlay;

    @Provides
    FishingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FishingConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.spotOverlay);
        this.overlayManager.add(this.fishingSpotMinimapOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        this.spotOverlay.setHidden(true);
        this.fishingSpotMinimapOverlay.setHidden(true);
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.spotOverlay);
        this.overlayManager.remove(this.fishingSpotMinimapOverlay);
        this.fishingSpots.clear();
        this.minnowSpots.clear();
        this.currentSpot = null;
        this.trawlerStartTime = null;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        GameState gameState = gameStateChanged.getGameState();
        if (gameState == GameState.CONNECTION_LOST || gameState == GameState.LOGIN_SCREEN || gameState == GameState.HOPPING) {
            this.fishingSpots.clear();
            this.minnowSpots.clear();
        }
    }

    void reset() {
        this.session.setLastFishCaught(null);
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        boolean showOverlays;
        if (event.getItemContainer() != this.client.getItemContainer(93) && event.getItemContainer() != this.client.getItemContainer(94)) {
            return;
        }
        boolean bl = showOverlays = this.session.getLastFishCaught() != null || this.canPlayerFish(this.client.getItemContainer(93)) || this.canPlayerFish(this.client.getItemContainer(94));
        if (!showOverlays) {
            this.currentSpot = null;
        }
        this.spotOverlay.setHidden(!showOverlays);
        this.fishingSpotMinimapOverlay.setHidden(!showOverlays);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.SPAM) {
            return;
        }
        String message = event.getMessage();
        if (FISHING_CATCH_REGEX.matcher(message).find()) {
            this.session.setLastFishCaught(Instant.now());
            this.spotOverlay.setHidden(false);
            this.fishingSpotMinimapOverlay.setHidden(false);
        }
        if (message.equals("A flying fish jumps up and eats some of your minnows!")) {
            this.notifier.notify(this.config.flyingFishNotification(), "A flying fish is eating your minnows!");
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (event.getSource() != this.client.getLocalPlayer()) {
            return;
        }
        Actor target = event.getTarget();
        if (!(target instanceof NPC)) {
            return;
        }
        NPC npc = (NPC)target;
        FishingSpot spot = FishingSpot.findSpot(npc.getId());
        if (spot == null) {
            return;
        }
        this.currentSpot = spot;
    }

    private boolean canPlayerFish(ItemContainer itemContainer) {
        if (itemContainer == null) {
            return false;
        }
        for (Item item : itemContainer.getItems()) {
            switch (item.getId()) {
                case 301: 
                case 303: 
                case 305: 
                case 307: 
                case 309: 
                case 311: 
                case 1585: 
                case 3157: 
                case 3159: 
                case 6209: 
                case 10129: 
                case 11323: 
                case 21028: 
                case 21031: 
                case 21033: 
                case 22816: 
                case 22817: 
                case 22842: 
                case 22844: 
                case 22846: 
                case 23762: 
                case 23764: 
                case 23864: 
                case 25059: 
                case 25114: 
                case 25367: 
                case 25373: 
                case 30342: 
                case 30343: {
                    return true;
                }
            }
        }
        return false;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (this.session.getLastFishCaught() != null) {
            Duration statTimeout = Duration.ofMinutes(this.config.statTimeout());
            Duration sinceCaught = Duration.between(this.session.getLastFishCaught(), Instant.now());
            if (sinceCaught.compareTo(statTimeout) >= 0) {
                this.currentSpot = null;
                this.session.setLastFishCaught(null);
            }
        }
        this.inverseSortSpotDistanceFromPlayer();
        for (NPC npc : this.fishingSpots) {
            int id;
            MinnowSpot minnowSpot;
            if (FishingSpot.findSpot(npc.getId()) != FishingSpot.MINNOW || !this.config.showMinnowOverlay() || (minnowSpot = this.minnowSpots.get(id = npc.getIndex())) != null && minnowSpot.getLoc().equals((Object)npc.getWorldLocation())) continue;
            this.minnowSpots.put(id, new MinnowSpot(npc.getWorldLocation(), Instant.now()));
        }
        this.updateTrawlerTimer();
        this.updateTrawlerContribution();
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (FishingSpot.findSpot(npc.getId()) == null) {
            return;
        }
        this.fishingSpots.add(npc);
        this.inverseSortSpotDistanceFromPlayer();
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        this.fishingSpots.remove(npc);
        MinnowSpot minnowSpot = this.minnowSpots.remove(npc.getIndex());
        if (minnowSpot != null) {
            log.debug("Minnow spot {} despawned", (Object)npc);
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() == 366) {
            this.trawlerStartTime = Instant.now();
            log.debug("Trawler session started");
        }
    }

    private void updateTrawlerContribution() {
        int regionID = this.client.getLocalPlayer().getWorldLocation().getRegionID();
        if (regionID != 7499 && regionID != 8011) {
            return;
        }
        if (!this.config.trawlerContribution()) {
            return;
        }
        Widget trawlerContributionWidget = this.client.getWidget(23986189);
        if (trawlerContributionWidget == null) {
            return;
        }
        int trawlerContribution = this.client.getVarbitValue(3377);
        trawlerContributionWidget.setText("Contribution: " + trawlerContribution);
    }

    private void updateTrawlerTimer() {
        if (this.trawlerStartTime == null) {
            return;
        }
        int regionID = this.client.getLocalPlayer().getWorldLocation().getRegionID();
        if (regionID != 7499 && regionID != 8011) {
            log.debug("Trawler session ended");
            this.trawlerStartTime = null;
            return;
        }
        if (!this.config.trawlerTimer()) {
            return;
        }
        Widget trawlerTimerWidget = this.client.getWidget(23986190);
        if (trawlerTimerWidget == null) {
            return;
        }
        long timeLeft = 314L - Duration.between(this.trawlerStartTime, Instant.now()).getSeconds();
        if (timeLeft < 0L) {
            timeLeft = 0L;
        }
        int minutes = (int)timeLeft / 60;
        int seconds = (int)timeLeft % 60;
        StringBuilder trawlerText = new StringBuilder();
        trawlerText.append("Time Left: ");
        if (minutes > 0) {
            trawlerText.append(minutes);
        } else {
            trawlerText.append('0');
        }
        trawlerText.append(':');
        if (seconds < 10) {
            trawlerText.append('0');
        }
        trawlerText.append(seconds);
        trawlerTimerWidget.setText(trawlerText.toString());
    }

    private void inverseSortSpotDistanceFromPlayer() {
        if (this.fishingSpots.isEmpty()) {
            return;
        }
        LocalPoint cameraPoint = new LocalPoint(this.client.getCameraX(), this.client.getCameraY());
        this.fishingSpots.sort(Comparator.comparingInt(npc -> -npc.getLocalLocation().distanceTo(cameraPoint)).thenComparing(Actor::getLocalLocation, Comparator.comparingInt(LocalPoint::getX).thenComparingInt(LocalPoint::getY)).thenComparingInt(NPC::getId));
    }

    FishingSession getSession() {
        return this.session;
    }

    Map<Integer, MinnowSpot> getMinnowSpots() {
        return this.minnowSpots;
    }

    List<NPC> getFishingSpots() {
        return this.fishingSpots;
    }

    FishingSpot getCurrentSpot() {
        return this.currentSpot;
    }
}

