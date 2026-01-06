/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.google.inject.Provides
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.CommandExecuted
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.VarbitChanged
 *  net.runelite.api.events.WidgetClosed
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.timetracking;

import com.google.inject.Inject;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.TimeTrackingPanel;
import net.runelite.client.plugins.timetracking.clocks.ClockManager;
import net.runelite.client.plugins.timetracking.farming.CompostTracker;
import net.runelite.client.plugins.timetracking.farming.FarmingContractManager;
import net.runelite.client.plugins.timetracking.farming.FarmingTracker;
import net.runelite.client.plugins.timetracking.farming.PaymentTracker;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseTracker;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(name="Time Tracking", description="Enable the Time Tracking panel, which contains timers, stopwatches, and farming and bird house trackers", tags={"birdhouse", "farming", "hunter", "notifications", "skilling", "stopwatches", "timers", "panel"})
public class TimeTrackingPlugin
extends Plugin {
    private static final String CONTRACT_COMPLETED = "You've completed a Farming Guild Contract. You should return to Guildmaster Jane.";
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private Client client;
    @Inject
    private EventBus eventBus;
    @Inject
    private CompostTracker compostTracker;
    @Inject
    private PaymentTracker paymentTracker;
    @Inject
    private FarmingTracker farmingTracker;
    @Inject
    private BirdHouseTracker birdHouseTracker;
    @Inject
    private FarmingContractManager farmingContractManager;
    @Inject
    private ClockManager clockManager;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ScheduledExecutorService executorService;
    @Inject
    private ConfigManager configManager;
    private ScheduledFuture panelUpdateFuture;
    private ScheduledFuture notifierFuture;
    private TimeTrackingPanel panel;
    private NavigationButton navButton;
    private WorldPoint lastTickLocation;
    private boolean lastTickPostLogin;
    private int lastModalCloseTick = 0;

    @Provides
    TimeTrackingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TimeTrackingConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        this.clockManager.loadTimers();
        this.clockManager.loadStopwatches();
        this.birdHouseTracker.loadFromConfig();
        this.farmingTracker.loadCompletionTimes();
        this.eventBus.register(this.compostTracker);
        this.eventBus.register(this.paymentTracker);
        BufferedImage icon = ImageUtil.loadImageResource(this.getClass(), "watch.png");
        this.panel = (TimeTrackingPanel)this.injector.getInstance(TimeTrackingPanel.class);
        this.navButton = NavigationButton.builder().tooltip("Time Tracking").icon(icon).panel(this.panel).priority(4).build();
        this.clientToolbar.addNavigation(this.navButton);
        this.panelUpdateFuture = this.executorService.scheduleAtFixedRate(this::updatePanel, 200L, 200L, TimeUnit.MILLISECONDS);
        this.notifierFuture = this.executorService.scheduleAtFixedRate(this::checkCompletion, 10L, 10L, TimeUnit.SECONDS);
    }

    @Override
    protected void shutDown() throws Exception {
        this.lastTickLocation = null;
        this.lastTickPostLogin = false;
        this.eventBus.unregister(this.paymentTracker);
        this.eventBus.unregister(this.compostTracker);
        if (this.panelUpdateFuture != null) {
            this.panelUpdateFuture.cancel(true);
            this.panelUpdateFuture = null;
        }
        this.notifierFuture.cancel(true);
        this.clientToolbar.removeNavigation(this.navButton);
        this.infoBoxManager.removeInfoBox(this.farmingContractManager.getInfoBox());
        this.farmingContractManager.setInfoBox(null);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        if (!e.getGroup().equals("timetracking")) {
            return;
        }
        if (this.clockManager.getTimers().isEmpty() && e.getKey().equals("timers")) {
            this.clockManager.loadTimers();
        } else if (this.clockManager.getStopwatches().isEmpty() && e.getKey().equals("stopwatches")) {
            this.clockManager.loadStopwatches();
        } else if (e.getKey().equals("preferSoonest")) {
            this.farmingTracker.loadCompletionTimes();
        }
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted commandExecuted) {
        if (commandExecuted.getCommand().equalsIgnoreCase("resetfarmtick")) {
            this.configManager.unsetRSProfileConfiguration("timetracking", "farmTickOffsetPrecision");
            this.configManager.unsetRSProfileConfiguration("timetracking", "farmTickOffset");
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged e) {
        if (e.getVarbitId() == 30231) {
            this.birdHouseTracker.setDonatorRank(this.client.getDonatorRank());
        }
    }

    @Subscribe
    public void onGameTick(GameTick t) {
        if (this.client.getGameState() != GameState.LOGGED_IN) {
            this.lastTickLocation = null;
            return;
        }
        Widget motd = this.client.getWidget(24772614);
        if (motd != null && !motd.isHidden()) {
            this.lastTickPostLogin = true;
            return;
        }
        if (this.lastTickPostLogin) {
            this.lastTickPostLogin = false;
            return;
        }
        WorldPoint loc = this.lastTickLocation;
        this.lastTickLocation = this.client.getLocalPlayer().getWorldLocation();
        if (loc == null || loc.getRegionID() != this.lastTickLocation.getRegionID()) {
            return;
        }
        boolean birdHouseDataChanged = this.birdHouseTracker.updateData(loc);
        boolean farmingDataChanged = this.farmingTracker.updateData(loc, this.client.getTickCount() - this.lastModalCloseTick);
        boolean farmingContractDataChanged = this.farmingContractManager.updateData(loc);
        if (birdHouseDataChanged || farmingDataChanged || farmingContractDataChanged) {
            this.panel.update();
        }
    }

    @Subscribe
    public void onRuneScapeProfileChanged(RuneScapeProfileChanged e) {
        this.farmingTracker.loadCompletionTimes();
        this.birdHouseTracker.loadFromConfig();
        this.farmingContractManager.loadContractFromConfig();
        this.panel.update();
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE || !event.getMessage().equals(CONTRACT_COMPLETED)) {
            return;
        }
        this.farmingContractManager.setContract(null);
    }

    @Subscribe
    private void onWidgetClosed(WidgetClosed ev) {
        if (ev.getModalMode() != 1) {
            this.lastModalCloseTick = this.client.getTickCount();
        }
    }

    private void checkCompletion() {
        boolean birdHouseDataChanged = this.birdHouseTracker.checkCompletion();
        if (birdHouseDataChanged) {
            this.panel.update();
        }
        this.farmingTracker.checkCompletion();
    }

    private void updatePanel() {
        long unitTime = Instant.now().toEpochMilli() / 200L;
        boolean clockDataChanged = false;
        boolean timerOrderChanged = false;
        if (unitTime % 5L == 0L) {
            clockDataChanged = this.clockManager.checkCompletion();
            timerOrderChanged = this.clockManager.checkTimerOrder();
            this.clockManager.checkForWarnings();
        }
        if (unitTime % (long)this.panel.getUpdateInterval() == 0L || clockDataChanged || timerOrderChanged) {
            this.panel.update();
        }
    }
}

