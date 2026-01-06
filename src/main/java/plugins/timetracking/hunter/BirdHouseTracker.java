/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.coords.WorldPoint
 *  net.runelite.api.vars.DonatorRank
 */
package net.runelite.client.plugins.timetracking.hunter;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.vars.DonatorRank;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseData;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseSpace;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseState;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseTabPanel;

@Singleton
public class BirdHouseTracker {
    static final int BIRD_HOUSE_DURATION = (int)Duration.ofMinutes(50L).getSeconds();
    private static ImmutableSet<Integer> FOSSIL_ISLAND_REGIONS = ImmutableSet.of((Object)14650, (Object)14651, (Object)14652, (Object)14906, (Object)14907, (Object)15162, (Object[])new Integer[]{15163});
    private final Client client;
    private final ItemManager itemManager;
    private final ConfigManager configManager;
    private final TimeTrackingConfig config;
    private final Notifier notifier;
    private final ConcurrentMap<BirdHouseSpace, BirdHouseData> birdHouseData = new ConcurrentHashMap<BirdHouseSpace, BirdHouseData>();
    private SummaryState summary = SummaryState.UNKNOWN;
    private long completionTime = -1L;
    private DonatorRank donatorRank;

    @Inject
    private BirdHouseTracker(Client client, ItemManager itemManager, ConfigManager configManager, TimeTrackingConfig config, Notifier notifier) {
        this.client = client;
        this.itemManager = itemManager;
        this.configManager = configManager;
        this.config = config;
        this.notifier = notifier;
    }

    public BirdHouseTabPanel createBirdHouseTabPanel() {
        return new BirdHouseTabPanel(this.configManager, this.itemManager, this, this.config);
    }

    public void loadFromConfig() {
        this.birdHouseData.clear();
        for (BirdHouseSpace space : BirdHouseSpace.values()) {
            String[] parts;
            String key = "birdhouse." + space.getVarp();
            String storedValue = this.configManager.getRSProfileConfiguration("timetracking", key);
            if (storedValue == null || (parts = storedValue.split(":")).length != 2) continue;
            try {
                int varp = Integer.parseInt(parts[0]);
                long timestamp = Long.parseLong(parts[1]);
                this.birdHouseData.put(space, new BirdHouseData(space, varp, timestamp));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        this.updateCompletionTime();
    }

    public boolean updateData(WorldPoint location) {
        boolean changed = false;
        if (FOSSIL_ISLAND_REGIONS.contains((Object)location.getRegionID()) && location.getPlane() == 0) {
            HashMap<BirdHouseSpace, BirdHouseData> newData = new HashMap<BirdHouseSpace, BirdHouseData>();
            long currentTime = Instant.now().getEpochSecond();
            int removalCount = 0;
            for (BirdHouseSpace space : BirdHouseSpace.values()) {
                int oldVarp;
                int varp = this.client.getVarpValue(space.getVarp());
                BirdHouseData oldData = (BirdHouseData)this.birdHouseData.get((Object)space);
                int n = oldVarp = oldData == null ? -1 : oldData.getVarp();
                if (varp != oldVarp) {
                    newData.put(space, new BirdHouseData(space, varp, currentTime));
                    changed = true;
                }
                if (varp > 0 || oldVarp <= 0) continue;
                ++removalCount;
            }
            if (removalCount > 2) {
                return false;
            }
            if (changed) {
                this.birdHouseData.putAll(newData);
                this.updateCompletionTime();
                this.saveToConfig(newData);
            }
        }
        return changed;
    }

    public boolean checkCompletion() {
        if (this.summary == SummaryState.IN_PROGRESS && this.completionTime < Instant.now().getEpochSecond()) {
            this.summary = SummaryState.COMPLETED;
            this.completionTime = 0L;
            if (Boolean.TRUE.equals(this.configManager.getRSProfileConfiguration("timetracking", "birdHouseNotification", Boolean.TYPE))) {
                this.notifier.notify("Your bird houses are ready to be dismantled.");
            }
            return true;
        }
        return false;
    }

    private void updateCompletionTime() {
        if (this.birdHouseData.isEmpty()) {
            this.summary = SummaryState.UNKNOWN;
            this.completionTime = -1L;
            return;
        }
        boolean allEmpty = true;
        long maxCompletionTime = 0L;
        for (BirdHouseData data : this.birdHouseData.values()) {
            BirdHouseState state = BirdHouseState.fromVarpValue(data.getVarp());
            if (state != BirdHouseState.EMPTY) {
                allEmpty = false;
            }
            if (state != BirdHouseState.SEEDED) continue;
            maxCompletionTime = Math.max(maxCompletionTime, data.getTimestamp() + (long)this.getHouseDuration());
        }
        if (allEmpty) {
            this.summary = SummaryState.EMPTY;
            this.completionTime = 0L;
        } else if (maxCompletionTime <= Instant.now().getEpochSecond()) {
            this.summary = SummaryState.COMPLETED;
            this.completionTime = 0L;
        } else {
            this.summary = SummaryState.IN_PROGRESS;
            this.completionTime = maxCompletionTime;
        }
    }

    public int getHouseDuration() {
        if (this.donatorRank == null) {
            return BIRD_HOUSE_DURATION;
        }
        switch (this.donatorRank) {
            case REGULAR: {
                return (int)Duration.ofMinutes(35L).getSeconds();
            }
            case SUPER_DONATOR: {
                return (int)Duration.ofMinutes(30L).getSeconds();
            }
            case EXTREME_DONATOR: {
                return (int)Duration.ofMinutes(25L).getSeconds();
            }
            case LEGENDARY_DONATOR: {
                return (int)Duration.ofMinutes(20L).getSeconds();
            }
            case ELITE_DONATOR: {
                return (int)Duration.ofMinutes(15L).getSeconds();
            }
            case UBER_DONATOR: {
                return (int)Duration.ofMinutes(10L).getSeconds();
            }
            case SUPREME_DONATOR: {
                return (int)Duration.ofMinutes(5L).getSeconds();
            }
        }
        return BIRD_HOUSE_DURATION;
    }

    private void saveToConfig(Map<BirdHouseSpace, BirdHouseData> updatedData) {
        for (BirdHouseData data : updatedData.values()) {
            String key = "birdhouse." + data.getSpace().getVarp();
            this.configManager.setRSProfileConfiguration("timetracking", key, data.getVarp() + ":" + data.getTimestamp());
        }
    }

    ConcurrentMap<BirdHouseSpace, BirdHouseData> getBirdHouseData() {
        return this.birdHouseData;
    }

    public SummaryState getSummary() {
        return this.summary;
    }

    public long getCompletionTime() {
        return this.completionTime;
    }

    public void setDonatorRank(DonatorRank donatorRank) {
        this.donatorRank = donatorRank;
    }
}

