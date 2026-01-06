/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import java.util.Collections;
import java.util.Set;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.SkillBonus;

public interface SkillAction {
    public String getName(ItemManager var1);

    public int getLevel();

    public float getXp();

    default public int getIcon() {
        return -1;
    }

    default public int getSprite() {
        return -1;
    }

    default public boolean isBonusApplicable(SkillBonus bonus) {
        return !this.getExcludedSkillBonuses().contains(bonus);
    }

    public boolean isMembers(ItemManager var1);

    default public Set<? extends SkillBonus> getExcludedSkillBonuses() {
        return Collections.emptySet();
    }
}

