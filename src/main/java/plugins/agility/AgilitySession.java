/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.EvictingQueue
 *  net.runelite.api.Client
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.agility;

import com.google.common.collect.EvictingQueue;
import java.time.Duration;
import java.time.Instant;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.plugins.agility.Courses;
import net.runelite.client.plugins.xptracker.XpTrackerService;

class AgilitySession {
    private final Courses course;
    private Instant lastLapCompleted;
    private int totalLaps;
    private int lapsTillGoal;
    private final EvictingQueue<Duration> lastLapTimes = EvictingQueue.create((int)30);
    private int lapsPerHour;

    AgilitySession(Courses course) {
        this.course = course;
    }

    void incrementLapCount(Client client, XpTrackerService xpTrackerService) {
        this.calculateLapsPerHour();
        ++this.totalLaps;
        int currentExp = client.getSkillExperience(Skill.AGILITY);
        int goalXp = xpTrackerService.getEndGoalXp(Skill.AGILITY);
        int goalRemainingXp = goalXp - currentExp;
        double courseTotalExp = this.course.getTotalXpProvider().apply(client);
        if (this.course == Courses.PYRAMID) {
            courseTotalExp += (double)Math.min(300 + 8 * client.getRealSkillLevel(Skill.AGILITY), 1000);
        }
        this.lapsTillGoal = goalRemainingXp > 0 ? (int)Math.ceil((double)goalRemainingXp / courseTotalExp) : 0;
    }

    void calculateLapsPerHour() {
        Duration timeSinceLastLap;
        Instant now = Instant.now();
        if (this.lastLapCompleted != null && !(timeSinceLastLap = Duration.between(this.lastLapCompleted, now)).isNegative()) {
            this.lastLapTimes.add((Object)timeSinceLastLap);
            Duration sum = Duration.ZERO;
            for (Duration lapTime : this.lastLapTimes) {
                sum = sum.plus(lapTime);
            }
            Duration averageLapTime = sum.dividedBy(this.lastLapTimes.size());
            this.lapsPerHour = (int)(Duration.ofHours(1L).toMillis() / averageLapTime.toMillis());
        }
        this.lastLapCompleted = now;
    }

    public Courses getCourse() {
        return this.course;
    }

    public Instant getLastLapCompleted() {
        return this.lastLapCompleted;
    }

    public int getTotalLaps() {
        return this.totalLaps;
    }

    public int getLapsTillGoal() {
        return this.lapsTillGoal;
    }

    public EvictingQueue<Duration> getLastLapTimes() {
        return this.lastLapTimes;
    }

    public int getLapsPerHour() {
        return this.lapsPerHour;
    }

    public void setLastLapCompleted(Instant lastLapCompleted) {
        this.lastLapCompleted = lastLapCompleted;
    }

    public void setTotalLaps(int totalLaps) {
        this.totalLaps = totalLaps;
    }

    public void setLapsTillGoal(int lapsTillGoal) {
        this.lapsTillGoal = lapsTillGoal;
    }

    public void setLapsPerHour(int lapsPerHour) {
        this.lapsPerHour = lapsPerHour;
    }
}

