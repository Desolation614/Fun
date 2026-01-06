/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 */
package net.runelite.client.plugins.achievementdiary;

import net.runelite.api.Client;
import net.runelite.client.plugins.achievementdiary.Requirement;

public class CombatLevelRequirement
implements Requirement {
    private final int level;

    public String toString() {
        return this.level + " Combat";
    }

    @Override
    public boolean satisfiesRequirement(Client client) {
        return client.getLocalPlayer() != null && client.getLocalPlayer().getCombatLevel() >= this.level;
    }

    public CombatLevelRequirement(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}

