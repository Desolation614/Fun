/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Skill
 */
package net.runelite.client.ui;

import java.awt.Color;
import net.runelite.api.Skill;

public enum SkillColor {
    ATTACK(155, 32, 7),
    DEFENCE(98, 119, 190),
    STRENGTH(4, 149, 90),
    HITPOINTS(131, 126, 126),
    RANGED(109, 144, 23),
    PRAYER(159, 147, 35),
    MAGIC(50, 80, 193),
    COOKING(112, 35, 134),
    WOODCUTTING(52, 140, 37),
    FLETCHING(3, 141, 125),
    FISHING(106, 132, 164),
    FIREMAKING(189, 120, 25),
    CRAFTING(151, 110, 77),
    SMITHING(108, 107, 82),
    MINING(93, 143, 167),
    HERBLORE(7, 133, 9),
    AGILITY(58, 60, 137),
    THIEVING(108, 52, 87),
    SLAYER(100, 100, 100),
    FARMING(101, 152, 63),
    RUNECRAFT(170, 141, 26),
    HUNTER(92, 89, 65),
    CONSTRUCTION(130, 116, 95);

    private final Color color;

    private SkillColor(int red, int green, int blue) {
        this.color = new Color(red, green, blue);
    }

    public static SkillColor find(Skill skill) {
        return SkillColor.values()[skill.ordinal()];
    }

    public Color getColor() {
        return this.color;
    }
}

