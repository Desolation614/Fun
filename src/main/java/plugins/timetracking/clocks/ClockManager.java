/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Comparators
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  com.google.inject.Singleton
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.timetracking.clocks;

import com.google.common.base.Strings;
import com.google.common.collect.Comparators;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.timetracking.SortOrder;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.clocks.Clock;
import net.runelite.client.plugins.timetracking.clocks.ClockTabPanel;
import net.runelite.client.plugins.timetracking.clocks.Stopwatch;
import net.runelite.client.plugins.timetracking.clocks.Timer;

@Singleton
public class ClockManager {
    @Inject
    private ConfigManager configManager;
    @Inject
    private TimeTrackingConfig config;
    @Inject
    private Notifier notifier;
    @Inject
    private Gson gson;
    private final List<Timer> timers = new CopyOnWriteArrayList<Timer>();
    private final List<Stopwatch> stopwatches = new ArrayList<Stopwatch>();
    private final ClockTabPanel clockTabPanel = new ClockTabPanel(this);

    void addTimer() {
        this.timers.add(new Timer("Timer " + (this.timers.size() + 1), this.config.defaultTimerMinutes() * 60));
        this.saveTimers();
        SwingUtilities.invokeLater(this.clockTabPanel::rebuild);
    }

    void addStopwatch() {
        this.stopwatches.add(new Stopwatch("Stopwatch " + (this.stopwatches.size() + 1)));
        this.saveStopwatches();
        SwingUtilities.invokeLater(this.clockTabPanel::rebuild);
    }

    void removeTimer(Timer timer) {
        this.timers.remove(timer);
        this.saveTimers();
        SwingUtilities.invokeLater(this.clockTabPanel::rebuild);
    }

    void removeStopwatch(Stopwatch stopwatch) {
        this.stopwatches.remove(stopwatch);
        this.saveStopwatches();
        SwingUtilities.invokeLater(this.clockTabPanel::rebuild);
    }

    public long getActiveTimerCount() {
        return this.timers.stream().filter(Clock::isActive).count();
    }

    public long getActiveStopwatchCount() {
        return this.stopwatches.stream().filter(Clock::isActive).count();
    }

    public boolean checkCompletion() {
        boolean changed = false;
        for (Timer timer : this.timers) {
            if (!timer.isActive() || timer.getDisplayTime() != 0L) continue;
            timer.pause();
            changed = true;
            this.notifier.notify(this.config.timerNotification(), "[" + timer.getName() + "] has finished counting down.");
            if (!timer.isLoop()) continue;
            timer.start();
        }
        if (changed) {
            this.saveTimers();
            SwingUtilities.invokeLater(this.clockTabPanel::rebuild);
        }
        return changed;
    }

    public boolean checkTimerOrder() {
        SortOrder sortOrder = this.config.sortOrder();
        if (sortOrder != SortOrder.NONE) {
            Comparator<Timer> comparator = Comparator.comparingLong(Timer::getDisplayTime);
            if (sortOrder == SortOrder.DESC) {
                comparator = comparator.reversed();
            }
            if (!Comparators.isInOrder(this.timers, comparator)) {
                this.timers.sort(comparator);
                SwingUtilities.invokeLater(this.clockTabPanel::rebuild);
                return true;
            }
        }
        return false;
    }

    public void checkForWarnings() {
        for (Timer timer : this.timers) {
            timer.setWarning(timer.getDisplayTime() <= (long)this.config.timerWarningThreshold());
        }
    }

    public void loadTimers() {
        String timersJson = this.configManager.getConfiguration("timetracking", "timers");
        if (!Strings.isNullOrEmpty((String)timersJson)) {
            List timers = (List)this.gson.fromJson(timersJson, new TypeToken<ArrayList<Timer>>(){}.getType());
            this.timers.clear();
            this.timers.addAll(timers);
            SwingUtilities.invokeLater(this.clockTabPanel::rebuild);
        }
    }

    public void loadStopwatches() {
        String stopwatchesJson = this.configManager.getConfiguration("timetracking", "stopwatches");
        if (!Strings.isNullOrEmpty((String)stopwatchesJson)) {
            List stopwatches = (List)this.gson.fromJson(stopwatchesJson, new TypeToken<ArrayList<Stopwatch>>(){}.getType());
            this.stopwatches.clear();
            this.stopwatches.addAll(stopwatches);
            SwingUtilities.invokeLater(this.clockTabPanel::rebuild);
        }
    }

    public void clear() {
        this.timers.clear();
        this.stopwatches.clear();
        SwingUtilities.invokeLater(this.clockTabPanel::rebuild);
    }

    void saveToConfig() {
        this.saveTimers();
        this.saveStopwatches();
    }

    void saveTimers() {
        String json = this.gson.toJson(this.timers);
        this.configManager.setConfiguration("timetracking", "timers", json);
    }

    void saveStopwatches() {
        String json = this.gson.toJson(this.stopwatches);
        this.configManager.setConfiguration("timetracking", "stopwatches", json);
    }

    public List<Timer> getTimers() {
        return this.timers;
    }

    public List<Stopwatch> getStopwatches() {
        return this.stopwatches;
    }

    public ClockTabPanel getClockTabPanel() {
        return this.clockTabPanel;
    }
}

