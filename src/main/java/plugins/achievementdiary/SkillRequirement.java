/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.achievementdiary;

import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.plugins.achievementdiary.Requirement;

public class SkillRequirement
implements Requirement {
    private final Skill skill;
    private final int level;

    public String toString() {
        return this.level + " " + this.skill.getName();
    }

    @Override
    public boolean satisfiesRequirement(Client client) {
        return client.getRealSkillLevel(this.skill) >= this.level;
    }

    public SkillRequirement(Skill skill, int level) {
        this.skill = skill;
        this.level = level;
    }

    public Skill getSkill() {
        return this.skill;
    }

    public int getLevel() {
        return this.level;
    }
}

