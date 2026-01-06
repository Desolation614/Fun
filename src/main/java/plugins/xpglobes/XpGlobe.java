/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.xpglobes;

import java.awt.image.BufferedImage;
import java.time.Instant;
import net.runelite.api.Skill;

class XpGlobe {
    private Skill skill;
    private int currentXp;
    private int currentLevel;
    private Instant time;
    private int size;
    private BufferedImage skillIcon;

    XpGlobe(Skill skill, int currentXp, int currentLevel, Instant time) {
        this.skill = skill;
        this.currentXp = currentXp;
        this.currentLevel = currentLevel;
        this.time = time;
    }

    public Skill getSkill() {
        return this.skill;
    }

    public int getCurrentXp() {
        return this.currentXp;
    }

    public int getCurrentLevel() {
        return this.currentLevel;
    }

    public Instant getTime() {
        return this.time;
    }

    public int getSize() {
        return this.size;
    }

    public BufferedImage getSkillIcon() {
        return this.skillIcon;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void setCurrentXp(int currentXp) {
        this.currentXp = currentXp;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSkillIcon(BufferedImage skillIcon) {
        this.skillIcon = skillIcon;
    }
}

