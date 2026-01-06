/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.Quest
 *  net.runelite.api.QuestState
 */
package net.runelite.client.plugins.achievementdiary;

import net.runelite.api.Client;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.client.plugins.achievementdiary.Requirement;

public class QuestRequirement
implements Requirement {
    private final Quest quest;
    private final boolean started;

    public QuestRequirement(Quest quest) {
        this(quest, false);
    }

    public String toString() {
        if (this.started) {
            return "Started " + this.quest.getName();
        }
        return this.quest.getName();
    }

    @Override
    public boolean satisfiesRequirement(Client client) {
        QuestState questState = this.quest.getState(client);
        if (this.started) {
            return questState != QuestState.NOT_STARTED;
        }
        return questState == QuestState.FINISHED;
    }

    public Quest getQuest() {
        return this.quest;
    }

    public boolean isStarted() {
        return this.started;
    }

    public QuestRequirement(Quest quest, boolean started) {
        this.quest = quest;
        this.started = started;
    }
}

