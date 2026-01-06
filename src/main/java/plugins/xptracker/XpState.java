/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  lombok.NonNull
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.xptracker;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.NonNull;
import net.runelite.api.Skill;
import net.runelite.client.plugins.xptracker.XpSave;
import net.runelite.client.plugins.xptracker.XpSaveSingle;
import net.runelite.client.plugins.xptracker.XpSnapshotSingle;
import net.runelite.client.plugins.xptracker.XpStateSingle;
import net.runelite.client.plugins.xptracker.XpTrackerConfig;
import net.runelite.client.plugins.xptracker.XpUpdateResult;

class XpState {
    private final Map<Skill, XpStateSingle> xpSkills = new EnumMap<Skill, XpStateSingle>(Skill.class);
    private final List<Skill> order = new ArrayList<Skill>(Skill.values().length);
    private XpStateSingle overall = new XpStateSingle(-1L);
    @Inject
    private XpTrackerConfig xpTrackerConfig;

    XpState() {
    }

    void reset() {
        this.xpSkills.clear();
        this.order.clear();
        this.overall = new XpStateSingle(-1L);
    }

    void resetSkillPerHour(Skill skill) {
        this.xpSkills.get(skill).resetPerHour();
    }

    void resetOverallPerHour() {
        this.overall.resetPerHour();
    }

    XpUpdateResult updateSkill(Skill skill, long currentXp, int goalStartXp, int goalEndXp) {
        int gainedXp;
        XpStateSingle state = this.xpSkills.get(skill);
        if (state == null || state.getStartXp() == -1L) {
            assert (currentXp >= 0L);
            this.initializeSkill(skill, currentXp);
            return XpUpdateResult.INITIALIZED;
        }
        long startXp = state.getStartXp();
        if (startXp + (long)(gainedXp = state.getTotalXpGained()) > currentXp) {
            this.initializeSkill(skill, currentXp);
            return XpUpdateResult.INITIALIZED;
        }
        if (!state.update(currentXp)) {
            return XpUpdateResult.NO_CHANGE;
        }
        state.updateGoals(currentXp, goalStartXp, goalEndXp);
        this.updateOrder(skill);
        return XpUpdateResult.UPDATED;
    }

    void updateOverall(long currentXp) {
        if (this.overall == null || this.overall.getStartXp() + (long)this.overall.getTotalXpGained() > currentXp) {
            this.overall = new XpStateSingle(currentXp);
        } else {
            this.overall.update(currentXp);
        }
    }

    void tick(Skill skill, long delta) {
        XpStateSingle state = this.getSkill(skill);
        this.tick(state, delta);
    }

    void tickOverall(long delta) {
        this.tick(this.overall, delta);
    }

    private void tick(XpStateSingle state, long delta) {
        state.tick(delta);
        int resetAfterMinutes = this.xpTrackerConfig.resetSkillRateAfter();
        if (resetAfterMinutes > 0) {
            long now = System.currentTimeMillis();
            int resetAfterMillis = resetAfterMinutes * 60 * 1000;
            long lastChangeMillis = state.getLastChangeMillis();
            if (lastChangeMillis != 0L && now - lastChangeMillis >= (long)resetAfterMillis) {
                state.resetPerHour();
            }
        }
    }

    void initializeSkill(Skill skill, long currentXp) {
        this.xpSkills.put(skill, new XpStateSingle(currentXp));
    }

    void initializeOverall(long currentXp) {
        this.overall = new XpStateSingle(currentXp);
    }

    boolean isInitialized(Skill skill) {
        XpStateSingle xpStateSingle = this.xpSkills.get(skill);
        return xpStateSingle != null && xpStateSingle.getStartXp() != -1L;
    }

    boolean isOverallInitialized() {
        return this.overall.getStartXp() != -1L;
    }

    @NonNull
    XpStateSingle getSkill(Skill skill) {
        return this.xpSkills.computeIfAbsent(skill, s -> new XpStateSingle(-1L));
    }

    @NonNull
    XpSnapshotSingle getSkillSnapshot(Skill skill) {
        return this.getSkill(skill).snapshot();
    }

    @NonNull
    XpSnapshotSingle getTotalSnapshot() {
        return this.overall.snapshot();
    }

    void setCompactView(Skill skill, boolean compactView) {
        this.xpSkills.get(skill).setCompactView(compactView);
    }

    void setOrder(Skill skill, int newPosition) {
        int oldPosition = this.order.indexOf(skill);
        if (oldPosition != newPosition) {
            this.order.remove(oldPosition);
            this.order.add(newPosition, skill);
        }
    }

    private void updateOrder(Skill skill) {
        if (this.xpTrackerConfig.prioritizeRecentXpSkills()) {
            int idx = this.order.indexOf(skill);
            if (idx != 0) {
                this.order.remove(skill);
                this.order.add(0, skill);
            }
        } else if (!this.order.contains(skill)) {
            this.order.add(skill);
        }
    }

    XpSave save() {
        if (this.overall.getStartXp() == -1L) {
            return null;
        }
        XpSave save = new XpSave();
        for (Skill skill : this.order) {
            XpStateSingle state = this.xpSkills.get(skill);
            if (state.getTotalXpGained() > 0) {
                save.skills.put(skill, state.save());
            }
            if (!state.isCompactView()) continue;
            save.compactViewSkills.add(skill);
        }
        save.overall = this.overall.save();
        return save;
    }

    void restore(XpSave save) {
        this.reset();
        for (Map.Entry<Skill, XpSaveSingle> entry : save.skills.entrySet()) {
            Skill skill = entry.getKey();
            XpSaveSingle s = entry.getValue();
            XpStateSingle state = new XpStateSingle(s.startXp);
            state.restore(s);
            this.xpSkills.put(skill, state);
            this.order.add(skill);
        }
        for (Skill skill : save.compactViewSkills) {
            XpStateSingle state = this.xpSkills.get(skill);
            if (state == null) continue;
            state.setCompactView(true);
        }
        this.overall.restore(save.overall);
    }
}

