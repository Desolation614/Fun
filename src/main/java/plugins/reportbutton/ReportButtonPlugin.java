/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.GameState
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.widgets.Widget
 *  org.apache.commons.lang3.time.DurationFormatUtils
 */
package net.runelite.client.plugins.reportbutton;

import com.google.inject.Provides;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.reportbutton.ReportButtonConfig;
import net.runelite.client.plugins.reportbutton.TimeFormat;
import net.runelite.client.plugins.reportbutton.TimeStyle;
import net.runelite.client.task.Schedule;
import org.apache.commons.lang3.time.DurationFormatUtils;

@PluginDescriptor(name="Report Button", description="Replace the text on the Report button with the current time", tags={"time", "utc", "clock"})
public class ReportButtonPlugin
extends Plugin {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId JAGEX = ZoneId.of("Europe/London");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM. dd, yyyy");
    private DateTimeFormatter timeFormat;
    private Instant loginTime;
    private int ticksSinceLogin;
    private boolean ready;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ReportButtonConfig config;

    @Provides
    ReportButtonConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ReportButtonConfig.class);
    }

    @Override
    public void startUp() {
        this.clientThread.invoke(this::updateReportButtonTime);
        this.updateTimeFormat();
    }

    @Override
    public void shutDown() {
        this.clientThread.invoke(() -> {
            Widget reportButton = this.client.getWidget(10616865);
            if (reportButton != null) {
                reportButton.setText("Report");
            }
        });
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState state = event.getGameState();
        switch (state) {
            case LOGGING_IN: 
            case HOPPING: {
                this.ready = true;
                break;
            }
            case LOGGED_IN: {
                if (!this.ready) break;
                this.loginTime = Instant.now();
                this.ticksSinceLogin = 0;
                this.ready = false;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        ++this.ticksSinceLogin;
        if (this.config.time() == TimeStyle.GAME_TICKS) {
            this.updateReportButtonTime();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("reportButton") && event.getKey().equals("switchTimeFormat")) {
            this.updateTimeFormat();
        }
    }

    @Schedule(period=500L, unit=ChronoUnit.MILLIS)
    public void updateSchedule() {
        this.updateReportButtonTime();
    }

    private void updateReportButtonTime() {
        if (this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        Widget reportButton = this.client.getWidget(10616865);
        if (reportButton == null) {
            return;
        }
        switch (this.config.time()) {
            case UTC: {
                reportButton.setText(this.getUTCTime());
                break;
            }
            case JAGEX: {
                reportButton.setText(this.getJagexTime());
                break;
            }
            case LOCAL_TIME: {
                reportButton.setText(this.getLocalTime());
                break;
            }
            case LOGIN_TIME: {
                reportButton.setText(this.getLoginTime());
                break;
            }
            case IDLE_TIME: {
                reportButton.setText(this.getIdleTime());
                break;
            }
            case DATE: {
                reportButton.setText(ReportButtonPlugin.getDate());
                break;
            }
            case GAME_TICKS: {
                reportButton.setText(this.getGameTicks());
            }
        }
    }

    private String getIdleTime() {
        long lastActivity = Long.min(this.client.getMouseIdleTicks(), this.client.getKeyboardIdleTicks());
        return DurationFormatUtils.formatDuration((long)(lastActivity * 20L), (String)"mm:ss");
    }

    private String getLoginTime() {
        if (this.loginTime == null) {
            return "Report";
        }
        Duration duration = Duration.between(this.loginTime, Instant.now());
        LocalTime time = LocalTime.ofSecondOfDay(duration.getSeconds());
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private String getGameTicks() {
        return Integer.toString(this.ticksSinceLogin);
    }

    private String getLocalTime() {
        return LocalTime.now().format(this.timeFormat);
    }

    private String getUTCTime() {
        LocalTime time = LocalTime.now(UTC);
        return time.format(this.timeFormat);
    }

    private String getJagexTime() {
        LocalTime time = LocalTime.now(JAGEX);
        return time.format(this.timeFormat);
    }

    private static String getDate() {
        return DATE_FORMAT.format(new Date());
    }

    private void updateTimeFormat() {
        this.timeFormat = this.config.switchTimeFormat() == TimeFormat.TIME_24H ? DateTimeFormatter.ofPattern("HH:mm:ss") : DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
    }
}

