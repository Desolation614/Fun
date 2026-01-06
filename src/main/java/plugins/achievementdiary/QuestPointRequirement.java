/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.achievementdiary;

import net.runelite.api.Client;
import net.runelite.client.plugins.achievementdiary.Requirement;

public class QuestPointRequirement
implements Requirement {
    private final int qp;

    public String toString() {
        return this.qp + " Quest points";
    }

    @Override
    public boolean satisfiesRequirement(Client client) {
        return client.getVarpValue(101) >= this.qp;
    }

    public QuestPointRequirement(int qp) {
        this.qp = qp;
    }

    public int getQp() {
        return this.qp;
    }
}

