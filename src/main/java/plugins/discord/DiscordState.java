/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ComparisonChain
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.discord;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ComparisonChain;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import net.runelite.client.discord.DiscordPresence;
import net.runelite.client.discord.DiscordService;
import net.runelite.client.plugins.discord.DiscordConfig;
import net.runelite.client.plugins.discord.DiscordGameEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DiscordState {
    private static final Logger log = LoggerFactory.getLogger(DiscordState.class);
    private final List<EventWithTime> events = new ArrayList<EventWithTime>();
    private final DiscordService discordService;
    private final DiscordConfig config;
    private final String runeliteTitle;
    private final String runeliteVersion;
    private DiscordPresence lastPresence;
    private final boolean safeMode;

    @Inject
    private DiscordState(DiscordService discordService, DiscordConfig config, @Named(value="runelite.title") String runeliteTitle, @Named(value="runelite.version") String runeliteVersion, @Named(value="safeMode") boolean safeMode) {
        this.discordService = discordService;
        this.config = config;
        this.runeliteTitle = runeliteTitle;
        this.runeliteVersion = runeliteVersion;
        this.safeMode = safeMode;
    }

    void reset() {
        this.discordService.clearPresence();
        this.events.clear();
        this.lastPresence = null;
    }

    void triggerEvent(DiscordGameEventType eventType) {
        EventWithTime event;
        Optional<EventWithTime> foundEvent = this.events.stream().filter(e -> e.type == eventType).findFirst();
        if (foundEvent.isPresent()) {
            event = foundEvent.get();
        } else {
            event = new EventWithTime(eventType);
            event.setStart(Instant.now());
            this.events.add(event);
        }
        event.setUpdated(Instant.now());
        if (event.getType().isShouldClear()) {
            this.events.removeIf(e -> e.getType() != eventType && e.getType().isShouldBeCleared());
        }
        if (event.getType().isShouldRestart()) {
            event.setStart(Instant.now());
        }
        this.events.sort((a, b) -> ComparisonChain.start().compare(b.getType().getPriority(), a.getType().getPriority()).compare((Comparable)b.getUpdated(), (Comparable)a.getUpdated()).result());
        log.debug("Events: {}", this.events);
        this.updatePresenceWithLatestEvent();
    }

    private void updatePresenceWithLatestEvent() {
        Instant startTime;
        if (this.events.isEmpty()) {
            this.reset();
            return;
        }
        EventWithTime event = this.events.get(0);
        String imageKey = null;
        String state = null;
        String details = null;
        for (EventWithTime eventWithTime : this.events) {
            if (imageKey == null) {
                imageKey = eventWithTime.getType().getImageKey();
            }
            if (details == null) {
                details = eventWithTime.getType().getDetails();
            }
            if (state == null) {
                state = eventWithTime.getType().getState();
            }
            if (imageKey == null || details == null || state == null) continue;
            break;
        }
        String versionShortHand = this.runeliteVersion.replace("-SNAPSHOT", "+");
        StringBuilder largeImageTooltipText = new StringBuilder(this.runeliteTitle).append(" v").append(versionShortHand);
        if (this.safeMode) {
            largeImageTooltipText.append(" (safe mode)");
        }
        DiscordPresence.DiscordPresenceBuilder presenceBuilder = DiscordPresence.builder().state((String)MoreObjects.firstNonNull((Object)state, (Object)"")).details((String)MoreObjects.firstNonNull((Object)details, (Object)"")).largeImageText(largeImageTooltipText.toString()).smallImageKey(imageKey);
        switch (this.config.elapsedTimeType()) {
            case HIDDEN: {
                startTime = null;
                break;
            }
            case TOTAL: {
                startTime = this.events.stream().map(EventWithTime::getStart).min(Instant::compareTo).get();
                break;
            }
            default: {
                startTime = event.getStart();
            }
        }
        presenceBuilder.startTimestamp(startTime);
        DiscordPresence presence = presenceBuilder.build();
        if (!presence.equals(this.lastPresence)) {
            this.lastPresence = presence;
            this.discordService.updatePresence(presence);
        }
    }

    void checkForTimeout() {
        if (this.events.isEmpty()) {
            return;
        }
        Duration actionTimeout = Duration.ofMinutes(this.config.actionTimeout());
        Instant now = Instant.now();
        boolean removedAny = this.events.removeAll(this.events.stream().filter(event -> event.getType().isShouldBeCleared()).filter(event -> event.getType().isShouldTimeout() && now.isAfter(event.getUpdated().plus(actionTimeout))).collect(Collectors.toList()));
        if (removedAny) {
            this.updatePresenceWithLatestEvent();
        }
    }

    private static class EventWithTime {
        private final DiscordGameEventType type;
        private Instant start;
        private Instant updated;

        public EventWithTime(DiscordGameEventType type) {
            this.type = type;
        }

        public DiscordGameEventType getType() {
            return this.type;
        }

        public Instant getStart() {
            return this.start;
        }

        public Instant getUpdated() {
            return this.updated;
        }

        public void setStart(Instant start) {
            this.start = start;
        }

        public void setUpdated(Instant updated) {
            this.updated = updated;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof EventWithTime)) {
                return false;
            }
            EventWithTime other = (EventWithTime)o;
            if (!other.canEqual(this)) {
                return false;
            }
            DiscordGameEventType this$type = this.getType();
            DiscordGameEventType other$type = other.getType();
            if (this$type == null ? other$type != null : !((Object)((Object)this$type)).equals((Object)other$type)) {
                return false;
            }
            Instant this$start = this.getStart();
            Instant other$start = other.getStart();
            if (this$start == null ? other$start != null : !((Object)this$start).equals(other$start)) {
                return false;
            }
            Instant this$updated = this.getUpdated();
            Instant other$updated = other.getUpdated();
            return !(this$updated == null ? other$updated != null : !((Object)this$updated).equals(other$updated));
        }

        protected boolean canEqual(Object other) {
            return other instanceof EventWithTime;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            DiscordGameEventType $type = this.getType();
            result = result * 59 + ($type == null ? 43 : ((Object)((Object)$type)).hashCode());
            Instant $start = this.getStart();
            result = result * 59 + ($start == null ? 43 : ((Object)$start).hashCode());
            Instant $updated = this.getUpdated();
            result = result * 59 + ($updated == null ? 43 : ((Object)$updated).hashCode());
            return result;
        }

        public String toString() {
            return "DiscordState.EventWithTime(type=" + String.valueOf((Object)this.getType()) + ", start=" + String.valueOf(this.getStart()) + ", updated=" + String.valueOf(this.getUpdated()) + ")";
        }
    }
}

